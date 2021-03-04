package com.github.engatec.vdl.controller.postprocessing;

import java.text.ParseException;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.github.engatec.vdl.controller.StageAwareController;
import com.github.engatec.vdl.model.postprocessing.FragmentCutPostprocessing;
import com.github.engatec.vdl.model.postprocessing.Postprocessing;
import com.github.engatec.vdl.ui.Dialogs;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

public class FragmentController extends StageAwareController {

    private static final Pattern TIME_PATTERN = Pattern.compile("^[0-9]?[0-9]?:[0-5]?[0-9]?:[0-5]?[0-9]?$");
    private static final String DEFAULT_TIME = "00:00:00";

    private FragmentCutPostprocessing model;
    private Consumer<? super Postprocessing> okClickCallback;

    @FXML private TextField fragmentFromTextField;
    @FXML private TextField fragmentToTextField;

    @FXML private Button okButton;
    @FXML private Button cancelButton;

    private FragmentController() {
    }

    public FragmentController(Stage stage, FragmentCutPostprocessing model, Consumer<? super Postprocessing> okClickCallback) {
        super(stage);
        this.model = model;
        this.okClickCallback = okClickCallback;
    }

    @FXML
    public void initialize() throws ParseException {
        initFragment();

        okButton.setOnAction(this::handleOkButtonClick);
        cancelButton.setOnAction(this::handleCancelButtonClick);
    }

    private void initFragment() {
        initFragmentTimeTextField(fragmentFromTextField);
        initFragmentTimeTextField(fragmentToTextField);

        if (model != null) {
            fragmentFromTextField.setText(model.getStartTimeAsString());
            fragmentToTextField.setText(model.getEndTimeAsString());
        }
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

    private void handleOkButtonClick(ActionEvent event) {
        LocalTime startTime = LocalTime.parse(fragmentFromTextField.getText());
        LocalTime endTime = LocalTime.parse(fragmentToTextField.getText());
        if (endTime.isAfter(startTime)) {
            okClickCallback.accept(FragmentCutPostprocessing.newInstance(startTime, endTime));
            stage.close();
        } else {
            Dialogs.error("stage.postprocessing.fragment.error.timerange");
        }
        event.consume();
    }

    private void handleCancelButtonClick(ActionEvent e) {
        stage.close();
        e.consume();
    }
}
