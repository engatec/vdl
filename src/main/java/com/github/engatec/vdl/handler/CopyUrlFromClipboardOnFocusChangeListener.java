package com.github.engatec.vdl.handler;

import java.net.MalformedURLException;
import java.net.URL;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.model.preferences.wrapper.general.AutoSearchFromClipboardPref;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import org.apache.commons.lang3.StringUtils;

public class CopyUrlFromClipboardOnFocusChangeListener implements ChangeListener<Boolean> {

    private final TextField textField;
    private final Button button;

    private final Clipboard systemClipboard = Clipboard.getSystemClipboard();

    public CopyUrlFromClipboardOnFocusChangeListener(TextField textField, Button button) {
        this.textField = textField;
        this.button = button;
    }

    @Override
    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        if (!newValue) {
            return;
        }

        if (!ApplicationContext.INSTANCE.getConfigRegistry().get(AutoSearchFromClipboardPref.class).getValue()) {
            return;
        }

        if (!button.isVisible()) { // Search is already in progress
            return;
        }

        try {
            String clipboardText = systemClipboard.getString();
            String currentUrlText = textField.getText();
            if (StringUtils.isBlank(clipboardText) || clipboardText.equals(currentUrlText)) {
                return;
            }

            URL url = new URL(clipboardText);
            textField.setText(url.toString());
            button.fire();
        } catch (MalformedURLException ignored) {
        }
    }
}
