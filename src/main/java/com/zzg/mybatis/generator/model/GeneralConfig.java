package com.zzg.mybatis.generator.model;

public class GeneralConfig {
	private int commentLines=0;
	private int fieldLine=1;
	private int fieldTypeLine=2;
	private int sheetIndex=0;
	private int beginLine=4;
	private int totalCut=0;
	private String outputUrl="D:\\Users\\admin\\Desktop\\config.json";
	private String codingFormat="UTF-8";
    
	public int getCommentLines() {
		return commentLines;
	}

	public void setCommentLines(int commentLines) {
		this.commentLines = commentLines;
	}

	public int getFieldLine() {
		return fieldLine;
	}

	public void setFieldLine(int fieldLine) {
		this.fieldLine = fieldLine;
	}

	public int getFieldTypeLine() {
		return fieldTypeLine;
	}

	public void setFieldTypeLine(int fieldTypeLine) {
		this.fieldTypeLine = fieldTypeLine;
	}

	public int getSheetIndex() {
		return sheetIndex;
	}

	public void setSheetIndex(int sheetIndex) {
		this.sheetIndex = sheetIndex;
	}

	public int getBeginLine() {
		return beginLine;
	}

	public void setBeginLine(int beginLine) {
		this.beginLine = beginLine;
	}

	public int getTotalCut() {
		return totalCut;
	}

	public void setTotalCut(int totalCut) {
		this.totalCut = totalCut;
	}

	public String getOutputUrl() {
		return outputUrl;
	}

	public void setOutputUrl(String outputUrl) {
		this.outputUrl = outputUrl;
	}

	public String getCodingFormat() {
		return codingFormat;
	}

	public void setCodingFormat(String codingFormat) {
		this.codingFormat = codingFormat;
	}

}
