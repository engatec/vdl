package com.github.engatec.vdl.ui.component.controller;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.preference.property.engine.ProxyEnabledConfigProperty;
import com.github.engatec.vdl.preference.property.engine.ProxyUrlConfigProperty;
import com.github.engatec.vdl.ui.stage.core.AboutStage;
import com.github.engatec.vdl.ui.stage.core.PreferencesStage;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

public class ServicebarComponentController extends HBox {

    private final ApplicationContext ctx = ApplicationContext.getInstance();

    private final Stage stage;

    @FXML private CheckBox proxyEnabledCheckBox;
    @FXML private ImageView helpButton;
    @FXML private ImageView preferencesButton;

    public ServicebarComponentController(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        StringProperty proxyUrlProperty = ctx.getConfigRegistry().get(ProxyUrlConfigProperty.class).getProperty();
        BooleanBinding proxyUrlIsNotBlankBinding = Bindings.createBooleanBinding(() -> StringUtils.isNotBlank(proxyUrlProperty.getValue()), proxyUrlProperty);
        proxyEnabledCheckBox.visibleProperty().bind(proxyUrlIsNotBlankBinding);
        proxyEnabledCheckBox.managedProperty().bind(proxyUrlIsNotBlankBinding);
        proxyEnabledCheckBox.selectedProperty().bindBidirectional(ctx.getConfigRegistry().get(ProxyEnabledConfigProperty.class).getProperty());

        helpButton.setOnMouseClicked(e -> {
            new AboutStage().modal(stage).showAndWait();
            e.consume();
        });

        preferencesButton.setOnMouseClicked(e -> {
            new PreferencesStage().modal(stage).showAndWait();
            e.consume();
        });
    }
}
