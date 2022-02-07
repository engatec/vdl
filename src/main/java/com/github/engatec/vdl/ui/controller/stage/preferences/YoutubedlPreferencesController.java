package com.github.engatec.vdl.ui.controller.stage.preferences;

import com.github.engatec.fxcontrols.FxFileChooser;
import com.github.engatec.fxcontrols.FxTextField;
import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.preferences.ConfigRegistry;
import com.github.engatec.vdl.handler.textformatter.IntegerTextFormatter;
import com.github.engatec.vdl.handler.textformatter.NotBlankTextFormatter;
import com.github.engatec.vdl.handler.textformatter.RegexTextFormatter;
import com.github.engatec.vdl.preference.property.youtubedl.AuthPasswordConfigProperty;
import com.github.engatec.vdl.preference.property.youtubedl.AuthUsernameConfigProperty;
import com.github.engatec.vdl.preference.property.youtubedl.ConfigFilePathConfigProperty;
import com.github.engatec.vdl.preference.property.youtubedl.CookiesFileLocationConfigProperty;
import com.github.engatec.vdl.preference.property.youtubedl.EmbedSubtitlesConfigProperty;
import com.github.engatec.vdl.preference.property.youtubedl.ForceIpV4ConfigProperty;
import com.github.engatec.vdl.preference.property.youtubedl.ForceIpV6ConfigProperty;
import com.github.engatec.vdl.preference.property.youtubedl.MarkWatchedConfigProperty;
import com.github.engatec.vdl.preference.property.youtubedl.NetrcConfigProperty;
import com.github.engatec.vdl.preference.property.youtubedl.NoContinueConfigProperty;
import com.github.engatec.vdl.preference.property.youtubedl.NoMTimeConfigProperty;
import com.github.engatec.vdl.preference.property.youtubedl.NoPartConfigProperty;
import com.github.engatec.vdl.preference.property.youtubedl.OutputTemplateConfigProperty;
import com.github.engatec.vdl.preference.property.youtubedl.PreferredSubtitlesConfigProperty;
import com.github.engatec.vdl.preference.property.youtubedl.ProxyUrlConfigProperty;
import com.github.engatec.vdl.preference.property.youtubedl.RateLimitConfigProperty;
import com.github.engatec.vdl.preference.property.youtubedl.ReadCookiesConfigProperty;
import com.github.engatec.vdl.preference.property.youtubedl.SocketTimeoutConfigProperty;
import com.github.engatec.vdl.preference.property.youtubedl.SourceAddressConfigProperty;
import com.github.engatec.vdl.preference.property.youtubedl.TwoFactorCodeConfigProperty;
import com.github.engatec.vdl.preference.property.youtubedl.UseConfigFileConfigProperty;
import com.github.engatec.vdl.preference.property.youtubedl.VideoPasswordConfigProperty;
import com.github.engatec.vdl.preference.property.youtubedl.WriteSubtitlesConfigProperty;
import com.github.engatec.vdl.ui.SvgIcons;
import com.github.engatec.vdl.validation.InputForm;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.apache.commons.validator.routines.UrlValidator;

public class YoutubedlPreferencesController extends ScrollPane implements InputForm {

    private final ApplicationContext ctx = ApplicationContext.getInstance();

    @FXML private FxTextField outputTemplateTextField;

    @FXML private FxTextField proxyUrlTextField;
    @FXML private FxTextField socketTimoutTextField;
    @FXML private FxTextField sourceAddressTextField;

    @FXML private CheckBox writeSubtitlesCheckbox;
    @FXML private CheckBox embedSubtitlesCheckbox;
    @FXML private Label subtitlesLanguagesLabel;
    @FXML private FxTextField subtitlesLanguagesTextField;

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

    @FXML private FxTextField rateLimitTextField;

    public YoutubedlPreferencesController() {
    }

    @FXML
    public void initialize() {
        initGeneralSettings();
        initSubtitlesSettings();
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
    }

