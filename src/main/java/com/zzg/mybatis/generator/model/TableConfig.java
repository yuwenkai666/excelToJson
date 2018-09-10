package com.zzg.mybatis.generator.model;

public class TableConfig {
	/**
	 * dicname
	 */
	private String dicname;
	/**
	 * 表名称
	 */
	private String tabname;
	/**
	 * 是否需要加载
	 */
	private String isload;
    /**
     * json对象名称
     */
	private String classname;
	
	public String getDicname() {
		return dicname;
	}
	public void setDicname(String dicname) {
		this.dicname = dicname;
	}
	public String getTabname() {
		return tabname;
	}
	public void setTabname(String tabname) {
		this.tabname = tabname;
	}
	
	public String getIsload() {
		return isload;
	}
	public void setIsload(String isload) {
		this.isload = isload;
	}
	public String getClassname() {
		return classname;
	}
	public void setClassname(String classname) {
		this.classname = classname;
	}
	
}
