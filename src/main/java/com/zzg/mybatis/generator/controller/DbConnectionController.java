package com.zzg.mybatis.generator.controller;

import com.zzg.mybatis.generator.model.GeneralConfig;
import com.zzg.mybatis.generator.util.ConfigHelper;
import com.zzg.mybatis.generator.view.AlertUtil;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

@SuppressWarnings("restriction")
public class DbConnectionController extends BaseFXController {

	private static final Logger _LOG = LoggerFactory.getLogger(DbConnectionController.class);

	@FXML
	private TextField commentLines;
	@FXML
	private TextField fieldLine;
	@FXML
	private TextField fieldTypeLine;
	@FXML
	private TextField sheetIndex;
	@FXML
	private TextField beginLine;
	@FXML
	private TextField totalCut;
	@FXML
	private TextField outputUrl;
	@FXML
	private ChoiceBox<String> encodingChoice;

	private MainUIController mainUIController;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setConfig();
	}

	@FXML
	public void chooseProjectFolder() {
		FileChooser fileChooser = new FileChooser();
		File selectedFolder = fileChooser.showSaveDialog(getPrimaryStage());

		if (selectedFolder != null) {
			outputUrl.setText(selectedFolder.getAbsolutePath());
		}
	}

	@FXML
	void saveConnection() {
		GeneralConfig config = extractConfigForUI();
		if (config == null) {
			return;
		}
		try {
			ConfigHelper.saveGeneralConfig(true, config);
			if (!mainUIController.isFlag()) {
				 AlertUtil.showInfoAlert("保存成功");	
				 mainUIController.setFlag(true);
			}
		} catch (Exception e) {
			_LOG.error(e.getMessage(), e);
			AlertUtil.showErrorAlert(e.getMessage());
		}
	}

	void setMainUIController(MainUIController controller) {
		this.mainUIController = controller;
	}

	private GeneralConfig extractConfigForUI() {
		
		Integer comment = Integer.parseInt(commentLines.getText());
		Integer filed = Integer.parseInt(fieldLine.getText());
		Integer type = Integer.parseInt(fieldTypeLine.getText());
		Integer sheet = Integer.parseInt(sheetIndex.getText());
		Integer begin = Integer.parseInt(beginLine.getText());
		String encoding = encodingChoice.getValue();
		Integer totalcut = Integer.parseInt(totalCut.getText());
		String output = outputUrl.getText();
		
		GeneralConfig config = new GeneralConfig();
		config.setBeginLine(begin);
		config.setCodingFormat(encoding);
		config.setCommentLines(comment);
		config.setFieldLine(filed);
		config.setFieldTypeLine(type);
		config.setOutputUrl(output);
		config.setSheetIndex(sheet);
		config.setTotalCut(totalcut);
		if (StringUtils.isAnyEmpty(comment+"", filed+"", type+"", sheet+"", encoding, begin+"", encoding,totalcut+"",output)) {
			AlertUtil.showWarnAlert("所有字段必填");
			return null;
		}
		return config;
	}

	public void setConfig() {
		try {
			GeneralConfig loadGeneralConfig = ConfigHelper.loadGeneralConfig();
			commentLines.setText(loadGeneralConfig.getCommentLines()+"");
			fieldLine.setText(loadGeneralConfig.getFieldLine()+"");
			fieldTypeLine.setText(loadGeneralConfig.getFieldTypeLine()+"");
			sheetIndex.setText(loadGeneralConfig.getSheetIndex()+"");
			beginLine.setText(loadGeneralConfig.getBeginLine()+"");
			encodingChoice.setValue(loadGeneralConfig.getCodingFormat());
			totalCut.setText(loadGeneralConfig.getTotalCut()+"");
			outputUrl.setText(loadGeneralConfig.getOutputUrl());
			ischanged();
		} catch (Exception e) {
			_LOG.error(e.getMessage(), e);
			AlertUtil.showErrorAlert(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void ischanged() {
		commentLines.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            	mainUIController.setFlag(false); 
            }
        });
		fieldLine.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				mainUIController.setFlag(false); 
			}
		});
		fieldTypeLine.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				mainUIController.setFlag(false); 
			}
		});
		sheetIndex.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				mainUIController.setFlag(false); 
			}
		});
		beginLine.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				mainUIController.setFlag(false); 
			}
		});
		encodingChoice.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				mainUIController.setFlag(false); 
			}
		});
		totalCut.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				mainUIController.setFlag(false); 
			}
		});
		outputUrl.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				mainUIController.setFlag(false); 
			}
		});
		
	}

}
