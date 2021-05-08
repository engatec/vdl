package com.github.engatec.vdl.ui.data;

public class ComboBoxValueHolder<T> {

    private final String key;
    private final T value;

    public ComboBoxValueHolder(String key, T value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public T getValue() {
        return value;
    }

    @Override
    public String toString() {
        return key;
    }
}
