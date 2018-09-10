package com.zzg.mybatis.generator.exception;

public class TypeMatchingError extends Exception {
	
	 
	private static final long serialVersionUID = 1L;
	
	private String filename;
	private String filetype;
	private String realvaule;

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFiletype() {
		return filetype;
	}

	public void setFiletype(String filetype) {
		this.filetype = filetype;
	}

	public String getRealvaule() {
		return realvaule;
	}

	public void setRealvaule(String realvaule) {
		this.realvaule = realvaule;
	}

}
