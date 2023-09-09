package com.github.engatec.vdl.ui.stage.controller.preferences;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.preference.ConfigRegistry;
import com.github.engatec.vdl.preference.property.general.VdlStartupUpdatesCheckConfigProperty;
import com.github.engatec.vdl.preference.property.general.YtdlpStartupUpdatesCheckConfigProperty;
import com.github.engatec.vdl.ui.validation.InputForm;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;

public class UpdatesPreferencesController extends ScrollPane implements InputForm {

    private final ApplicationContext ctx = ApplicationContext.getInstance();
    private final ConfigRegistry configRegistry = ctx.getConfigRegistry();

    @FXML private CheckBox vdlStartupUpdatesCheckBox;
    @FXML private CheckBox ytdlpStartupUpdatesCheckBox;

    @FXML
    public void initialize() {
        vdlStartupUpdatesCheckBox.selectedProperty().bindBidirectional(configRegistry.get(VdlStartupUpdatesCheckConfigProperty.class).getProperty());
        ytdlpStartupUpdatesCheckBox.selectedProperty().bindBidirectional(configRegistry.get(YtdlpStartupUpdatesCheckConfigProperty.class).getProperty());
    }

    @Override
    public boolean hasErrors() {
        return false;
    }
}
