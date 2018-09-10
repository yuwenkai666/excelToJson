package com.zzg.mybatis.generator.util;

import com.alibaba.fastjson.JSON;
import com.zzg.mybatis.generator.model.DatabaseConfig;
import com.zzg.mybatis.generator.model.DbType;
import com.zzg.mybatis.generator.model.FiledConfig;
import com.zzg.mybatis.generator.model.GeneralConfig;
import com.zzg.mybatis.generator.model.GeneratorConfig;
import com.zzg.mybatis.generator.model.TableConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * XML based config file help class
 * <p>
 * Created by Owen on 6/16/16.
 */
public class ConfigHelper {

	private static final Logger _LOG = LoggerFactory.getLogger(ConfigHelper.class);
	private static final String BASE_DIR = "config";
	private static final String CONFIG_FILE = "/sqlite3.db";

	public static void createEmptyFiles() throws Exception {
		File file = new File(BASE_DIR);
		if (!file.exists()) {
			file.mkdir();
		}
		File uiConfigFile = new File(BASE_DIR + CONFIG_FILE);
		if (!uiConfigFile.exists()) {
			createEmptyXMLFile(uiConfigFile);
		}
	}

	static void createEmptyXMLFile(File uiConfigFile) throws IOException {
		InputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = Thread.currentThread().getContextClassLoader().getResourceAsStream("sqlite3.db");
			fos = new FileOutputStream(uiConfigFile);
			byte[] buffer = new byte[1024];
			int byteread = 0;
			while ((byteread = fis.read(buffer)) != -1) {
				fos.write(buffer, 0, byteread);
			}
		} finally {
			if (fis != null)
				fis.close();
			if (fos != null)
				fos.close();
		}

	}

	public static List<DatabaseConfig> loadDatabaseConfig() throws Exception {
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		try {
			conn = ConnectionManager.getConnection();
			stat = conn.createStatement();
			rs = stat.executeQuery("SELECT * FROM dbs");
			List<DatabaseConfig> configs = new ArrayList<>();
			while (rs.next()) {
				int id = rs.getInt("id");
				String value = rs.getString("value");
				DatabaseConfig databaseConfig = JSON.parseObject(value, DatabaseConfig.class);
				databaseConfig.setId(id);
				configs.add(databaseConfig);
			}

			return configs;
		} finally {
			if (rs != null)
				rs.close();
			if (stat != null)
				stat.close();
			if (conn != null)
				conn.close();
		}
	}

	public static void saveDatabaseConfig(boolean isUpdate, Integer primaryKey, DatabaseConfig dbConfig)
			throws Exception {
		String configName = dbConfig.getName();
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		try {
			conn = ConnectionManager.getConnection();
			stat = conn.createStatement();
			if (!isUpdate) {
				ResultSet rs1 = stat.executeQuery("SELECT * from dbs where name = '" + configName + "'");
				if (rs1.next()) {
					throw new RuntimeException("配置已经存在, 请使用其它名字");
				}
			}
			String jsonStr = JSON.toJSONString(dbConfig);
			String sql;
			if (isUpdate) {
				sql = String.format("UPDATE dbs SET name = '%s', value = '%s' where id = %d", configName, jsonStr,
						primaryKey);
			} else {
				sql = String.format("INSERT INTO dbs (name, value) values('%s', '%s')", configName, jsonStr);
			}
			stat.executeUpdate(sql);
		} finally {
			if (rs != null)
				rs.close();
			if (stat != null)
				stat.close();
			if (conn != null)
				conn.close();
		}
	}

	public static void deleteDatabaseConfig(DatabaseConfig databaseConfig) throws Exception {
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		try {
			conn = ConnectionManager.getConnection();
			stat = conn.createStatement();
			String sql = String.format("delete from dbs where id=%d", databaseConfig.getId());
			stat.executeUpdate(sql);
		} finally {
			if (rs != null)
				rs.close();
			if (stat != null)
				stat.close();
			if (conn != null)
				conn.close();
		}
	}

	/** 加载所有保存的目录配置 */
	public static List<String> loadDirectoryConfig() throws Exception {
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		try {
			conn = ConnectionManager.getConnection();
			stat = conn.createStatement();
			rs = stat.executeQuery("SELECT * FROM directorys");
			List<String> configs = new ArrayList<>();
			while (rs.next()) {

				String value = rs.getString("dicname");
				configs.add(value);
			}

			return configs;
		} finally {
			if (rs != null)
				rs.close();
			if (stat != null)
				stat.close();
			if (conn != null)
				conn.close();
		}
	}

	/** 保存所有保存的目录配置 */
	public static void saveDirectoryConfig(String directoryName) throws Exception {
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		try {
			conn = ConnectionManager.getConnection();
			stat = conn.createStatement();
			ResultSet rs1 = stat.executeQuery("SELECT * from directorys where dicname = '" + directoryName + "'");
			if (rs1.next()) {
				throw new RuntimeException("配置已经存在, 请使用其它名字");
			}
			String sql = String.format("INSERT INTO directorys (dicname) values('%s')", directoryName);

			stat.executeUpdate(sql);
		} finally {
			if (rs != null)
				rs.close();
			if (stat != null)
				stat.close();
			if (conn != null)
				conn.close();
		}
	}

	/** 删除所有保存的目录配置 */
	public static void deleteDirectoryConfig(String directoryName) throws Exception {
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		try {
			conn = ConnectionManager.getConnection();
			stat = conn.createStatement();
			String sql ="delete from directorys where  dicname = '" + directoryName + "'";
			stat.executeUpdate(sql);
		} finally {
			if (rs != null)
				rs.close();
			if (stat != null)
				stat.close();
			if (conn != null)
				conn.close();
		}
	}
	/** 加载表格的配置 */
	public static TableConfig loadTableConfig(String directoryName,String tabname) throws Exception {
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		TableConfig tableConfig=null;
		try {
			conn = ConnectionManager.getConnection();
			stat = conn.createStatement();
			rs = stat.executeQuery("SELECT * from tables where dicname = '" + directoryName + "'"+"and tabname='"+tabname+ "'");
			while (rs.next()) {
				tableConfig =new TableConfig();
				tableConfig.setClassname(rs.getString("classname"));
				tableConfig.setDicname(rs.getString("dicname"));
				tableConfig.setIsload(rs.getString("isload"));
				tableConfig.setTabname(rs.getString("tabname"));
			}
			
			return tableConfig;
		} finally {
			if (rs != null)
				rs.close();
			if (stat != null)
				stat.close();
			if (conn != null)
				conn.close();
		}
	}
	/** 加载需要加载的表格配置 */
	public static List<TableConfig> loadNeedTableConfig(String directoryName) throws Exception {
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		List<TableConfig> tableConfigs=new ArrayList<>();
		try {
			conn = ConnectionManager.getConnection();
			stat = conn.createStatement();
			rs = stat.executeQuery("SELECT * from tables where dicname = '" + directoryName + "'"+"and isload='true'");
			while (rs.next()) {
				TableConfig tableConfig =new TableConfig();
				tableConfig.setClassname(rs.getString("classname"));
				tableConfig.setDicname(rs.getString("dicname"));
				tableConfig.setIsload(rs.getString("isload"));
				tableConfig.setTabname(rs.getString("tabname"));
				tableConfigs.add(tableConfig);
			}
			
			return tableConfigs;
		} finally {
			if (rs != null)
				rs.close();
			if (stat != null)
				stat.close();
			if (conn != null)
				conn.close();
		}
	}

	
	/** 保存表的配置 */
	public static void saveTableConfig(boolean isUpdate, String directoryName, String tabname, String isload,
			String classname) {
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;

		try {
			conn = ConnectionManager.getConnection();
			stat = conn.createStatement();

			String sql = null;
			if (isUpdate) {
				if (classname == null) {
					sql = String.format(
							"UPDATE tables SET isload = '%s' where dicname = '%s' and tabname='%s'",
							isload, directoryName, tabname);
				}else {
					sql = String.format(
							"UPDATE tables SET isload = '%s', classname = '%s' where dicname = '%s' and tabname='%s'",
							isload, classname, directoryName, tabname);
				}
			} else {
				sql = String.format(
						"INSERT INTO tables (dicname,tabname,isload,classname) values('%s', '%s','%s','%s')",
						directoryName, tabname, isload, classname);
			}
			stat.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (stat != null)
					stat.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	/** 保存字段的配置的配置 */
	public static void saveFiledConfig(boolean isUpdate, String filedname, String tabname, String isload,
			Integer index,String filedtype) {
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;

		try {
			conn = ConnectionManager.getConnection();
			stat = conn.createStatement();

			String sql = null;
			if (isUpdate) {
				sql = String.format(
						"UPDATE filed SET isload = '%s', filedname = '%s',filedtype = '%s' where indexs = '%d' and tabname='%s'",
						isload,filedname,filedtype,index, tabname);
			} else {
				sql = String.format(
						"INSERT INTO filed (indexs,tabname,isload,filedname,filedtype) values('%d', '%s','%s','%s','%s')",
						index, tabname, isload, filedname,filedtype);
			}
			stat.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (stat != null)
					stat.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	/** 加载字段的配置 */
	public static FiledConfig loadFiledConfig(String tabname,Integer index) throws Exception {
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		FiledConfig filedConfig=null;
		try {
			conn = ConnectionManager.getConnection();
			stat = conn.createStatement();
			//System.out.println("SELECT * from filed where tabname = '" + tabname + "'"+"and indexs="+index);
			rs = stat.executeQuery("SELECT * from filed where tabname = '" + tabname + "'"+"and indexs="+index);
			while (rs.next()) {
				filedConfig =new FiledConfig();
				filedConfig.setTabname(rs.getString("tabname"));
				filedConfig.setFiledname(rs.getString("filedname"));
				filedConfig.setIsload(rs.getString("isload"));
				filedConfig.setIndex(rs.getInt("indexs"));
				filedConfig.setFiledtype(rs.getString("filedtype"));
			}
			
			return filedConfig;
		} finally {
			if (rs != null)
				rs.close();
			if (stat != null)
				stat.close();
			if (conn != null)
				conn.close();
		}
	}
	/** 加载字段的配置 */
	public static List<FiledConfig> loadFileds(String tabname) throws Exception {
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		List<FiledConfig>  filedConfigs=new ArrayList<>();
		try {
			conn = ConnectionManager.getConnection();
			stat = conn.createStatement();
			//System.out.println("SELECT * from filed where tabname = '" + tabname + "'"+"and indexs="+index);
			rs = stat.executeQuery("SELECT * from filed where tabname = '" + tabname + "'");
			while (rs.next()) {
				FiledConfig filedConfig =new FiledConfig();
				filedConfig.setTabname(rs.getString("tabname"));
				filedConfig.setFiledname(rs.getString("filedname"));
				filedConfig.setIsload(rs.getString("isload"));
				filedConfig.setIndex(rs.getInt("indexs"));
				filedConfig.setFiledtype(rs.getString("filedtype"));
				filedConfigs.add(filedConfig);
			}
			
			return filedConfigs;
		} finally {
			if (rs != null)
				rs.close();
			if (stat != null)
				stat.close();
			if (conn != null)
				conn.close();
		}
	}
	/** 加载通用配置 */
	public static GeneralConfig loadGeneralConfig() throws Exception {
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		try {
			conn = ConnectionManager.getConnection();
			stat = conn.createStatement();
			rs = stat.executeQuery("SELECT * FROM generalconfig");
			GeneralConfig generalConfig=null;
			while (rs.next()) {
				generalConfig=new GeneralConfig();
				generalConfig.setBeginLine(rs.getInt("beginLine"));
				generalConfig.setCodingFormat(rs.getString("codingFormat"));
				generalConfig.setCommentLines(rs.getInt("commentLines"));
				generalConfig.setFieldLine(rs.getInt("fieldLine"));
				generalConfig.setFieldTypeLine(rs.getInt("fieldTypeLine"));
				generalConfig.setOutputUrl(rs.getString("outputUrl"));
				generalConfig.setSheetIndex(rs.getInt("sheetIndex"));
				generalConfig.setTotalCut(rs.getInt("totalCut"));	
			}
			return generalConfig;
		} finally {
			if (rs != null)
				rs.close();
			if (stat != null)
				stat.close();
			if (conn != null)
				conn.close();
		}
	}
	/** 保存字段的配置的配置 */
	public static void saveGeneralConfig(boolean isUpdate, GeneralConfig generalConfig) {
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;

		try {
			conn = ConnectionManager.getConnection();
			stat = conn.createStatement();
			String sql = null;
			if (isUpdate) {
				sql = String.format(
						"UPDATE generalconfig SET beginLine = '%s', codingFormat = '%s',commentLines = '%s',fieldLine = '%d',fieldTypeLine = '%d',outputUrl = '%s',sheetIndex = '%d',totalCut = '%d'",
						generalConfig.getBeginLine(), generalConfig.getCodingFormat(), generalConfig.getCommentLines(), generalConfig.getFieldLine(),generalConfig.getFieldTypeLine(),generalConfig.getOutputUrl(),generalConfig.getSheetIndex(),generalConfig.getTotalCut());
			} else {
				sql = String.format(
						"INSERT INTO generalconfig (beginLine,codingFormat,commentLines,fieldLine,fieldTypeLine,outputUrl,sheetIndex,totalCut) values('%d', '%s','%d','%d','%d','%s','%d','%d')",
						generalConfig.getBeginLine(), generalConfig.getCodingFormat(), generalConfig.getCommentLines(), generalConfig.getFieldLine(),generalConfig.getFieldTypeLine(),generalConfig.getOutputUrl(),generalConfig.getSheetIndex(),generalConfig.getTotalCut());
			}
			stat.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (stat != null)
					stat.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
    public static void main(String[] args) {
    	String sql=String.format("UPDATE tables SET isload = '%s', classname = '%s' where dicname = '%s' and tabname='%s'", "1","2","3","4");
    	System.out.println(Boolean.parseBoolean(sql));
    	
	}
	public static void saveGeneratorConfig(GeneratorConfig generatorConfig) throws Exception {
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		try {
			conn = ConnectionManager.getConnection();
			stat = conn.createStatement();
			String jsonStr = JSON.toJSONString(generatorConfig);
			String sql = String.format("INSERT INTO generator_config values('%s', '%s')", generatorConfig.getName(),
					jsonStr);
			stat.executeUpdate(sql);
		} finally {
			if (rs != null)
				rs.close();
			if (stat != null)
				stat.close();
			if (conn != null)
				conn.close();
		}
	}
	
	public static GeneratorConfig loadGeneratorConfig(String name) throws Exception {
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		try {
			conn = ConnectionManager.getConnection();
			stat = conn.createStatement();
			String sql = String.format("SELECT * FROM generator_config where name='%s'", name);
			_LOG.info("sql: {}", sql);
			rs = stat.executeQuery(sql);
			GeneratorConfig generatorConfig = null;
			if (rs.next()) {
				String value = rs.getString("value");
				generatorConfig = JSON.parseObject(value, GeneratorConfig.class);
			}
			return generatorConfig;
		} finally {
			if (rs != null)
				rs.close();
			if (stat != null)
				stat.close();
			if (conn != null)
				conn.close();
		}
	}

	public static List<GeneratorConfig> loadGeneratorConfigs() throws Exception {
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		try {
			conn = ConnectionManager.getConnection();
			stat = conn.createStatement();
			String sql = String.format("SELECT * FROM generator_config");
			_LOG.info("sql: {}", sql);
			rs = stat.executeQuery(sql);
			List<GeneratorConfig> configs = new ArrayList<>();
			while (rs.next()) {
				String value = rs.getString("value");
				configs.add(JSON.parseObject(value, GeneratorConfig.class));
			}
			return configs;
		} finally {
			if (rs != null)
				rs.close();
			if (stat != null)
				stat.close();
			if (conn != null)
				conn.close();
		}
	}

	public static int deleteGeneratorConfig(String name) throws Exception {
		Connection conn = null;
		Statement stat = null;
		try {
			conn = ConnectionManager.getConnection();
			stat = conn.createStatement();
			String sql = String.format("DELETE FROM generator_config where name='%s'", name);
			_LOG.info("sql: {}", sql);
			return stat.executeUpdate(sql);
		} finally {
			if (stat != null)
				stat.close();
			if (conn != null)
				conn.close();
		}
	}

	public static String findConnectorLibPath(String dbType) {
		DbType type = DbType.valueOf(dbType);
		URL resource = Thread.currentThread().getContextClassLoader().getResource("logback.xml");
		_LOG.info("jar resource: {}", resource);
		if (resource != null) {
			try {
				File file = new File(resource.toURI().getRawPath() + "/../lib/" + type.getConnectorJarFile());
				return URLDecoder.decode(file.getCanonicalPath(), Charset.forName("UTF-8").displayName());
			} catch (Exception e) {
				throw new RuntimeException("找不到驱动文件，请联系开发者");
			}
		} else {
			throw new RuntimeException("lib can't find");
		}
	}

	public static List<String> getAllJDBCDriverJarPaths() {
		List<String> jarFilePathList = new ArrayList<>();
		URL url = Thread.currentThread().getContextClassLoader().getResource("logback.xml");
		try {
			File file;
			if (url.getPath().contains(".jar")) {
				file = new File("lib/");
			} else {
				file = new File("src/main/resources/lib");
			}
			System.out.println(file.getCanonicalPath());
			File[] jarFiles = file.listFiles();
			System.out.println("jarFiles:" + jarFiles);
			if (jarFiles != null && jarFiles.length > 0) {
				for (File jarFile : jarFiles) {
					if (jarFile.isFile() && jarFile.getAbsolutePath().endsWith(".jar")) {
						jarFilePathList.add(jarFile.getAbsolutePath());
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("找不到驱动文件，请联系开发者");
		}
		return jarFilePathList;
	}

}
