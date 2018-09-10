package com.zzg.mybatis.generator.controller;

import com.zzg.mybatis.generator.model.FiledConfig;
import com.zzg.mybatis.generator.model.GeneralConfig;
import com.zzg.mybatis.generator.model.GeneratorConfig;
import com.zzg.mybatis.generator.model.TableConfig;
import com.zzg.mybatis.generator.model.UITableColumnVO;
import com.zzg.mybatis.generator.util.ConfigHelper;
import com.zzg.mybatis.generator.util.ImportExeclUtil;
import com.zzg.mybatis.generator.util.MyStringUtils;
import com.zzg.mybatis.generator.view.AlertUtil;
import com.zzg.mybatis.generator.view.UIProgressCallback;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

@SuppressWarnings("restriction")
public class MainUIController extends BaseFXController {

	private static final Logger _LOG = LoggerFactory.getLogger(MainUIController.class);
	private static final String FOLDER_NO_EXIST = "部分目录不存在，是否创建";
	// tool bar buttons
	@FXML
	private Label connectionLabel;
	@FXML
	private Label configsLabel;
	@FXML
	private Label startLabel;
	@FXML
	private TextField modelTargetPackage;
	@FXML
	private TextField mapperTargetPackage;
	@FXML
	private TextField daoTargetPackage;
	@FXML
	private TextField tableNameField;
	@FXML
	private TextField domainObjectNameField;
	@FXML
	private TextField generateKeysField; // 主键ID
	@FXML
	private TextField modelTargetProject;
	@FXML
	private TextField mappingTargetProject;
	@FXML
	private TextField daoTargetProject;
	@FXML
	private TextField mapperName;
	@FXML
	private TextField projectFolderField;
	@FXML
	private CheckBox offsetLimitCheckBox;
	@FXML
	private CheckBox commentCheckBox;
	@FXML
	private CheckBox overrideXML;
	@FXML
	private CheckBox needToStringHashcodeEquals;
	@FXML
	private CheckBox forUpdateCheckBox;
	@FXML
	private CheckBox annotationDAOCheckBox;
	@FXML
	private CheckBox useTableNameAliasCheckbox;
	@FXML
	private CheckBox annotationCheckBox;
	@FXML
	private CheckBox useActualColumnNamesCheckbox;
	@FXML
	private CheckBox useExample;
	@FXML
	private CheckBox useDAOExtendStyle;
	@FXML
	private CheckBox useSchemaPrefix;
	@FXML
	private CheckBox jsr310Support;
	@FXML
	private TableView<UITableColumnVO> columnListView;
	@FXML
	private TableColumn<UITableColumnVO, Boolean> checkedColumn;
	@FXML
	private TableColumn<UITableColumnVO, String> columnNameColumn;
	@FXML
	private TableColumn<UITableColumnVO, String> tableNameColumn;
	@FXML
	private TableColumn<UITableColumnVO, String> tableAnnotationColumn;
	@FXML
	private TableColumn<UITableColumnVO, String> columnType;

	private CheckBox isLoad;
    
	private int loadnumber;
	/**
	 * 修改表格配置是否保存
	 */
	private boolean isSaved=true;
	@FXML
	private TreeView<String> leftDBTree;

