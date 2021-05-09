package com.github.engatec.vdl.ui.component.search;

import com.github.engatec.vdl.controller.component.search.SearchComponentController;
import com.github.engatec.vdl.ui.component.AppComponent;
import javafx.stage.Stage;

public class SearchComponent extends AppComponent<SearchComponentController> {

    public SearchComponent(Stage stage) {
        super(stage);
    }

    @Override
    protected String getFxmlPath() {
        return "/fxml/search/search.fxml";
    }

    @Override
    protected SearchComponentController getController() {
        return new SearchComponentController(stage);
    }
}