    private void initSubtitlesSettings() {
        BooleanBinding noNeedDownloadSubtitlesBinding = writeSubtitlesCheckbox.selectedProperty().not();
        embedSubtitlesCheckbox.disableProperty().bind(noNeedDownloadSubtitlesBinding);
        subtitlesLanguagesLabel.disableProperty().bind(noNeedDownloadSubtitlesBinding);
        subtitlesLanguagesTextField.disableProperty().bind(noNeedDownloadSubtitlesBinding);

        subtitlesLanguagesTextField.setHint(ctx.getLocalizedString("preferences.youtubedl.subtitles.languages.hint"));
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
        proxyUrlTextField.textProperty().bindBidirectional(configRegistry.get(ProxyUrlConfigProperty.class).getProperty());
        socketTimoutTextField.textProperty().bindBidirectional(configRegistry.get(SocketTimeoutConfigProperty.class).getProperty());
        sourceAddressTextField.textProperty().bindBidirectional(configRegistry.get(SourceAddressConfigProperty.class).getProperty());
        forceIpV4CheckBox.selectedProperty().bindBidirectional(configRegistry.get(ForceIpV4ConfigProperty.class).getProperty());
        forceIpV6CheckBox.selectedProperty().bindBidirectional(configRegistry.get(ForceIpV6ConfigProperty.class).getProperty());

        writeSubtitlesCheckbox.selectedProperty().bindBidirectional(configRegistry.get(WriteSubtitlesConfigProperty.class).getProperty());
        embedSubtitlesCheckbox.selectedProperty().bindBidirectional(configRegistry.get(EmbedSubtitlesConfigProperty.class).getProperty());
        subtitlesLanguagesTextField.textProperty().bindBidirectional(configRegistry.get(PreferredSubtitlesConfigProperty.class).getProperty());

        rateLimitTextField.textProperty().bindBidirectional(configRegistry.get(RateLimitConfigProperty.class).getProperty());

        usernameTextField.textProperty().bindBidirectional(configRegistry.get(AuthUsernameConfigProperty.class).getProperty());
        passwordTextField.textProperty().bindBidirectional(configRegistry.get(AuthPasswordConfigProperty.class).getProperty());
        twoFactorTextField.textProperty().bindBidirectional(configRegistry.get(TwoFactorCodeConfigProperty.class).getProperty());
        videoPasswordTextField.textProperty().bindBidirectional(configRegistry.get(VideoPasswordConfigProperty.class).getProperty());
        netrcCheckbox.selectedProperty().bindBidirectional(configRegistry.get(NetrcConfigProperty.class).getProperty());

        outputTemplateTextField.textProperty().bindBidirectional(configRegistry.get(OutputTemplateConfigProperty.class).getProperty());
        markWatchedCheckbox.selectedProperty().bindBidirectional(configRegistry.get(MarkWatchedConfigProperty.class).getProperty());
        noContinueCheckbox.selectedProperty().bindBidirectional(configRegistry.get(NoContinueConfigProperty.class).getProperty());
        noPartCheckBox.selectedProperty().bindBidirectional(configRegistry.get(NoPartConfigProperty.class).getProperty());
        noMTimeCheckBox.selectedProperty().bindBidirectional(configRegistry.get(NoMTimeConfigProperty.class).getProperty());

        readCookiesCheckbox.selectedProperty().bindBidirectional(configRegistry.get(ReadCookiesConfigProperty.class).getProperty());
        cookiesFileChooser.pathProperty().bindBidirectional(configRegistry.get(CookiesFileLocationConfigProperty.class).getProperty());

        useConfigFileCheckBox.selectedProperty().bindBidirectional(configRegistry.get(UseConfigFileConfigProperty.class).getProperty());
        configFileChooser.pathProperty().bindBidirectional(configRegistry.get(ConfigFilePathConfigProperty.class).getProperty());
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
