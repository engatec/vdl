package com.github.engatec.vdl.controller.preferences;

import java.text.ParseException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.github.engatec.vdl.controller.StageAwareController;
import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.model.downloadable.Downloadable;
import com.github.engatec.vdl.model.postprocessing.FragmentCutPostprocessing;
import com.github.engatec.vdl.model.postprocessing.Postprocessing;
import com.github.engatec.vdl.ui.Dialogs;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

public class PostprocessingController extends StageAwareController {

    private static final Pattern TIME_PATTERN = Pattern.compile("^[0-9]?[0-9]?:[0-5]?[0-9]?:[0-5]?[0-9]?$");
    private static final String DEFAULT_TIME = "00:00:00";

    private Downloadable downloadable;

    @FXML private CheckBox fragmentCheckbox;
    @FXML private HBox fragmentTimeRangeWrapper;
    @FXML private TextField fragmentFromTextField;
    @FXML private TextField fragmentToTextField;

    @FXML private Button okButton;
    @FXML private Button cancelButton;

    private PostprocessingController() {
    }

    public PostprocessingController(Stage stage, Downloadable downloadable) {
        super(stage);
        this.downloadable = downloadable;
    }

    @FXML
    public void initialize() throws ParseException {
        stage.setTitle(ApplicationContext.INSTANCE.getResourceBundle().getString("component.downloadgrid.postprocessing"));
        initFragment();

        okButton.setOnAction(this::handleOkButtonClick);
        cancelButton.setOnAction(this::handleCancelButtonClick);
    }

    private void initFragment() {
        fragmentTimeRangeWrapper.disableProperty().bind(fragmentCheckbox.selectedProperty().not());
        initFragmentTimeTextField(fragmentFromTextField);
        initFragmentTimeTextField(fragmentToTextField);

        ListUtils.emptyIfNull(downloadable.getPostprocessingSteps()).stream()
                .filter(it -> it instanceof FragmentCutPostprocessing)
                .map(it -> (FragmentCutPostprocessing) it)
                .findFirst()
                .ifPresent(it -> {
                    fragmentCheckbox.setSelected(true);
                    fragmentFromTextField.setText(it.getStartTimeAsString());
                    fragmentToTextField.setText(it.getEndTimeAsString());
                });
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
        List<Postprocessing> tempPostprocessingList = new ArrayList<>();

        if (fragmentCheckbox.isSelected()) {
            LocalTime startTime = LocalTime.parse(fragmentFromTextField.getText());
            LocalTime endTime = LocalTime.parse(fragmentToTextField.getText());
            if (startTime.isAfter(endTime)) {
                Dialogs.error("stage.postprocessing.fragmen.error.starttimeafterendtime");
                return;
            }
            tempPostprocessingList.add(FragmentCutPostprocessing.newInstance(startTime, endTime));
        }

        downloadable.setPostprocessingSteps(tempPostprocessingList);
        stage.close();
        event.consume();
    }

    private void handleCancelButtonClick(ActionEvent e) {
        stage.close();
        e.consume();
    }
}
