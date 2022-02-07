package com.github.engatec.vdl.ui.controller.stage.preferences;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.preferences.ConfigRegistry;
import com.github.engatec.vdl.preference.property.general.VdlStartupUpdatesCheckConfigProperty;
import com.github.engatec.vdl.preference.property.general.YoutubeDlStartupUpdatesCheckConfigProperty;
import com.github.engatec.vdl.preference.property.general.YtdlpStartupUpdatesCheckConfigProperty;
import com.github.engatec.vdl.validation.InputForm;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;

public class UpdatesPreferencesController extends ScrollPane implements InputForm {

    private final ApplicationContext ctx = ApplicationContext.getInstance();
    private final ConfigRegistry configRegistry = ctx.getConfigRegistry();

    @FXML private CheckBox vdlStartupUpdatesCheckBox;
    @FXML private CheckBox youtubeDlStartupUpdatesCheckBox;
    @FXML private CheckBox ytdlpStartupUpdatesCheckBox;

    @FXML
    public void initialize() {
        vdlStartupUpdatesCheckBox.selectedProperty().bindBidirectional(configRegistry.get(VdlStartupUpdatesCheckConfigProperty.class).getProperty());
        youtubeDlStartupUpdatesCheckBox.selectedProperty().bindBidirectional(configRegistry.get(YoutubeDlStartupUpdatesCheckConfigProperty.class).getProperty());
        ytdlpStartupUpdatesCheckBox.selectedProperty().bindBidirectional(configRegistry.get(YtdlpStartupUpdatesCheckConfigProperty.class).getProperty());
    }

    @Override
    public boolean hasErrors() {
        return false;
    }
}
