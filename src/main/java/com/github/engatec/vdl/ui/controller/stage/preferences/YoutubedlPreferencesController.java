package com.github.engatec.vdl.ui.controller.stage.preferences;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

import com.github.engatec.fxcontrols.FxFileChooser;
import com.github.engatec.fxcontrols.FxTextField;
import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.preferences.ConfigRegistry;
import com.github.engatec.vdl.handler.textformatter.IntegerTextFormatter;
import com.github.engatec.vdl.handler.textformatter.NotBlankTextFormatter;
import com.github.engatec.vdl.handler.textformatter.RegexTextFormatter;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.AuthPasswordPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.AuthUsernamePref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.ConfigFilePathPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.CookiesFileLocationPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.ForceIpV4Pref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.ForceIpV6Pref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.MarkWatchedPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.NetrcPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.NoContinuePref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.NoMTimePref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.NoPartPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.OutputTemplatePref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.ProxyUrlPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.RateLimitPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.ReadCookiesPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.SocketTimeoutPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.SourceAddressPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.TwoFactorCodePref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.UseConfigFilePref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.VideoPasswordPref;
import com.github.engatec.vdl.ui.Dialogs;
import com.github.engatec.vdl.ui.SvgIcons;
import com.github.engatec.vdl.ui.stage.YoutubeCookiesGeneratorStage;
import com.github.engatec.vdl.validation.InputForm;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class YoutubedlPreferencesController extends ScrollPane implements InputForm {

    private static final Logger LOGGER = LogManager.getLogger(YoutubedlPreferencesController.class);

    private final ApplicationContext ctx = ApplicationContext.getInstance();

    @FXML private FxTextField outputTemplateTextField;

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
    @FXML private Button generateCookieButton;

    @FXML private CheckBox useConfigFileCheckBox;
    @FXML private FxFileChooser configFileChooser;

    @FXML private FxTextField rateLimitTextField;

    public YoutubedlPreferencesController() {
    }

    @FXML
    public void initialize() {
        initGeneralSettings();
        initDownloadSettings();
        initNetworkSettings();
        initAuthenticationSettings();
        initConfigFileSettings();

        bindProperties();
    }

    private void initGeneralSettings() {
        outputTemplateTextField.setTextFormatter(new NotBlankTextFormatter());

        markWatchedCheckbox.setGraphic(SvgIcons.infoWithTooltip("preferences.youtubedl.general.markwatched.tooltip"));
        markWatchedCheckbox.setContentDisplay(ContentDisplay.RIGHT);

        cookiesFileChooser.setButtonText(ctx.getLocalizedString("button.filechoose"));
        cookiesFileChooser.disableProperty().bind(readCookiesCheckbox.selectedProperty().not());
        generateCookieButton.setOnAction(this::handleGenerateCookieButtonClick);
    }

    private void initDownloadSettings() {
        rateLimitTextField.setTextFormatter(RegexTextFormatter.of("^(0|[1-9][0-9]{0,9}[KkMmGg]?)$"));
        rateLimitTextField.setHint(ctx.getLocalizedString("preferences.youtubedl.download.ratelimit.hint"));
    }

    private void initNetworkSettings() {
        proxyUrlTextField.textProperty().addListener((observable, oldValue, newValue) -> proxyUrlTextField.clearError());
        proxyUrlTextField.setHint(ctx.getLocalizedString("preferences.youtubedl.network.proxy.hint"));

        socketTimoutTextField.setTextFormatter(new IntegerTextFormatter());
        socketTimoutTextField.setHint(ctx.getLocalizedString("preferences.youtubedl.network.socket.timeout.hint"));

        sourceAddressTextField.textProperty().addListener((observable, oldValue, newValue) -> sourceAddressTextField.clearError());
        sourceAddressTextField.setHint(ctx.getLocalizedString("preferences.youtubedl.network.sourceaddress.hint"));

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
        videoPasswordTextField.setHint(ctx.getLocalizedString("preferences.youtubedl.authentication.videopassword.hint"));
    }

    private void initConfigFileSettings() {
        useConfigFileCheckBox.setGraphic(SvgIcons.infoWithTooltip("preferences.youtubedl.configfile.tooltip"));
        useConfigFileCheckBox.setContentDisplay(ContentDisplay.RIGHT);

        configFileChooser.setButtonText(ctx.getLocalizedString("button.filechoose"));
        configFileChooser.disableProperty().bind(useConfigFileCheckBox.selectedProperty().not());
    }

    private void bindProperties() {
        ConfigRegistry configRegistry = ctx.getConfigRegistry();
        proxyUrlTextField.textProperty().bindBidirectional(configRegistry.get(ProxyUrlPref.class).getProperty());
        socketTimoutTextField.textProperty().bindBidirectional(configRegistry.get(SocketTimeoutPref.class).getProperty());
        sourceAddressTextField.textProperty().bindBidirectional(configRegistry.get(SourceAddressPref.class).getProperty());
        forceIpV4CheckBox.selectedProperty().bindBidirectional(configRegistry.get(ForceIpV4Pref.class).getProperty());
        forceIpV6CheckBox.selectedProperty().bindBidirectional(configRegistry.get(ForceIpV6Pref.class).getProperty());

        rateLimitTextField.textProperty().bindBidirectional(configRegistry.get(RateLimitPref.class).getProperty());

        usernameTextField.textProperty().bindBidirectional(configRegistry.get(AuthUsernamePref.class).getProperty());
        passwordTextField.textProperty().bindBidirectional(configRegistry.get(AuthPasswordPref.class).getProperty());
        twoFactorTextField.textProperty().bindBidirectional(configRegistry.get(TwoFactorCodePref.class).getProperty());
        videoPasswordTextField.textProperty().bindBidirectional(configRegistry.get(VideoPasswordPref.class).getProperty());
        netrcCheckbox.selectedProperty().bindBidirectional(configRegistry.get(NetrcPref.class).getProperty());

        outputTemplateTextField.textProperty().bindBidirectional(configRegistry.get(OutputTemplatePref.class).getProperty());
        markWatchedCheckbox.selectedProperty().bindBidirectional(configRegistry.get(MarkWatchedPref.class).getProperty());
        noContinueCheckbox.selectedProperty().bindBidirectional(configRegistry.get(NoContinuePref.class).getProperty());
        noPartCheckBox.selectedProperty().bindBidirectional(configRegistry.get(NoPartPref.class).getProperty());
        noMTimeCheckBox.selectedProperty().bindBidirectional(configRegistry.get(NoMTimePref.class).getProperty());

        readCookiesCheckbox.selectedProperty().bindBidirectional(configRegistry.get(ReadCookiesPref.class).getProperty());
        cookiesFileChooser.pathProperty().bindBidirectional(configRegistry.get(CookiesFileLocationPref.class).getProperty());

        useConfigFileCheckBox.selectedProperty().bindBidirectional(configRegistry.get(UseConfigFilePref.class).getProperty());
        configFileChooser.pathProperty().bindBidirectional(configRegistry.get(ConfigFilePathPref.class).getProperty());
    }

    private void handleGenerateCookieButtonClick(ActionEvent event) {
        Consumer<String> onCookiesReadyCallback = cookiesContent -> {
            Path cookiesPath = ApplicationContext.getInstance().getAppDataDir().resolve("cookies.txt");
            try {
                Files.deleteIfExists(cookiesPath);
                Files.writeString(cookiesPath, cookiesContent);
                readCookiesCheckbox.setSelected(true);
                cookiesFileChooser.pathProperty().setValue(cookiesPath.toString());
            } catch (IOException ex) {
                LOGGER.warn(ex.getMessage(), ex);
            }
        };
        Dialogs.infoWithYesNoButtons(
                "preferences.youtubedl.cookies.generate.info",
                () -> new YoutubeCookiesGeneratorStage(onCookiesReadyCallback).modal(this.getScene().getWindow()).showAndWait(),
                null
        );

        event.consume();
    }

    @Override
    public boolean hasErrors() {
        boolean hasErrors = false;

        String proxyUrl = proxyUrlTextField.getText();
        if (StringUtils.isNotBlank(proxyUrl) && !new UrlValidator(new String[] {"http", "https", "socks4", "socks5"}).isValid(proxyUrl)) {
            proxyUrlTextField.setError(ctx.getLocalizedString("preferences.youtubedl.network.proxy.error"));
            hasErrors = true;
        }

        String sourceAddressText = sourceAddressTextField.getText();
        InetAddressValidator ipValidator = InetAddressValidator.getInstance();
        if (StringUtils.isNotBlank(sourceAddressText) && !(ipValidator.isValidInet4Address(sourceAddressText) || ipValidator.isValidInet6Address(sourceAddressText))) {
            sourceAddressTextField.setError(ctx.getLocalizedString("preferences.youtubedl.network.sourceaddress.error"));
            hasErrors = true;
        }

        if (StringUtils.isBlank(usernameTextField.getText()) && StringUtils.isNotBlank(passwordTextField.getText())) {
            usernameTextField.setError(ctx.getLocalizedString("preferences.youtubedl.authentication.username.error"));
            hasErrors = true;
        }

        if (StringUtils.isBlank(passwordTextField.getText()) && StringUtils.isNotBlank(usernameTextField.getText())) {
            passwordTextField.setError(ctx.getLocalizedString("preferences.youtubedl.authentication.password.error"));
            hasErrors = true;
        }

        return hasErrors;
    }
}