	// Current selected tableName
	private String tableName;
	private String dicurl;
    private boolean flag=true;
	@FXML
	private ChoiceBox<String> encodingChoice;
    
	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public void setColumnList(ObservableList<UITableColumnVO> columns) {
		columnListView.setItems(columns);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ImageView dbImage = new ImageView("icons/computer.png");
		dbImage.setFitHeight(40);
		dbImage.setFitWidth(40);
		connectionLabel.setGraphic(dbImage);
		connectionLabel.setOnMouseClicked(event -> {
			// DbConnectionController controller = (DbConnectionController)
			// loadFXMLPage("新建数据库连接", FXMLPage.NEW_CONNECTION, false);
			// controller.setMainUIController(this);
			// controller.showDialogStage();
			chooseProjectFolder();
		});
		ImageView dataImage = new ImageView("icons/database.png");
		dataImage.setFitHeight(40);
		dataImage.setFitWidth(50);
		startLabel.setGraphic(dataImage);
		startLabel.setOnMouseClicked(event -> {
			try {
				generateCode();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		ImageView configImage = new ImageView("icons/config-list.png");
		configImage.setFitHeight(40);
		configImage.setFitWidth(40);
		configsLabel.setGraphic(configImage);
		configsLabel.setOnMouseClicked(event -> {
			DbConnectionController controller = (DbConnectionController) loadFXMLPage("配置", FXMLPage.NEW_CONNECTION,
					false);
			controller.setMainUIController(this);
			controller.showDialogStage();
			Stage primaryStage = controller.getDialogStage();
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent event) {
					if (!flag) {
						flag = true;
						controller.saveConnection();
						AlertUtil.showInfoAlert("已保存修改部分");
					}
				}
			});
		});

		leftDBTree.setShowRoot(false);
		leftDBTree.setRoot(new TreeItem<>());
		Callback<TreeView<String>, TreeCell<String>> defaultCellFactory = TextFieldTreeCell.forTreeView();
		leftDBTree.setCellFactory((TreeView<String> tv) -> {
			TreeCell<String> cell = defaultCellFactory.call(tv);
			cell.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
				int level = leftDBTree.getTreeItemLevel(cell.getTreeItem());
				TreeCell<String> treeCell = (TreeCell<String>) event.getSource();
				TreeItem<String> treeItem = treeCell.getTreeItem();
				if (level == 1) {
					final ContextMenu contextMenu = new ContextMenu();
					MenuItem item3 = new MenuItem("删除目录");
					item3.setOnAction(event1 -> {
						String selectedConfig = (String) treeItem.getGraphic().getUserData();
						try {
							ConfigHelper.deleteDirectoryConfig(selectedConfig);
							;
							this.loadLeftDBTree();
						} catch (Exception e) {
							System.out.println(e.getMessage());
							AlertUtil.showErrorAlert("Delete connection failed! Reason: " + e.getMessage());
						}
					});
					contextMenu.getItems().addAll(item3);
					cell.setContextMenu(contextMenu);
				}
				// 双击点出表配置
				if (event.getClickCount() == 2) {
					if (treeItem == null) {
						return;
					}
					treeItem.setExpanded(true);
					if (level == 1) {
						System.out.println("index: " + leftDBTree.getSelectionModel().getSelectedIndex());
						String dicname = (String) treeItem.getGraphic().getUserData();
						try {
							List<String> filedNames = getFiledNames(dicname);
							if (filedNames != null && filedNames.size() > 0) {
								ObservableList<TreeItem<String>> children = cell.getTreeItem().getChildren();
								children.clear();
								for (String tableName : filedNames) {
									TreeItem<String> newTreeItem = new TreeItem<>();
									/*
									 * ImageView imageView = new ImageView("icons/table.png");
									 * imageView.setFitHeight(16); imageView.setFitWidth(16);
									 * newTreeItem.setGraphic(imageView);
									 */
									newTreeItem.setValue(tableName);
									TableConfig tableConfig = ConfigHelper.loadTableConfig(dicname, tableName);
									isLoad = new CheckBox();
									if (tableConfig != null) {
										isLoad.setSelected(Boolean.parseBoolean(tableConfig.getIsload()));
									} else {
										ConfigHelper.saveTableConfig(false, dicname, tableName, "true", "");
										isLoad.setSelected(true);
									}
									isLoad.selectedProperty().addListener(new ChangeListener<Boolean>() {
										@Override
										public void changed(ObservableValue<? extends Boolean> observable,
												Boolean oldValue, Boolean newValue) {
											String temp = newValue ? "true" : "false";
											ConfigHelper.saveTableConfig(true, dicname, tableName, temp, null);

										}
									});
									newTreeItem.setGraphic(isLoad);
									children.add(newTreeItem);
								}
							}
						} catch (Exception e) {
							_LOG.error(e.getMessage(), e);
							AlertUtil.showErrorAlert(e.getMessage());
						}
					}
				}
				if (level == 2 && event.getClickCount() == 1) {// 单击左键出字段表
					if (isSaved) {
						String tableNamex = treeCell.getTreeItem().getValue();
						this.tableName = tableNamex;
						String dicurl = (String) treeItem.getParent().getGraphic().getUserData();
						this.dicurl = dicurl;
						List<UITableColumnVO> tableColumns = null;
						try {
							tableColumns = getTableData(tableNamex, dicurl);
						} catch (Exception e) {
							e.printStackTrace();
						}
						this.setColumnList(FXCollections.observableList(tableColumns));
						fillTableData();
						for (UITableColumnVO uiTableColumnVO : tableColumns) {
							ObservableValue<Boolean> cellObservableValue = checkedColumn
									.getCellObservableValue(uiTableColumnVO);
							cellObservableValue.addListener(new ChangeListener<Boolean>() {
								@Override
								public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
										Boolean newValue) {
									isSaved = false;
								}
							});
						}	
					} else {
						AlertUtil.showInfoAlert("修改尚未保存");
					}
				}
			});
			return cell;
		});
		loadLeftDBTree();
		// setTooltip();
		// 默认选中第一个，否则如果忘记选择，没有对应错误提示
		// encodingChoice.getSelectionModel().selectFirst();
		try {
			GeneralConfig loadGeneralConfig = ConfigHelper.loadGeneralConfig();
			if (loadGeneralConfig == null) {
				ConfigHelper.saveGeneralConfig(false, new GeneralConfig());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**填充表格数据*/
	private void fillTableData() {
		 // cellvaluefactory
        checkedColumn.setCellValueFactory(new PropertyValueFactory<>("checked"));
        tableNameColumn.setCellValueFactory(new PropertyValueFactory<>("tableColumnName"));
        tableAnnotationColumn.setCellValueFactory(new PropertyValueFactory<>("tableAnnotation"));
        columnNameColumn.setCellValueFactory(new PropertyValueFactory<>("columnName"));
        columnType.setCellValueFactory(new PropertyValueFactory<>("columnType"));
        // Cell Factory that customize how the cell should render
        checkedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(checkedColumn));
        //columnNameColumn.setCellFactory( (TableColumn<UITableColumnVO, String> p) -> new EditingCell<UITableColumnVO>());
        columnType.setCellFactory(TextFieldTableCell.forTableColumn());
        columnType.setOnEditCommit(event -> {
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setColumnType(event.getNewValue());
            isSaved=false;
        });
        columnNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        columnNameColumn.setOnEditCommit(event -> {
        	event.getTableView().getItems().get(event.getTablePosition().getRow()).setColumnName(event.getNewValue());
        	isSaved=false;
        });
       
	}
	/**获取数据填充表格
	 * @throws Exception */
    private  List<UITableColumnVO> getTableData(String tableName,String dicurl) throws Exception{
    	List<UITableColumnVO> columns = new ArrayList<>();
    	GeneralConfig loadGeneralConfig = ConfigHelper.loadGeneralConfig();
    	String filePath =dicurl+"\\"+tableName;
    	File file2 = new File(filePath);
    	InputStream in = new FileInputStream(file2);
    	Workbook wb = ImportExeclUtil.chooseWorkbook(file2.getName(), in);
    	String[] readHead = ImportExeclUtil.readHead(loadGeneralConfig.getSheetIndex(), wb, loadGeneralConfig.getFieldLine());
    	String[] zhushis = ImportExeclUtil.readHead(loadGeneralConfig.getSheetIndex(), wb, loadGeneralConfig.getCommentLines());
    	String[] types = ImportExeclUtil.readHead(loadGeneralConfig.getSheetIndex(), wb, loadGeneralConfig.getFieldTypeLine());
    	int i=0;
    	for (String zhushi : zhushis) {
    		UITableColumnVO columnVO = new UITableColumnVO();
    		if (i<readHead.length) {
    			columnVO.setTableColumnName(readHead[i]);
			}else {
				columnVO.setTableColumnName("");
			}
    		columnVO.setTableAnnotation(zhushi);
    		FiledConfig filedConfig = ConfigHelper.loadFiledConfig(tableName, i);
    		if (filedConfig!=null) {
    			columnVO.setColumnName(filedConfig.getFiledname());	
    			columnVO.setChecked(Boolean.parseBoolean(filedConfig.getIsload()));
    			columnVO.setColumnType(filedConfig.getFiledtype());
			}else {
				if (i<readHead.length) {
	    			columnVO.setColumnName(MyStringUtils.dbStringToCamelStyle(readHead[i]));
				}else {
					columnVO.setColumnName("");
				}
				if (i<types.length) {
					columnVO.setColumnType(getFiledType(types[i]));
				}else {
					columnVO.setColumnType("string");
				}
				ConfigHelper.saveFiledConfig(false, columnVO.getColumnName(), tableName, "true", i,columnVO.getColumnType());
			}
    		columns.add(columnVO);
			i++;
		}
    	TableConfig tableConfig = ConfigHelper.loadTableConfig(dicurl, tableName);
    	if (!StringUtils.isBlank(tableConfig.getClassname())) {
    		modelTargetPackage.setText(tableConfig.getClassname());
		}else {
			Sheet sheet = wb.getSheetAt(0);
	    	String sheetName =sheet.getSheetName();
	    	modelTargetPackage.setText(MyStringUtils.getEnglishWord(sheetName));
	    	ConfigHelper.saveTableConfig(true, dicurl, tableName, tableConfig.getIsload(), MyStringUtils.getEnglishWord(sheetName));
		}
		return columns;
    }
    /**获取字段类型包含整形的一路按照数值 其余一律按照string处理*/
    private String getFiledType(String string) {
    	String real ="";
    	if (string.contains("int")||string.contains("integer") || string.contains("Integer") || string.contains("long")
					|| string.contains("Long")) {
    		real="int";
		}else {
			real="string";
		}
		return real;
    }
	/** 获取目录下面的文件 不递归 */
	private List<String> getFiledNames(String dicUrl) {
		List<String> list = new ArrayList<>();
		File file = new File(dicUrl);
		File[] files = file.listFiles();
		for (File file2 : files) {
			if (file2.isFile()&&(file2.getName().endsWith(".xls")||file2.getName().endsWith(".xlsx"))) {
				list.add(file2.getName());
			}
		}
		return list;

	}
	/*
	 * private void setTooltip() { encodingChoice.setTooltip(new
	 * Tooltip("生成文件的编码，必选")); generateKeysField.setTooltip(new
	 * Tooltip("insert时可以返回主键ID")); offsetLimitCheckBox.setTooltip(new
	 * Tooltip("是否要生成分页查询代码")); commentCheckBox.setTooltip(new
	 * Tooltip("使用数据库的列注释作为实体类字段名的Java注释 "));
	 * useActualColumnNamesCheckbox.setTooltip(new
	 * Tooltip("是否使用数据库实际的列名作为实体类域的名称")); useTableNameAliasCheckbox.setTooltip(new
	 * Tooltip("在Mapper XML文件中表名使用别名，并且列全部使用as查询")); overrideXML.setTooltip(new
	 * Tooltip("重新生成时把原XML文件覆盖，否则是追加")); useDAOExtendStyle.setTooltip(new
	 * Tooltip("将通用接口方法放在公共接口中，DAO接口留空")); forUpdateCheckBox.setTooltip(new
	 * Tooltip("在Select语句中增加for update后缀")); }
	 */

	@SuppressWarnings("unchecked")
	void loadLeftDBTree() {
		@SuppressWarnings("rawtypes")
		TreeItem rootTreeItem = leftDBTree.getRoot();
		rootTreeItem.getChildren().clear();
		try {
			List<String> dbConfigs = ConfigHelper.loadDirectoryConfig();
			for (String dbConfig : dbConfigs) {
				TreeItem<String> treeItem = new TreeItem<>();
				treeItem.setValue(dbConfig);
				ImageView dbImage = new ImageView("icons/computer.png");
				dbImage.setFitHeight(16);
				dbImage.setFitWidth(16);
				dbImage.setUserData(dbConfig);
				treeItem.setGraphic(dbImage);
				rootTreeItem.getChildren().add(treeItem);
			}
		} catch (Exception e) {
			_LOG.error("connect db failed, reason: {}", e);
			AlertUtil.showErrorAlert(e.getMessage() + "\n" + ExceptionUtils.getStackTrace(e));
		}
	}

	@FXML
	public void chooseProjectFolder() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		File selectedFolder = directoryChooser.showDialog(getPrimaryStage());
		try {
			if (selectedFolder != null) {
				ConfigHelper.saveDirectoryConfig(selectedFolder.getAbsolutePath());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		loadLeftDBTree();
		/*
		 * if (selectedFolder != null) {
		 * projectFolderField.setText(selectedFolder.getAbsolutePath()); }
		 */
	}

	@FXML
	public void generateCode(){
		validateConfig();
		ImportExcelToJson importExcelToJson = new ImportExcelToJson();
		String url =dicurl;
		try {
			if (url==null) {
				url=ConfigHelper.loadDirectoryConfig().get(0);
			}
			importExcelToJson.toJson(url,loadnumber);
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			loadnumber++;
		}
	}

	private void validateConfig() {

		try {
			// 检查下json对象名称是否都有
			List<TableConfig> tableConfigs = ConfigHelper.loadNeedTableConfig(dicurl);
			for (TableConfig tableConfig : tableConfigs) {
				if (StringUtils.isBlank(tableConfig.getClassname())) {
					AlertUtil.showErrorAlert(tableConfig.getTabname() + "还没有配置 请前去点击确认一下");
				}
			}
			// 检查一下通用配置是否可用
			GeneralConfig loadGeneralConfig = ConfigHelper.loadGeneralConfig();
			if (loadGeneralConfig == null || StringUtils.isAnyEmpty(loadGeneralConfig.getBeginLine() + "",
					loadGeneralConfig.getCodingFormat() + "", loadGeneralConfig.getCommentLines() + "",
					loadGeneralConfig.getFieldLine() + "", loadGeneralConfig.getOutputUrl(),
					loadGeneralConfig.getSheetIndex() + "", loadGeneralConfig.getTotalCut()+"", loadGeneralConfig.getFieldLine() + "")) {
				AlertUtil.showErrorAlert("通用配置不正确 请前去点击确认一下");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void saveGeneratorConfig() {
		TextInputDialog dialog = new TextInputDialog("");
		dialog.setTitle("保存当前配置");
		dialog.setContentText("请输入配置名称");
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			String name = result.get();
			if (StringUtils.isEmpty(name)) {
				AlertUtil.showErrorAlert("名称不能为空");
				return;
			}
			_LOG.info("user choose name: {}", name);
			try {
				GeneratorConfig generatorConfig = getGeneratorConfigFromUI();
				generatorConfig.setName(name);
				ConfigHelper.saveGeneratorConfig(generatorConfig);
			} catch (Exception e) {
				AlertUtil.showErrorAlert("删除配置失败");
			}
		}
	}

	public GeneratorConfig getGeneratorConfigFromUI() {
		GeneratorConfig generatorConfig = new GeneratorConfig();
		generatorConfig.setProjectFolder(projectFolderField.getText());
		generatorConfig.setModelPackage(modelTargetPackage.getText());
		generatorConfig.setGenerateKeys(generateKeysField.getText());
		generatorConfig.setModelPackageTargetFolder(modelTargetProject.getText());
		generatorConfig.setDaoPackage(daoTargetPackage.getText());
		generatorConfig.setDaoTargetFolder(daoTargetProject.getText());
		generatorConfig.setMapperName(mapperName.getText());
		generatorConfig.setMappingXMLPackage(mapperTargetPackage.getText());
		generatorConfig.setMappingXMLTargetFolder(mappingTargetProject.getText());
		generatorConfig.setTableName(tableNameField.getText());
		generatorConfig.setDomainObjectName(domainObjectNameField.getText());
		generatorConfig.setOffsetLimit(offsetLimitCheckBox.isSelected());
		generatorConfig.setComment(commentCheckBox.isSelected());
		generatorConfig.setOverrideXML(overrideXML.isSelected());
		generatorConfig.setNeedToStringHashcodeEquals(needToStringHashcodeEquals.isSelected());
		generatorConfig.setUseTableNameAlias(useTableNameAliasCheckbox.isSelected());
		generatorConfig.setNeedForUpdate(forUpdateCheckBox.isSelected());
		generatorConfig.setAnnotationDAO(annotationDAOCheckBox.isSelected());
		generatorConfig.setAnnotation(annotationCheckBox.isSelected());
		generatorConfig.setUseActualColumnNames(useActualColumnNamesCheckbox.isSelected());
		generatorConfig.setEncoding(encodingChoice.getValue());
		generatorConfig.setUseExample(useExample.isSelected());
		generatorConfig.setUseDAOExtendStyle(useDAOExtendStyle.isSelected());
		generatorConfig.setUseSchemaPrefix(useSchemaPrefix.isSelected());
		generatorConfig.setJsr310Support(jsr310Support.isSelected());
		return generatorConfig;
	}

	public void setGeneratorConfigIntoUI(GeneratorConfig generatorConfig) {
		projectFolderField.setText(generatorConfig.getProjectFolder());
		modelTargetPackage.setText(generatorConfig.getModelPackage());
		generateKeysField.setText(generatorConfig.getGenerateKeys());
		modelTargetProject.setText(generatorConfig.getModelPackageTargetFolder());
		daoTargetPackage.setText(generatorConfig.getDaoPackage());
		daoTargetProject.setText(generatorConfig.getDaoTargetFolder());
		mapperTargetPackage.setText(generatorConfig.getMappingXMLPackage());
		mappingTargetProject.setText(generatorConfig.getMappingXMLTargetFolder());
		encodingChoice.setValue(generatorConfig.getEncoding());
		useExample.setSelected(generatorConfig.isUseExample());
	}

	@FXML
	public void openTableColumnCustomizationPage() {

		// List<UITableColumnVO> tableColumns =
		// DbUtil.getTableColumns(selectedDatabaseConfig, tableName);
		// this.setColumnList(FXCollections.observableList(tableColumns));

	}

	/**
	 * 检查并创建不存在的文件夹
	 *
	 * @return
	 */
	private boolean checkDirs(GeneratorConfig config) {
		List<String> dirs = new ArrayList<>();
		dirs.add(config.getProjectFolder());
		dirs.add(FilenameUtils
				.normalize(config.getProjectFolder().concat("/").concat(config.getModelPackageTargetFolder())));
		dirs.add(FilenameUtils.normalize(config.getProjectFolder().concat("/").concat(config.getDaoTargetFolder())));
		dirs.add(FilenameUtils
				.normalize(config.getProjectFolder().concat("/").concat(config.getMappingXMLTargetFolder())));
		boolean haveNotExistFolder = false;
		for (String dir : dirs) {
			File file = new File(dir);
			if (!file.exists()) {
				haveNotExistFolder = true;
			}
		}
		if (haveNotExistFolder) {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setContentText(FOLDER_NO_EXIST);
			Optional<ButtonType> optional = alert.showAndWait();
			if (optional.isPresent()) {
				if (ButtonType.OK == optional.get()) {
					try {
						for (String dir : dirs) {
							FileUtils.forceMkdir(new File(dir));
						}
						return true;
					} catch (Exception e) {
						AlertUtil.showErrorAlert("创建目录失败，请检查目录是否是文件而非目录");
					}
				} else {
					return false;
				}
			}
		}
		return true;
	}

	@FXML
	public void openTargetFolder() {
		GeneratorConfig generatorConfig = getGeneratorConfigFromUI();
		String projectFolder = generatorConfig.getProjectFolder();
		try {
			Desktop.getDesktop().browse(new File(projectFolder).toURI());
		} catch (Exception e) {
			AlertUtil.showErrorAlert("打开目录失败，请检查目录是否填写正确" + e.getMessage());
		}

	}

	@FXML
	public void ok() {
		ObservableList<UITableColumnVO> items = columnListView.getItems();
		String text = modelTargetPackage.getText();
		
		int i = 0;
		if (items != null && items.size() > 0) {
			for (UITableColumnVO item : items) {
				String value = item.columnNameProperty().getValue();
				Boolean isload = item.checkedProperty().getValue();
				String load = isload ? "true" : "false";
				//System.out.println(value + "---" + isload+"?????"+item.columnTypeProperty().getValue());
				ConfigHelper.saveFiledConfig(true, value, tableName, load, i,item.columnTypeProperty().getValue());
				i++;
			}
		}
		TableConfig loadTableConfig = null;
		try {
			loadTableConfig = ConfigHelper.loadTableConfig(dicurl, tableName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!text.equals(loadTableConfig.getClassname())) {
			ConfigHelper.saveTableConfig(true, dicurl, tableName, loadTableConfig.getIsload(), text);
		}
		isSaved=true;
		AlertUtil.showInfoAlert("修改成功");
	}

	@FXML
	public void cancel() {
		getDialogStage().close();
	}
}
