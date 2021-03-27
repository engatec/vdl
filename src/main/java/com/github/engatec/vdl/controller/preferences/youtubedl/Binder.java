package com.github.engatec.vdl.controller.preferences.youtubedl;

import com.github.engatec.vdl.core.preferences.ConfigRegistry;
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

class Binder {

    static void bind(Context ctx) {
        ctx.getProxyUrlTextField().textProperty().bindBidirectional(ConfigRegistry.get(ProxyUrlPref.class).getProperty());
        ctx.getSocketTimoutTextField().textProperty().bindBidirectional(ConfigRegistry.get(SocketTimeoutPref.class).getProperty());
        ctx.getSourceAddressTextField().textProperty().bindBidirectional(ConfigRegistry.get(SourceAddressPref.class).getProperty());
        ctx.getForceIpV4CheckBox().selectedProperty().bindBidirectional(ConfigRegistry.get(ForceIpV4Pref.class).getProperty());
        ctx.getForceIpV6CheckBox().selectedProperty().bindBidirectional(ConfigRegistry.get(ForceIpV6Pref.class).getProperty());

        ctx.getUsernameTextField().textProperty().bindBidirectional(ConfigRegistry.get(AuthUsernamePref.class).getProperty());
        ctx.getPasswordTextField().textProperty().bindBidirectional(ConfigRegistry.get(AuthPasswordPref.class).getProperty());

        ctx.getNoMTimeCheckBox().selectedProperty().bindBidirectional(ConfigRegistry.get(NoMTimePref.class).getProperty());

        ctx.getUseConfigFileCheckBox().selectedProperty().bindBidirectional(ConfigRegistry.get(UseConfigFilePref.class).getProperty());
        ctx.getConfigFileTextField().textProperty().bindBidirectional(ConfigRegistry.get(ConfigFilePathPref.class).getProperty());
    }
}
