package com.github.engatec.vdl.controller.preferences;

import java.io.File;
import java.util.ResourceBundle;

import com.github.engatec.fxcontrols.TextFieldExt;
import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.preferences.ConfigRegistry;
import com.github.engatec.vdl.handler.textformatter.IntegerTextFormatter;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.AuthPasswordPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.AuthUsernamePref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.ConfigFilePathPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.ForceIpV4Pref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.ForceIpV6Pref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.NoMTimePref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.ProxyUrlPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.SocketTimeoutPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.SourceAddressPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.UseConfigFilePref;
import com.github.engatec.vdl.ui.Icons;
import com.github.engatec.vdl.validation.InputForm;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.apache.commons.validator.routines.UrlValidator;

public class YoutubedlPreferencesController extends VBox implements InputForm {

    private final Stage stage;

    @FXML private TextFieldExt proxyUrlTextField;
    @FXML private TextFieldExt socketTimoutTextField;
    @FXML private TextFieldExt sourceAddressTextField;

    @FXML private CheckBox forceIpV4CheckBox;
    @FXML private CheckBox forceIpV6CheckBox;

    @FXML private TextFieldExt usernameTextField;
    @FXML private TextFieldExt passwordTextField;

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
        initAuthenticationSettings();
        initConfigFileSettings();
        bindPropertyHolder();
    }

    private void bindPropertyHolder() {
        proxyUrlTextField.textProperty().bindBidirectional(ConfigRegistry.get(ProxyUrlPref.class).getProperty());
        socketTimoutTextField.textProperty().bindBidirectional(ConfigRegistry.get(SocketTimeoutPref.class).getProperty());
        sourceAddressTextField.textProperty().bindBidirectional(ConfigRegistry.get(SourceAddressPref.class).getProperty());
        forceIpV4CheckBox.selectedProperty().bindBidirectional(ConfigRegistry.get(ForceIpV4Pref.class).getProperty());
        forceIpV6CheckBox.selectedProperty().bindBidirectional(ConfigRegistry.get(ForceIpV6Pref.class).getProperty());

        usernameTextField.textProperty().bindBidirectional(ConfigRegistry.get(AuthUsernamePref.class).getProperty());
        passwordTextField.textProperty().bindBidirectional(ConfigRegistry.get(AuthPasswordPref.class).getProperty());

        noMTimeCheckBox.selectedProperty().bindBidirectional(ConfigRegistry.get(NoMTimePref.class).getProperty());

        useConfigFileCheckBox.selectedProperty().bindBidirectional(ConfigRegistry.get(UseConfigFilePref.class).getProperty());
        configFileTextField.textProperty().bindBidirectional(ConfigRegistry.get(ConfigFilePathPref.class).getProperty());
    }

    private void initNetworkSettings() {
        ResourceBundle resourceBundle = ApplicationContext.INSTANCE.getResourceBundle();

        proxyUrlTextField.textProperty().addListener((observable, oldValue, newValue) -> proxyUrlTextField.clearError());
        proxyUrlTextField.setHint(resourceBundle.getString("preferences.youtubedl.network.proxy.hint"));

        socketTimoutTextField.setTextFormatter(new IntegerTextFormatter());
        socketTimoutTextField.setHint(resourceBundle.getString("preferences.youtubedl.network.socket.timeout.hint"));

        sourceAddressTextField.textProperty().addListener((observable, oldValue, newValue) -> sourceAddressTextField.clearError());
        sourceAddressTextField.setHint(resourceBundle.getString("preferences.youtubedl.network.sourceaddress.hint"));

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

    private void initAuthenticationSettings() {
        usernameTextField.textProperty().addListener((observable, oldValue, newValue) -> usernameTextField.clearError());
        passwordTextField.textProperty().addListener((observable, oldValue, newValue) -> passwordTextField.clearError());
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

    @Override
    public boolean hasErrors() {
        boolean hasErrors = false;
        ResourceBundle resourceBundle = ApplicationContext.INSTANCE.getResourceBundle();

        String proxyUrl = proxyUrlTextField.getText();
        if (StringUtils.isNotBlank(proxyUrl) && !new UrlValidator(new String[] {"http", "https", "socks4", "socks5"}).isValid(proxyUrl)) {
            proxyUrlTextField.setError(resourceBundle.getString("preferences.youtubedl.network.proxy.error"));
            hasErrors = true;
        }

        String sourceAddressText = sourceAddressTextField.getText();
        InetAddressValidator ipValidator = InetAddressValidator.getInstance();
        if (StringUtils.isNotBlank(sourceAddressText) && !(ipValidator.isValidInet4Address(sourceAddressText) || ipValidator.isValidInet6Address(sourceAddressText))) {
            sourceAddressTextField.setError(resourceBundle.getString("preferences.youtubedl.network.sourceaddress.error"));
            hasErrors = true;
        }

        if (StringUtils.isBlank(usernameTextField.getText()) && StringUtils.isNotBlank(passwordTextField.getText())) {
            usernameTextField.setError(resourceBundle.getString("preferences.youtubedl.authentication.username.error"));
            hasErrors = true;
        }

        if (StringUtils.isBlank(passwordTextField.getText()) && StringUtils.isNotBlank(usernameTextField.getText())) {
            passwordTextField.setError(resourceBundle.getString("preferences.youtubedl.authentication.password.error"));
            hasErrors = true;
        }

        return hasErrors;
    }
}
