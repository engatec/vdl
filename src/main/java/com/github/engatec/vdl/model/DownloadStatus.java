package com.github.engatec.vdl.model;

public enum DownloadStatus {

    READY("Ready"),
    IN_PROGRESS("In progress"),
    FAILED("Failed"),
    FINISHED("Finished");

    private final String uiValue;

    DownloadStatus(String uiValue) {
        this.uiValue = uiValue;
    }

    public String getUiValue() {
        return uiValue;
    }


    @Override
    public String toString() {
        return uiValue;
    }
}
