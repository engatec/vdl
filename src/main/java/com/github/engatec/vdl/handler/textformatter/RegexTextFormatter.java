package com.github.engatec.vdl.handler.textformatter;

import java.util.regex.Pattern;

import javafx.scene.control.TextFormatter;
import javafx.util.converter.DefaultStringConverter;
import org.apache.commons.lang3.StringUtils;

public class RegexTextFormatter extends TextFormatter<String> {

    private RegexTextFormatter(Pattern pattern) {
        super(
                new DefaultStringConverter(),
                null,
                change -> {
                    String newText = change.getControlNewText();
                    return StringUtils.isEmpty(newText) || pattern.matcher(newText).matches() ? change : null;
                }
        );
    }

    public static RegexTextFormatter of(String regex) {
        return new RegexTextFormatter(Pattern.compile(regex));
    }
}
