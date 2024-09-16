module com.telemetria.monitoreo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires eu.hansolo.tilesfx;
    requires org.snmp4j;

    opens com.telemetria.monitoreo to javafx.fxml;
    exports com.telemetria.monitoreo;
}