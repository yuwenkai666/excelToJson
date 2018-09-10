package com.zzg.mybatis.generator.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by Owen on 6/22/16.
 */
@SuppressWarnings("restriction")
public class UITableColumnVO {

   
	private BooleanProperty checked = new SimpleBooleanProperty(true); // Default set to true

    private StringProperty columnName = new SimpleStringProperty();

    private StringProperty tableColumnName = new SimpleStringProperty();
    
    private StringProperty tableAnnotation = new SimpleStringProperty();
    
    private StringProperty columnType = new SimpleStringProperty();
    
    public String getColumnType() {
        return columnType.get();
    }

    public void setColumnType(String columnType) {
        this.columnType.set(columnType);
    }

    public StringProperty columnTypeProperty() {
        return columnType;
    }
   
    
    public String getTableAnnotation() {
		return tableAnnotation.get();
	}

	public void setTableAnnotation(String tableAnnotation) {
		this.tableAnnotation.set(tableAnnotation);
	}
	public StringProperty tableAnnotationProperty() {
        return tableAnnotation;
    }

	public String getColumnName() {
        return columnName.get();
    }

    public void setColumnName(String columnName) {
        this.columnName.set(columnName);
    }

    public StringProperty columnNameProperty() {
        return columnName;
    }
    

    public BooleanProperty checkedProperty() {
        return checked;
    }

    public Boolean getChecked() {
        return this.checked.get();
    }

    public void setChecked(Boolean checked) {
        this.checked.set(checked);
    }

  
    public StringProperty tableColumnNameProperty() {
        return tableColumnName;
    }
   

	public String getTableColumnName() {
		return tableColumnName.get();
	}

	public void setTableColumnName(String tableColumnName) {
		this.tableColumnName.set(tableColumnName);;
	}


     
}
