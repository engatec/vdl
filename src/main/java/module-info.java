open module engatec.vdl {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.prefs;
    requires java.net.http;

    requires org.apache.commons.lang3;
    requires org.apache.commons.collections4;
    requires org.apache.commons.io;
    requires org.apache.logging.log4j;
    requires commons.validator;

    requires com.fasterxml.jackson.databind;

    requires engatec.fxcontrol;

    exports com.github.engatec.vdl;
}
