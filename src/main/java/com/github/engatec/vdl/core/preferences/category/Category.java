package com.github.engatec.vdl.core.preferences.category;

import com.github.engatec.vdl.validation.InputForm;
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
