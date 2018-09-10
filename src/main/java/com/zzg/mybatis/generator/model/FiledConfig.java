package com.zzg.mybatis.generator.model;

public class FiledConfig {
	private String tabname;
	private String filedname;
	private String isload;
	private Integer index;
	private String filedtype;
	

	public String getFiledtype() {
		return filedtype;
	}

	public void setFiledtype(String filedtype) {
		this.filedtype = filedtype;
	}

	public String getTabname() {
		return tabname;
	}

	public void setTabname(String tabname) {
		this.tabname = tabname;
	}

	public String getFiledname() {
		return filedname;
	}

	public void setFiledname(String filedname) {
		this.filedname = filedname;
	}

	public String getIsload() {
		return isload;
	}

	public void setIsload(String isload) {
		this.isload = isload;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

}
