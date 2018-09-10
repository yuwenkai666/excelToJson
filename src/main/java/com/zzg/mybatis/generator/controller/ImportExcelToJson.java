package com.zzg.mybatis.generator.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zzg.mybatis.generator.exception.TypeMatchingError;
import com.zzg.mybatis.generator.model.FiledConfig;
import com.zzg.mybatis.generator.model.GeneralConfig;
import com.zzg.mybatis.generator.model.TableConfig;
import com.zzg.mybatis.generator.util.ConfigHelper;
import com.zzg.mybatis.generator.util.ImportExeclUtil;
import com.zzg.mybatis.generator.util.MyStringUtils;
import com.zzg.mybatis.generator.view.AlertUtil;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.Loader;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.annotation.Annotation;

import org.apache.poi.ss.usermodel.Workbook;
public class ImportExcelToJson {
	private CtClass list;
	private CtClass string;
	private CtClass num;
	{
		try {
			list = ClassPool.getDefault().getCtClass("java.util.List");
			string = ClassPool.getDefault().getCtClass("java.lang.String");
			num = ClassPool.getDefault().getCtClass("java.lang.Integer");
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws IOException, Exception {
		ImportExcelToJson importExcelToJson = new ImportExcelToJson();
		String url ="D:\\Users\\admin\\Desktop\\myconfig";
		importExcelToJson.toJson(url,1);
	}

	public void toJson(String dicurl,int loadnumber) throws IOException, Exception {
		// 获取目录下的所有表格文件
		File file = new File(dicurl);
		File[] files = file.listFiles();
		GeneralConfig loadGeneralConfig = ConfigHelper.loadGeneralConfig();
		// 获取可以加载的数据表
		List<TableConfig> tableConfigs = ConfigHelper.loadNeedTableConfig(dicurl);
		// 生成config类
		ClassPool pool = ClassPool.getDefault();
		CtClass makeClass =pool.makeClass("Config"+loadnumber);
		for (TableConfig tableConfig : tableConfigs) {
			CtField ctField = new CtField(list, tableConfig.getClassname(), makeClass);
			ctField.setModifiers(Modifier.PUBLIC);
			makeClass.addField(ctField);
		}
		Object config = makeClass.toClass().newInstance();
		Field[] objfield = config.getClass().getDeclaredFields();

		int number = 0;
		for (TableConfig tableConfig : tableConfigs) {
			for (File file2 : files) {
				if (tableConfig.getTabname().equals(file2.getName())) {
					InputStream in = new FileInputStream(file2);
					Workbook wb = ImportExeclUtil.chooseWorkbook(file2.getName(), in);
					// 根据classname生成类
					CtClass makeClass2 = pool.makeClass(tableConfig.getClassname()+loadnumber);
					ClassFile ccFile = makeClass2.getClassFile();
					ConstPool constpool = ccFile.getConstPool();

					List<FiledConfig> loadFileds = ConfigHelper.loadFileds(tableConfig.getTabname());
					for (FiledConfig filedConfig : loadFileds) {
						CtField ctField = null;
						if (filedConfig.getFiledtype().equalsIgnoreCase("int")) {
							ctField = new CtField(num, filedConfig.getFiledname(), makeClass2);
						} else {
							ctField = new CtField(string, filedConfig.getFiledname(), makeClass2);
						}
						ctField.setModifiers(Modifier.PUBLIC);
						if (!Boolean.parseBoolean(filedConfig.getIsload())) {
							FieldInfo fieldInfo = ctField.getFieldInfo();
							AnnotationsAttribute fieldAttr = new AnnotationsAttribute(constpool,
									AnnotationsAttribute.visibleTag);
							Annotation isIgnore = new Annotation("com.zzg.mybatis.generator.util.IsIgnore", constpool);
							fieldAttr.addAnnotation(isIgnore);
							fieldInfo.addAttribute(fieldAttr);
						}
						makeClass2.addField(ctField);
					}
					Object object = makeClass2.toClass().newInstance();
					List<Object> readDateListT=null;
					try {
						readDateListT = ImportExeclUtil.readDateListT(loadGeneralConfig.getSheetIndex(), wb, object, loadGeneralConfig.getBeginLine(), loadGeneralConfig.getTotalCut());
					} catch (Exception e) {
						if (e instanceof TypeMatchingError ) {
							AlertUtil.showErrorAlert(tableConfig.getTabname()+"表中的"+((TypeMatchingError)e).getFilename()+"字段类型为:"+((TypeMatchingError)e).getFiletype()+"实际数据为:"+((TypeMatchingError)e).getRealvaule());
						}
					}
					List<Object> newobjectList = new ArrayList<>();
					// 新的类
					makeClass2.defrost();
					CtField[] fields = makeClass2.getFields();
					for (CtField ctField : fields) {
						if (ctField.hasAnnotation("com.zzg.mybatis.generator.util.IsIgnore")) {
							makeClass2.removeField(ctField);
						}
					}
					Loader cl = new Loader(pool);
					Class<?> loadClass = cl.loadClass(tableConfig.getClassname()+loadnumber);
					
					Field[] oldfields = readDateListT.get(0).getClass().getDeclaredFields();
					for (Object readDateList : readDateListT) {
						Object object2 =loadClass.newInstance();
						Field[] newfields = object2.getClass().getDeclaredFields();
						for (Field field : oldfields) {
							field.setAccessible(true);
							String name = field.getName();
							for (Field newfield : newfields) {
								newfield.setAccessible(true);
								String newfieldname = newfield.getName();
								if (newfieldname == name) {
									newfield.set(object2, field.get(readDateList));
								}
							}
						}
						newobjectList.add(object2);
					}
					for (Field field : objfield) {
						field.setAccessible(true);
						if (MyStringUtils.getEnglishWord(object.getClass().getSimpleName()).equals(field.getName())) {
							field.set(config, newobjectList);
							System.out.println(object.getClass().getSimpleName() + newobjectList.size());
							number++;
						}
					}

				}
			}

		}
		System.out.println(number + "总的数量");
		String obj = JSON.toJSONString(config, SerializerFeature.WriteMapNullValue,
				SerializerFeature.DisableCircularReferenceDetect);
		String outputUrl =loadGeneralConfig.getOutputUrl();
		try {
			file = new File(outputUrl);
		} catch (Exception e2) {
			e2.printStackTrace();
			AlertUtil.showErrorAlert("请检查一下输出目录");
		}
		String chartName =loadGeneralConfig.getCodingFormat();
		try {
			OutputStream out =new FileOutputStream(file);
			OutputStreamWriter outputStream =new OutputStreamWriter(out, chartName);
			outputStream.write(obj);
			outputStream.flush();
			outputStream.close();
			out.close();
			AlertUtil.showInfoAlert("数据导出完成."+number+"张表被导出,共"+tableConfigs.size()+"张需要导出,失败"+(tableConfigs.size()-number)+"张");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
