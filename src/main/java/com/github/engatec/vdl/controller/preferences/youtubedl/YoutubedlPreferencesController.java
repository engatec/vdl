package com.github.engatec.vdl.controller.preferences.youtubedl;

import com.github.engatec.fxcontrols.FxFileChooser;
import com.github.engatec.fxcontrols.FxTextField;
import com.github.engatec.vdl.validation.InputForm;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class YoutubedlPreferencesController extends ScrollPane implements InputForm {

    private final Stage stage;

    private Context ctx;

    @FXML private FxTextField proxyUrlTextField;
    @FXML private FxTextField socketTimoutTextField;
    @FXML private FxTextField sourceAddressTextField;

    @FXML private CheckBox forceIpV4CheckBox;
    @FXML private CheckBox forceIpV6CheckBox;

    @FXML private FxTextField usernameTextField;
    @FXML private FxTextField passwordTextField;
    @FXML private TextField twoFactorTextField;
    @FXML private FxTextField videoPasswordTextField;
    @FXML private CheckBox netrcCheckbox;

    @FXML private CheckBox markWatchedCheckbox;
    @FXML private CheckBox noContinueCheckbox;
    @FXML private CheckBox noPartCheckBox;
    @FXML private CheckBox noMTimeCheckBox;

    @FXML private CheckBox readCookiesCheckbox;
    @FXML private FxFileChooser cookiesFileChooser;

    @FXML private CheckBox useConfigFileCheckBox;
    @FXML private FxFileChooser configFileChooser;

    public YoutubedlPreferencesController(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        ctx = new Context(
                stage,
                proxyUrlTextField,
                socketTimoutTextField,
                sourceAddressTextField,
                forceIpV4CheckBox,
                forceIpV6CheckBox,
                usernameTextField,
                passwordTextField,
                twoFactorTextField,
                videoPasswordTextField,
                netrcCheckbox,
                markWatchedCheckbox,
                noContinueCheckbox,
                noPartCheckBox,
                noMTimeCheckBox,
                readCookiesCheckbox,
                cookiesFileChooser,
                useConfigFileCheckBox,
                configFileChooser
        );

        Initializer.initialize(ctx);
        Binder.bind(ctx);
    }

    @Override
    public boolean hasErrors() {
        return Validator.hasErrors(ctx);
    }
}
