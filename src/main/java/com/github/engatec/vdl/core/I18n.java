package com.github.engatec.vdl.core;

import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;

public class I18n {

    public static void bindLocaleProperty(StringProperty prop, String key) {
        final ApplicationContext ctx = ApplicationContext.INSTANCE;
        prop.bind(Bindings.createStringBinding(() -> ctx.getResourceBundle().getString(key), ctx.getResourceBundleProperty()));
    }
}
