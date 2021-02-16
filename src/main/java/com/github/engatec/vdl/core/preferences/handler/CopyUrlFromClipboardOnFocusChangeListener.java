package com.github.engatec.vdl.core.preferences.handler;

import java.net.MalformedURLException;
import java.net.URL;

import com.github.engatec.vdl.core.preferences.ConfigManager;
import com.github.engatec.vdl.model.preferences.general.AutoSearchFromClipboardConfigItem;
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

        if (!ConfigManager.INSTANCE.getValue(new AutoSearchFromClipboardConfigItem())) {
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
