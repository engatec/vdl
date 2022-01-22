open module engatec.vdl {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.prefs;
    requires java.net.http;
    requires java.desktop;

    requires org.apache.commons.lang3;
    requires org.apache.commons.collections4;
    requires org.apache.commons.io;
    requires org.apache.logging.log4j;
    requires commons.validator;

    requires com.fasterxml.jackson.databind;

    requires engatec.fxcontrol;

    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires org.flywaydb.core;
    requires org.mybatis;

    exports com.github.engatec.vdl;
}
