package com.zzg.mybatis.generator.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.zzg.mybatis.generator.exception.TypeMatchingError;

/**
 * 
 * excel读取工具类
 * 
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class ImportExeclUtil {

	private static int totalRows = 0;// 总行数

	private static int totalCells = 0;// 总列数

	private static String errorInfo;// 错误信息

	/** 无参构造方法 */
	public ImportExeclUtil() {
	}

	public static int getTotalRows() {
		return totalRows;
	}

	public static int getTotalCells() {
		return totalCells;
	}

	public static String getErrorInfo() {
		return errorInfo;
	}

	/**
	 * 
	 * 根据流读取Excel文件
	 * 
	 * 
	 * @param inputStream
	 * @param isExcel2003
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public List<List<String>> read(InputStream inputStream, boolean isExcel2003) throws IOException {

		List<List<String>> dataLst = null;

		/** 根据版本选择创建Workbook的方式 */
		Workbook wb = null;

		if (isExcel2003) {
			wb = new HSSFWorkbook(inputStream);
		} else {
			wb = new XSSFWorkbook(inputStream);
		}
		dataLst = readDate(wb);

		return dataLst;
	}

	/**
	 * 
	 * 读取数据
	 * 
	 * @param wb
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	private List<List<String>> readDate(Workbook wb) {

		List<List<String>> dataLst = new ArrayList<List<String>>();

		/** 得到第一个shell */
		Sheet sheet = wb.getSheetAt(0);

		/** 得到Excel的行数 */
		totalRows = sheet.getPhysicalNumberOfRows();

		/** 得到Excel的列数 */
		if (totalRows >= 1 && sheet.getRow(0) != null) {
			totalCells = sheet.getRow(0).getPhysicalNumberOfCells();
		}

		/** 循环Excel的行 */
		for (int r = 0; r < totalRows; r++) {
			Row row = sheet.getRow(r);
			if (row == null) {
				continue;
			}

			List<String> rowLst = new ArrayList<String>();

			/** 循环Excel的列 */
			for (int c = 0; c < getTotalCells(); c++) {

				Cell cell = row.getCell(c);
				String cellValue = "";

				if (null != cell) {
					// 以下是判断数据的类型
					switch (cell.getCellType()) {
					case HSSFCell.CELL_TYPE_NUMERIC: // 数字
						cellValue = cell.getNumericCellValue() + "";
						break;

					case HSSFCell.CELL_TYPE_STRING: // 字符串
						cellValue = cell.getStringCellValue();
						break;

					case HSSFCell.CELL_TYPE_BOOLEAN: // Boolean
						cellValue = cell.getBooleanCellValue() + "";
						break;

					// 20170915 加入公式判断标准，公式和数字都可以导入
					case HSSFCell.CELL_TYPE_FORMULA: // 公式
						try {
							cellValue = cell.getStringCellValue();
						} catch (IllegalStateException e) {
							cellValue = String.valueOf(cell.getNumericCellValue());
						}
						break;

					case HSSFCell.CELL_TYPE_BLANK: // 空值
						cellValue = "";
						break;

					case HSSFCell.CELL_TYPE_ERROR: // 故障
						cellValue = "非法字符";
						break;

					default:
						cellValue = "未知类型";
						break;
					}
				}

				rowLst.add(cellValue);
			}

			/** 保存第r行的第c列 */
			dataLst.add(rowLst);
		}

		return dataLst;
	}

	/**
	 * 
	 * 按指定坐标读取实体数据 <按顺序放入带有注解的实体成员变量中>
	 * 
	 * @param wb
	 *            工作簿
	 * @param t
	 *            实体
	 * @param in
	 *            输入流
	 * @param integers
	 *            指定需要解析的坐标
	 * @return T 相应实体
	 * @throws IOException
	 * @throws Exception
	 * @see [类、类#方法、类#成员]
	 */
	@SuppressWarnings("unused")
	public static <T> T readDateT(Integer index, Workbook wb, T t, InputStream in, Integer[]... integers)
			throws IOException, Exception {
		// 获取该工作表中的第一个工作表
		Sheet sheet = wb.getSheetAt(index);

		// 成员变量的值
		Object entityMemberValue = "";

		// 所有成员变量
		Field[] fields = t.getClass().getDeclaredFields();
		// 列开始下标
		int startCell = 0;

		/** 循环出需要的成员 */
		for (int f = 0; f < fields.length; f++) {

			fields[f].setAccessible(true);
			String fieldName = fields[f].getName();
			boolean fieldHasAnno = fields[f].isAnnotationPresent(IsIgnore.class);
			// 没有忽略注解 填充字段数据
			if (!fieldHasAnno) {

				// 获取行和列
				int x = integers[startCell][0] - 1;
				int y = integers[startCell][1] - 1;

				Row row = sheet.getRow(x);
				Cell cell = row.getCell(y);

				if (row == null) {
					continue;
				}

				// Excel中解析的值
				String cellValue = getCellValue(cell);
				// 需要赋给成员变量的值
				entityMemberValue = getEntityMemberValue(entityMemberValue, fields, f, cellValue);
				// 赋值
				PropertyUtils.setProperty(t, fieldName, entityMemberValue);
				// 列的下标加1
				startCell++;
			}
		}

		return t;
	}

	/**
	 * 
	 * 读取列表数据 <按顺序放入带有注解的实体成员变量中>
	 * 
	 * @param wb
	 *            工作簿
	 * @param t
	 *            实体
	 * @param beginLine
	 *            开始行数
	 * @param totalcut
	 *            结束行数减去相应行数
	 * @return List<T> 实体列表
	 * @throws Exception
	 * @see [类、类#方法、类#成员]
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> readDateListT(Integer index, Workbook wb, T t, int beginLine, int totalcut)
			throws Exception {
		List<T> listt = new ArrayList<T>();

		/** 得到第index个shell */
		Sheet sheet = wb.getSheetAt(index);

		/** 得到Excel的行数 */
		totalRows = sheet.getPhysicalNumberOfRows();

		/** 得到Excel的列数 */
		if (totalRows >= 1 && sheet.getRow(0) != null) {
			totalCells = sheet.getRow(0).getPhysicalNumberOfCells();
		}

		/** 循环Excel的行 */
		outer: for (int r = beginLine - 1; r < totalRows - totalcut + beginLine - 1; r++) {
			Object newInstance = t.getClass().newInstance();
			Row row = sheet.getRow(r);
			if (row == null) {
				continue;
			}
			// 成员变量的值
			Object entityMemberValue = "";

			// 所有成员变量
			Field[] fields = t.getClass().getDeclaredFields();
			// 列开始下标
			int startCell = 0;

			for (int f = 0; f < fields.length; f++) {// 字段与表一一对应

				fields[f].setAccessible(true);
				String fieldName = fields[f].getName();
				if (!fieldName.equals("serialVersionUID")) {//防止有的实体继承了序列化接口 不映射该字段
					boolean fieldHasAnno = fields[f].isAnnotationPresent(IsIgnore.class);
					// 没有忽略注解 填充字段数据
					if (!fieldHasAnno) {

						Cell cell = row.getCell(startCell);
						String cellValue = getCellValue(cell);
						if (startCell == 0 && StringUtils.isBlank(cellValue)) {
							break outer;// 跳出多层循环
						}
						try {
							entityMemberValue = getEntityMemberValue(entityMemberValue, fields, f, cellValue);
						} catch (Exception e) {
							TypeMatchingError err= new TypeMatchingError();
							err.setFilename(fieldName);
							err.setFiletype(fields[f].getType().getName());
							err.setRealvaule(cellValue);
							throw err;
						}
						// 赋值
						fields[f].setAccessible(true);
						fields[f].set(newInstance, entityMemberValue);
					}
					// 列的下标加1
					startCell++;
				}

			}

			listt.add((T) newInstance);
		}

		return listt;
	}

	/**
	 * 读取表头内容
	 * 
	 * @param index
	 * @param wb
	 * @param readLine
	 * @return
	 */
	public static String[] readHead(Integer index, Workbook wb, int readLine) {
		/** 得到第index个shell */
		Sheet sheet = wb.getSheetAt(index);
		Row row = sheet.getRow(readLine);
		int cellNumber = row.getPhysicalNumberOfCells();
		List<String > list = new ArrayList<>();
		for (int i = 0; i < cellNumber; i++) {
			Cell cell = row.getCell(i);
			String cellValue = getCellValue(cell);
			if (StringUtils.isBlank(cellValue)) {
				break;// 跳出循环
			}
			list.add(cellValue);
		}
		return  list.toArray(new String[list.size()]);

	}

	/**
	 * 
	 * 根据Excel表格中的数据判断类型得到值
	 * 
	 * @param cell
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	private static String getCellValue(Cell cell) {
		String cellValue = "";

		if (null != cell) {
			// 以下是判断数据的类型
			switch (cell.getCellType()) {
			case HSSFCell.CELL_TYPE_NUMERIC: // 数字
				if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
					Date theDate = cell.getDateCellValue();
					SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd");
					cellValue = dff.format(theDate);
				} else {
					DecimalFormat df = new DecimalFormat("0");
					cellValue = df.format(cell.getNumericCellValue());
				}
				break;
			case HSSFCell.CELL_TYPE_STRING: // 字符串
				cellValue = cell.getStringCellValue();
				if (cellValue.contains("\n")) {
					cellValue = cellValue.replace("\n", "");
				}
				/*if (cellValue.contains(" ")) {
					cellValue = cellValue.replace(" ", "");
				}*/
				break;

			case HSSFCell.CELL_TYPE_BOOLEAN: // Boolean
				cellValue = cell.getBooleanCellValue() + "";
				break;

			case HSSFCell.CELL_TYPE_FORMULA: // 公式
				// cellValue = cell.getCellFormula() + "";
				try {
					cellValue = String.valueOf(cell.getNumericCellValue());
					if (cellValue.contains(".")) {
						cellValue = cellValue.substring(0, cellValue.length() - 2);
					}
				} catch (IllegalStateException e) {
					cellValue = cell.getStringCellValue();
				}
				break;

			case HSSFCell.CELL_TYPE_BLANK: // 空值
				cellValue = "";
				break;

			case HSSFCell.CELL_TYPE_ERROR: // 故障
				cellValue = "非法字符";
				break;

			default:
				cellValue = "未知类型";
				break;
			}

		}
		return cellValue;
	}
    public static void main(String[] args) {
		
	}
	/**
	 * 
	 * 根据实体成员变量的类型得到成员变量的值
	 * 
	 * @param realValue
	 * @param fields
	 * @param f
	 * @param cellValue
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	private static Object getEntityMemberValue(Object realValue, Field[] fields, int f, String cellValue) {
		String type = fields[f].getType().getName();
		switch (type) {
		case "char":
			byte[] bytes = cellValue.getBytes();
			StringBuffer stringBuffer = new StringBuffer();
			for (byte b : bytes) {
				stringBuffer.append(b);
			}
			realValue =stringBuffer.toString();
			break;
		case "java.lang.Character":
			byte[] byte1 = cellValue.getBytes();
			StringBuffer stringBuffer1 = new StringBuffer();
			for (byte b : byte1) {
				stringBuffer1.append(b);
			}
			realValue =stringBuffer1.toString();
			break;
		case "java.lang.String":
			realValue = cellValue;
			break;
		case "java.util.Date":
			realValue = StringUtils.isBlank(cellValue) ? null : DateUtil.strToDate(cellValue, DateUtil.YYYY_MM_DD);
			break;
		case "java.lang.Integer":
			realValue = StringUtils.isBlank(cellValue) ? null : Integer.valueOf(cellValue);
			// System.out.println(realValue);
			break;
		case "int":
			realValue = StringUtils.isBlank(cellValue) ? null : Integer.valueOf(cellValue);
			break;
		case "long":
			realValue = StringUtils.isBlank(cellValue) ? null : Long.valueOf(cellValue);
			break;
		case "float":
			realValue = StringUtils.isBlank(cellValue) ? null : Float.valueOf(cellValue);
			break;
		case "double":
			realValue = StringUtils.isBlank(cellValue) ? null : Double.valueOf(cellValue);
			break;
		case "java.lang.Double":
			realValue = StringUtils.isBlank(cellValue) ? null : Double.valueOf(cellValue);
			break;
		case "java.lang.Float":
			realValue = StringUtils.isBlank(cellValue) ? null : Float.valueOf(cellValue);
			break;
		case "java.lang.Long":
			realValue = StringUtils.isBlank(cellValue) ? null : Long.valueOf(cellValue);
			break;
		case "java.lang.Short":
			realValue = StringUtils.isBlank(cellValue) ? null : Short.valueOf(cellValue);
			break;
		case "java.math.BigDecimal":
			realValue = StringUtils.isBlank(cellValue) ? null : new BigDecimal(cellValue);
			break;
		default:
			break;
		}
		return realValue;
	}

	/**
	 * 
	 * 根据路径或文件名选择Excel版本
	 * 
	 * 
	 * @param filePathOrName
	 * @param in
	 * @return
	 * @throws IOException
	 * @see [类、类#方法、类#成员]
	 */
	public static Workbook chooseWorkbook(String filePathOrName, InputStream in) throws IOException {
		/** 根据版本选择创建Workbook的方式 */
		Workbook wb = null;
		boolean isExcel2003 = ExcelVersionUtil.isExcel2003(filePathOrName);

		if (isExcel2003) {
			wb = new HSSFWorkbook(in);
		} else {
			wb = new XSSFWorkbook(in);
		}

		return wb;
	}

	static class ExcelVersionUtil {

		/**
		 * 
		 * 是否是2003的excel，返回true是2003
		 * 
		 * 
		 * @param filePath
		 * @return
		 * @see [类、类#方法、类#成员]
		 */
		public static boolean isExcel2003(String filePath) {
			return filePath.matches("^.+\\.(?i)(xls)$");

		}

		/**
		 * 
		 * 是否是2007的excel，返回true是2007
		 * 
		 * 
		 * @param filePath
		 * @return
		 * @see [类、类#方法、类#成员]
		 */
		public static boolean isExcel2007(String filePath) {
			return filePath.matches("^.+\\.(?i)(xlsx)$");

		}

	}

	public static class DateUtil {

		// ======================日期格式化常量=====================//

		public static final String YYYY_MM_DDHHMMSS = "yyyy-MM-dd HH:mm:ss";

		public static final String YYYY_MM_DD = "yyyy-MM-dd";

		public static final String YYYY_MM = "yyyy-MM";

		public static final String YYYY = "yyyy";

		public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

		public static final String YYYYMMDD = "yyyyMMdd";

		public static final String YYYYMM = "yyyyMM";

		public static final String YYYYMMDDHHMMSS_1 = "yyyy/MM/dd HH:mm:ss";

		public static final String YYYY_MM_DD_1 = "yyyy/MM/dd";

		public static final String YYYY_MM_1 = "yyyy/MM";

		/**
		 * 
		 * 自定义取值，Date类型转为String类型
		 * 
		 * @param date
		 *            日期
		 * @param pattern
		 *            格式化常量
		 * @return
		 * @see [类、类#方法、类#成员]
		 */
		public static String dateToStr(Date date, String pattern) {
			SimpleDateFormat format = null;

			if (null == date)
				return null;
			format = new SimpleDateFormat(pattern, Locale.getDefault());

			return format.format(date);
		}

		/**
		 * 将字符串转换成Date类型的时间
		 * <hr>
		 * 
		 * @param s
		 *            日期类型的字符串<br>
		 *            datePattern :YYYY_MM_DD<br>
		 * @return java.util.Date
		 */
		public static Date strToDate(String s, String pattern) {
			if (s == null) {
				return null;
			}
			Date date = null;
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			try {
				date = sdf.parse(s);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return date;
		}
	}

}