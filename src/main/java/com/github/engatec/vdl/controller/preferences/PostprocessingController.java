package com.github.engatec.vdl.controller.preferences;

import java.text.ParseException;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.github.engatec.vdl.controller.StageAwareController;
import com.github.engatec.vdl.core.ApplicationContext;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

public class PostprocessingController extends StageAwareController {

    private static final Pattern TIME_PATTERN = Pattern.compile("^[0-9]?[0-9]?:[0-5]?[0-9]?:[0-5]?[0-9]?$");
    private static final String DEFAULT_TIME = "00:00:00";

    @FXML private CheckBox fragmentCheckbox;
    @FXML private HBox fragmentTimeRangeWrapper;
    @FXML private TextField fragmentFromTextField;
    @FXML private TextField fragmentToTextField;

    private PostprocessingController() {
    }

    public PostprocessingController(Stage stage) {
        super(stage);
    }

    @FXML
    public void initialize() throws ParseException {
        stage.setTitle(ApplicationContext.INSTANCE.getResourceBundle().getString("component.downloadgrid.postprocessing"));
        initFragment();
    }

    private void initFragment() {
        fragmentTimeRangeWrapper.disableProperty().bind(fragmentCheckbox.selectedProperty().not());
        initFragmentTimeTextField(fragmentFromTextField);
        initFragmentTimeTextField(fragmentToTextField);
    }

    private void initFragmentTimeTextField(final TextField tf) {
        tf.setText(DEFAULT_TIME);
        tf.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!TIME_PATTERN.matcher(newValue).matches()) {
                ((StringProperty) observable).set(oldValue);
            }
        });
        tf.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                String normalizedTime = Arrays.stream(tf.getText().split(":", -1))
                        .map(StringUtils::strip)
                        .map(it -> StringUtils.leftPad(it, 2, '0'))
                        .collect(Collectors.joining(":"));
                tf.setText(normalizedTime);
            }
        });
    }
}
