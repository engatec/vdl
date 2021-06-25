package com.github.engatec.vdl.controller.preferences.youtubedl;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.preferences.ConfigRegistry;
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
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.ProxyUrlPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.ReadCookiesPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.SocketTimeoutPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.SourceAddressPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.TwoFactorCodePref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.UseConfigFilePref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.VideoPasswordPref;

class Binder {

    static void bind(Context ctx) {
        ConfigRegistry configRegistry = ApplicationContext.INSTANCE.getConfigRegistry();
        ctx.getProxyUrlTextField().textProperty().bindBidirectional(configRegistry.get(ProxyUrlPref.class).getProperty());
        ctx.getSocketTimoutTextField().textProperty().bindBidirectional(configRegistry.get(SocketTimeoutPref.class).getProperty());
        ctx.getSourceAddressTextField().textProperty().bindBidirectional(configRegistry.get(SourceAddressPref.class).getProperty());
        ctx.getForceIpV4CheckBox().selectedProperty().bindBidirectional(configRegistry.get(ForceIpV4Pref.class).getProperty());
        ctx.getForceIpV6CheckBox().selectedProperty().bindBidirectional(configRegistry.get(ForceIpV6Pref.class).getProperty());

        ctx.getUsernameTextField().textProperty().bindBidirectional(configRegistry.get(AuthUsernamePref.class).getProperty());
        ctx.getPasswordTextField().textProperty().bindBidirectional(configRegistry.get(AuthPasswordPref.class).getProperty());
        ctx.getTwoFactorTextField().textProperty().bindBidirectional(configRegistry.get(TwoFactorCodePref.class).getProperty());
        ctx.getVideoPasswordTextField().textProperty().bindBidirectional(configRegistry.get(VideoPasswordPref.class).getProperty());
        ctx.getNetrcCheckbox().selectedProperty().bindBidirectional(configRegistry.get(NetrcPref.class).getProperty());

        ctx.getMarkWatchedCheckbox().selectedProperty().bindBidirectional(configRegistry.get(MarkWatchedPref.class).getProperty());
        ctx.getNoContinueCheckbox().selectedProperty().bindBidirectional(configRegistry.get(NoContinuePref.class).getProperty());
        ctx.getNoPartCheckBox().selectedProperty().bindBidirectional(configRegistry.get(NoPartPref.class).getProperty());
        ctx.getNoMTimeCheckBox().selectedProperty().bindBidirectional(configRegistry.get(NoMTimePref.class).getProperty());

        ctx.getReadCookiesCheckbox().selectedProperty().bindBidirectional(configRegistry.get(ReadCookiesPref.class).getProperty());
        ctx.getCookiesFileChooser().pathProperty().bindBidirectional(configRegistry.get(CookiesFileLocationPref.class).getProperty());

        ctx.getUseConfigFileCheckBox().selectedProperty().bindBidirectional(configRegistry.get(UseConfigFilePref.class).getProperty());
        ctx.getConfigFileChooser().pathProperty().bindBidirectional(configRegistry.get(ConfigFilePathPref.class).getProperty());
    }
}
