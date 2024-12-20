package com.github.engatec.vdl.ui.preference;

import com.github.engatec.vdl.ui.validation.InputForm;
import javafx.scene.Node;
import javafx.stage.Stage;

public abstract class Category implements InputForm {

    protected String title;
    protected Node node;

    public abstract Node buildCategoryUi(Stage stage);

    public Category(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }
}
