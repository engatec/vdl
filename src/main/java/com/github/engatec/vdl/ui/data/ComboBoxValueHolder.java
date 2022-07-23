package com.github.engatec.vdl.ui.data;

public record ComboBoxValueHolder<T>(String key, T value) {

    @Override
    public String toString() {
        return key;
    }
}
