package com.github.engatec.vdl.controller.preferences;

import java.io.File;

import com.github.engatec.vdl.core.preferences.ConfigRegistry;
import com.github.engatec.vdl.handler.textformatter.IntegerTextFormatter;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.ConfigFilePathPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.ForceIpV4Pref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.ForceIpV6Pref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.NoMTimePref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.ProxyUrlPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.SocketTimeoutPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.SourceAddressPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.UseConfigFilePref;
import com.github.engatec.vdl.ui.Icons;
import com.github.engatec.vdl.util.PaneUtils;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.BooleanUtils;

public class YoutubedlPreferencesController extends VBox {

    private final Stage stage;

    @FXML private TextField proxyUrlTextField;
    @FXML private AnchorPane proxyUrlHintPane;

    @FXML private TextField socketTimoutTextField;

    @FXML private TextField sourceAddressTextField;
    @FXML private AnchorPane sourceAddressHintPane;

    private final ToggleGroup ipvSelectionToggleGroup = new ToggleGroup();
    @FXML private CheckBox forceIpV4CheckBox;
    @FXML private CheckBox forceIpV6CheckBox;

    @FXML private CheckBox noMTimeCheckBox;

    @FXML private CheckBox useConfigFileCheckBox;
    @FXML private TextField configFileTextField;
    @FXML private Button configFileChooseBtn;

    public YoutubedlPreferencesController(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        initNetworkSettings();
        initConfigFileSettings();
        bindPropertyHolder();
    }

    private void bindPropertyHolder() {
        proxyUrlTextField.textProperty().bindBidirectional(ConfigRegistry.get(ProxyUrlPref.class).getProperty());
        socketTimoutTextField.textProperty().bindBidirectional(ConfigRegistry.get(SocketTimeoutPref.class).getProperty());
        sourceAddressTextField.textProperty().bindBidirectional(ConfigRegistry.get(SourceAddressPref.class).getProperty());
        forceIpV4CheckBox.selectedProperty().bindBidirectional(ConfigRegistry.get(ForceIpV4Pref.class).getProperty());
        forceIpV6CheckBox.selectedProperty().bindBidirectional(ConfigRegistry.get(ForceIpV6Pref.class).getProperty());

        noMTimeCheckBox.selectedProperty().bindBidirectional(ConfigRegistry.get(NoMTimePref.class).getProperty());

        useConfigFileCheckBox.selectedProperty().bindBidirectional(ConfigRegistry.get(UseConfigFilePref.class).getProperty());
        configFileTextField.textProperty().bindBidirectional(ConfigRegistry.get(ConfigFilePathPref.class).getProperty());
    }

    private void initNetworkSettings() {
        Group proxyUrlHintIcon = Icons.infoWithTooltip("preferences.youtubedl.network.proxy.hint");
        proxyUrlHintPane.getChildren().add(PaneUtils.fillAnchorPane(proxyUrlHintIcon));

        socketTimoutTextField.setTextFormatter(new IntegerTextFormatter());

        Group sourceAddressHintIcon = Icons.infoWithTooltip("preferences.youtubedl.network.sourceaddress.hint");
        sourceAddressHintPane.getChildren().add(PaneUtils.fillAnchorPane(sourceAddressHintIcon));

        forceIpV4CheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (BooleanUtils.isTrue(newValue)) {
                forceIpV6CheckBox.setSelected(false);
            }
        });
        forceIpV6CheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (BooleanUtils.isTrue(newValue)) {
                forceIpV4CheckBox.setSelected(false);
            }
        });
    }

    private void initConfigFileSettings() {
        useConfigFileCheckBox.setGraphic(Icons.infoWithTooltip("preferences.youtubedl.checkbox.configitem.tooltip"));
        useConfigFileCheckBox.setContentDisplay(ContentDisplay.RIGHT);

        BooleanBinding configFileCheckBoxUnselected = useConfigFileCheckBox.selectedProperty().not();
        configFileTextField.disableProperty().bind(configFileCheckBoxUnselected);
        configFileChooseBtn.disableProperty().bind(configFileCheckBoxUnselected);
        configFileChooseBtn.setOnAction(this::handleConfigFileChooseBtnClick);
    }

    private void handleConfigFileChooseBtnClick(ActionEvent event) {
        var fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            String path = selectedFile.getAbsolutePath();
            configFileTextField.setText(path);
        }
        event.consume();
    }
}
