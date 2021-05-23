package com.github.engatec.vdl.handler.textformatter;

import javafx.scene.control.TextFormatter;
import javafx.util.converter.DefaultStringConverter;
import org.apache.commons.lang3.StringUtils;

/**
 * Formatter forbids to have only space characters in the field. Although empty field is allowed.
 */
public class NotBlankTextFormatter extends TextFormatter<String> {

    public NotBlankTextFormatter() {
        this(null);
    }

    public NotBlankTextFormatter(String defaultValue) {
        super(
                new DefaultStringConverter(),
                defaultValue,
                change -> {
                    String newText = change.getControlNewText();
                    return StringUtils.isEmpty(newText) || StringUtils.isNotBlank(newText) ? change : null;
                }
        );
    }
}
