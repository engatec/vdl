package com.github.engatec.vdl.controller.preferences.youtubedl;

import java.util.ResourceBundle;

import com.github.engatec.vdl.core.ApplicationContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.apache.commons.validator.routines.UrlValidator;

class Validator {

    static boolean hasErrors(Context ctx) {
        boolean hasErrors = false;
        ResourceBundle resourceBundle = ApplicationContext.INSTANCE.getResourceBundle();

        String proxyUrl = ctx.getProxyUrlTextField().getText();
        if (StringUtils.isNotBlank(proxyUrl) && !new UrlValidator(new String[] {"http", "https", "socks4", "socks5"}).isValid(proxyUrl)) {
            ctx.getProxyUrlTextField().setError(resourceBundle.getString("preferences.youtubedl.network.proxy.error"));
            hasErrors = true;
        }

        String sourceAddressText = ctx.getSourceAddressTextField().getText();
        InetAddressValidator ipValidator = InetAddressValidator.getInstance();
        if (StringUtils.isNotBlank(sourceAddressText) && !(ipValidator.isValidInet4Address(sourceAddressText) || ipValidator.isValidInet6Address(sourceAddressText))) {
            ctx.getSourceAddressTextField().setError(resourceBundle.getString("preferences.youtubedl.network.sourceaddress.error"));
            hasErrors = true;
        }

        if (StringUtils.isBlank(ctx.getUsernameTextField().getText()) && StringUtils.isNotBlank(ctx.getPasswordTextField().getText())) {
            ctx.getUsernameTextField().setError(resourceBundle.getString("preferences.youtubedl.authentication.username.error"));
            hasErrors = true;
        }

        if (StringUtils.isBlank(ctx.getPasswordTextField().getText()) && StringUtils.isNotBlank(ctx.getUsernameTextField().getText())) {
            ctx.getPasswordTextField().setError(resourceBundle.getString("preferences.youtubedl.authentication.password.error"));
            hasErrors = true;
        }

        return hasErrors;
    }
}
