package com.github.engatec.vdl.preference.model;

import java.util.Objects;

import javafx.scene.control.TableColumn;

public record TableColumnConfigModel(int id, int pos, double width, TableColumn.SortType sortType) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TableColumnConfigModel that = (TableColumnConfigModel) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
