package com.zzg.mybatis.generator.controller;

import com.zzg.mybatis.generator.model.GeneratorConfig;
import com.zzg.mybatis.generator.util.ConfigHelper;
import com.zzg.mybatis.generator.view.AlertUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * 管理GeneratorConfig的Controller
 *
 * Created by Owen on 8/21/16.
 */
@SuppressWarnings({"restriction", "rawtypes"})
public class GeneratorConfigController extends BaseFXController {

    private static final Logger _LOG = LoggerFactory.getLogger(GeneratorConfigController.class);

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
    private TableView<GeneratorConfig> configTable;
    
	@FXML
    private TableColumn nameColumn;
    @FXML
    private TableColumn opsColumn;

    private MainUIController mainUIController;

    private GeneratorConfigController controller;

    @SuppressWarnings("unchecked")
	@Override
    public void initialize(URL location, ResourceBundle resources) {
        controller = this;
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        // 自定义操作列
        opsColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        opsColumn.setCellFactory(cell -> {
            return new TableCell() {
                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        Button btn1 = new Button("应用");
                        Button btn2 = new Button("删除");
                        HBox hBox = new HBox();
                        hBox.setSpacing(10);
                        hBox.getChildren().add(btn1);
                        hBox.getChildren().add(btn2);
                        btn1.setOnAction(event -> {
                            try {
                                // 应用配置
                                GeneratorConfig generatorConfig = ConfigHelper.loadGeneratorConfig(item.toString());
                                mainUIController.setGeneratorConfigIntoUI(generatorConfig);
                                controller.closeDialogStage();
                            } catch (Exception e) {
                                AlertUtil.showErrorAlert(e.getMessage());
                            }
                        });
                        btn2.setOnAction(event -> {
                            try {
                                // 删除配置
                                _LOG.debug("item: {}", item);
                                ConfigHelper.deleteGeneratorConfig(item.toString());
                                refreshTableView();
                            } catch (Exception e) {
                                AlertUtil.showErrorAlert(e.getMessage());
                            }
                        });
                        setGraphic(hBox);
                    }
                }
            };
        });
        refreshTableView();
    }

    public void refreshTableView() {
        try {
            List<GeneratorConfig> configs = ConfigHelper.loadGeneratorConfigs();
            configTable.setItems(FXCollections.observableList(configs));
        } catch (Exception e) {
            AlertUtil.showErrorAlert(e.getMessage());
        }
    }

    void setMainUIController(MainUIController mainUIController) {
        this.mainUIController = mainUIController;
    }

}
