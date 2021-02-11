package com.github.engatec.vdl.core.preferences.category;

import javafx.scene.Node;
import javafx.stage.Stage;

public abstract class Category {

    protected String title;
    protected Node node;

    public abstract Node buildCategoryUi(Stage stage);
    public abstract void readPreferences();
    public abstract void savePreferences();

    public Category(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }
}
