package com.github.engatec.vdl.handler.textformatter;

import javafx.scene.control.TextFormatter;
import javafx.util.converter.DefaultStringConverter;

/**
 * TextFormatter to add line separator if a URL has been pasted.
 * Paste determination is very primitive, just checking if the change is more than a couple of symbols (meaning it wasn't typed with a keyboard).
 */
public class NewLineOnUrlPasteTextFormatter extends TextFormatter<String> {

    public NewLineOnUrlPasteTextFormatter() {
        this(null);
    }

    public NewLineOnUrlPasteTextFormatter(String defaultValue) {
        super(
                new DefaultStringConverter(),
                defaultValue,
                change -> {
                    String text = change.getText();
                    if (text.length() > 5) {
                        change.setText(text + System.lineSeparator());
                        int newCaretPosition = change.getControlNewText().length();
                        change.selectRange(newCaretPosition, newCaretPosition);
                    }
                    return change;
                }
        );
    }
}
