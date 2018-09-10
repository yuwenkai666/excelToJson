package com.zzg.mybatis.generator.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Alert;
import org.mybatis.generator.api.ProgressCallback;

/**
 * Created by Owen on 6/21/16.
 */
@SuppressWarnings("restriction")
public class UIProgressCallback extends Alert implements ProgressCallback  {

    
	private StringProperty progressText = new SimpleStringProperty();

    public UIProgressCallback(AlertType alertType) {
		super(alertType);
		this.contentTextProperty().bindBidirectional(progressText);
    }

    @Override
    public void introspectionStarted(int totalTasks) {
		progressText.setValue("数据导出出错");
	}

    @Override
    public void generationStarted(int totalTasks) {
		progressText.setValue("开始数据导出");
    }

    @Override
    public void saveStarted(int totalTasks) {
		progressText.setValue("开始保存数据");
    }

    @Override
    public void startTask(String taskName) {
		progressText.setValue("数据导出开始");
    }

    @Override
    public void done() {
		progressText.setValue("数据导出完成");
    }

    @Override
    public void checkCancel() throws InterruptedException {
    }
}
