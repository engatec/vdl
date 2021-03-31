package com.github.engatec.vdl.controller.preferences.youtubedl;

import com.github.engatec.fxcontrols.TextFieldExt;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

class Context {

    private final Stage stage;

    private final TextFieldExt proxyUrlTextField;
    private final TextFieldExt socketTimoutTextField;
    private final TextFieldExt sourceAddressTextField;
    private final CheckBox forceIpV4CheckBox;
    private final CheckBox forceIpV6CheckBox;

    private final TextFieldExt usernameTextField;
    private final TextFieldExt passwordTextField;
    private final TextField twoFactorTextField;
    private final TextFieldExt videoPasswordTextField;
    private final CheckBox netrcCheckbox;

    private final CheckBox markWatchedCheckbox;
    private final CheckBox noMTimeCheckBox;
    private final CheckBox useConfigFileCheckBox;
    private final TextField configFileTextField;
    private final Button configFileChooseBtn;

    Context(
            Stage stage,
            TextFieldExt proxyUrlTextField,
            TextFieldExt socketTimoutTextField,
            TextFieldExt sourceAddressTextField,
            CheckBox forceIpV4CheckBox,
            CheckBox forceIpV6CheckBox,
            TextFieldExt usernameTextField,
            TextFieldExt passwordTextField,
            TextField twoFactorTextField,
            TextFieldExt videoPasswordTextField,
            CheckBox netrcCheckbox,
            CheckBox markWatchedCheckbox,
            CheckBox noMTimeCheckBox,
            CheckBox useConfigFileCheckBox,
            TextField configFileTextField,
            Button configFileChooseBtn
    ) {
        this.stage = stage;
        this.proxyUrlTextField = proxyUrlTextField;
        this.socketTimoutTextField = socketTimoutTextField;
        this.sourceAddressTextField = sourceAddressTextField;
        this.forceIpV4CheckBox = forceIpV4CheckBox;
        this.forceIpV6CheckBox = forceIpV6CheckBox;
        this.usernameTextField = usernameTextField;
        this.passwordTextField = passwordTextField;
        this.twoFactorTextField = twoFactorTextField;
        this.videoPasswordTextField = videoPasswordTextField;
        this.netrcCheckbox = netrcCheckbox;
        this.markWatchedCheckbox = markWatchedCheckbox;
        this.noMTimeCheckBox = noMTimeCheckBox;
        this.useConfigFileCheckBox = useConfigFileCheckBox;
        this.configFileTextField = configFileTextField;
        this.configFileChooseBtn = configFileChooseBtn;
    }

    public Stage getStage() {
        return stage;
    }

    TextFieldExt getProxyUrlTextField() {
        return proxyUrlTextField;
    }

    TextFieldExt getSocketTimoutTextField() {
        return socketTimoutTextField;
    }

    TextFieldExt getSourceAddressTextField() {
        return sourceAddressTextField;
    }

    CheckBox getForceIpV4CheckBox() {
        return forceIpV4CheckBox;
    }

    CheckBox getForceIpV6CheckBox() {
        return forceIpV6CheckBox;
    }

    TextFieldExt getUsernameTextField() {
        return usernameTextField;
    }

    TextFieldExt getPasswordTextField() {
        return passwordTextField;
    }

    TextField getTwoFactorTextField() {
        return twoFactorTextField;
    }

    TextFieldExt getVideoPasswordTextField() {
        return videoPasswordTextField;
    }

    CheckBox getNetrcCheckbox() {
        return netrcCheckbox;
    }

    public CheckBox getMarkWatchedCheckbox() {
        return markWatchedCheckbox;
    }

    CheckBox getNoMTimeCheckBox() {
        return noMTimeCheckBox;
    }

    CheckBox getUseConfigFileCheckBox() {
        return useConfigFileCheckBox;
    }

    TextField getConfigFileTextField() {
        return configFileTextField;
    }

    Button getConfigFileChooseBtn() {
        return configFileChooseBtn;
    }
}
