package com.github.engatec.vdl.handler.textformatter;

import java.util.regex.Pattern;

import javafx.scene.control.TextFormatter;
import javafx.util.converter.IntegerStringConverter;
import org.apache.commons.lang3.StringUtils;

public class IntegerTextFormatter extends TextFormatter<Integer> {

    private static final Pattern NUMERIC_PATTERN = Pattern.compile("[1-9][0-9]*");

    public IntegerTextFormatter() {
        this(null);
    }

    public IntegerTextFormatter(Integer defaultValue) {
        super(
                new IntegerStringConverter(),
                defaultValue,
                change -> {
                    String newText = change.getControlNewText();
                    return StringUtils.isBlank(newText) || NUMERIC_PATTERN.matcher(newText).matches() ? change : null;
                }
        );
    }
}
