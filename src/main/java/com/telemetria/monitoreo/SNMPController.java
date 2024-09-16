package com.telemetria.monitoreo;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class SNMPController {

    @FXML
    private BarChart<String, Number> barChart;

    @FXML
    private Label infoLabel;

    private static String community = "ingsoft";
    private static String ipAddress = "192.168.1.97";
    private static int port = 161;
    private static int timeout = 2000;
    private static int retries = 3;

    private static String sysLocationOID = "1.3.6.1.2.1.1.6.0";
    private static String sysContactOID = "1.3.6.1.2.1.1.4.0";
    private static String cpuOID = "1.3.6.1.4.1.2021.11.10.0";
    private static String memTotalOID = "1.3.6.1.4.1.2021.4.5.0";
    private static String memUsedOID = "1.3.6.1.4.1.2021.4.11.0";
    private static String storageTotalOID = "1.3.6.1.4.1.2021.4.3.0";
    private static String storageUsedOID = "1.3.6.1.4.1.2021.4.6.0";

    @FXML
    public void showContact() {
        hideAll();
        String contact = getSNMPValue(sysContactOID);
        if (contact != null) {
            infoLabel.setText("Contacto: " + contact);
        } else {
            infoLabel.setText("Error al obtener el contacto.");
        }
        infoLabel.setVisible(true);
    }

    @FXML
    public void showEmail() {
        hideAll();
        String contact = getSNMPValue(sysContactOID);
        if (contact != null) {
            infoLabel.setText("Contacto: " + contact);
        } else {
            infoLabel.setText("Error al obtener el contacto.");
        }
        infoLabel.setVisible(true);
    }

    @FXML
    public void showLocation() {
        hideAll();
        String location = getSNMPValue(sysLocationOID);
        if (location != null) {
            infoLabel.setText("Localización: " + location);
        } else {
            infoLabel.setText("Error al obtener la localización.");
        }
        infoLabel.setVisible(true);
    }

    @FXML
    public void showCPUUsage() {
        hideAll();
        updateBarChart("Uso de CPU", cpuOID);
    }

    @FXML
    public void showMemoryUsage() {
        hideAll();
        updateBarChart("Uso de Memoria", memTotalOID, memUsedOID);
    }

    @FXML
    public void showStorageUsage() {
        hideAll();
        updateBarChart("Uso de Almacenamiento", storageTotalOID, storageUsedOID);
    }

    private void updateBarChart(String title, String... oids) {
        barChart.getData().clear();
        barChart.setTitle(title);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Valores SNMP");

        try {
            Snmp snmp = setUpSnmp();
            CommunityTarget target = setUpTarget();

            for (String oid : oids) {
                String value = getSNMPValue(snmp, target, oid);
                series.getData().add(new XYChart.Data<>(oid, Double.parseDouble(value)));
            }

            snmp.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        barChart.getData().add(series);
        barChart.setVisible(true);
    }

    private String getSNMPValue(String oid) {
        try {
            Snmp snmp = setUpSnmp();
            CommunityTarget target = setUpTarget();

            PDU pdu = new PDU();
            pdu.add(new VariableBinding(new OID(oid)));
            pdu.setType(PDU.GET);

            ResponseEvent response = snmp.send(pdu, target);

            snmp.close();

            if (response != null && response.getResponse() != null) {
                return response.getResponse().get(0).getVariable().toString();
            } else {
                System.out.println("Error: No se recibió respuesta.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void hideAll() {
        infoLabel.setVisible(false);
        barChart.setVisible(false);
    }

    private Snmp setUpSnmp() throws Exception {
        TransportMapping<UdpAddress> transport = new DefaultUdpTransportMapping();
        transport.listen();
        return new Snmp(transport);
    }

    private CommunityTarget setUpTarget() {
        Address targetAddress = new UdpAddress(ipAddress + "/" + port);
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(community));
        target.setAddress(targetAddress);
        target.setVersion(SnmpConstants.version2c);
        target.setRetries(retries);
        target.setTimeout(timeout);
        return target;
    }

    private String getSNMPValue(Snmp snmp, CommunityTarget target, String oid) {
        try {
            PDU pdu = new PDU();
            pdu.add(new VariableBinding(new OID(oid)));
            pdu.setType(PDU.GET);

            ResponseEvent response = snmp.send(pdu, target);

            if (response != null && response.getResponse() != null) {
                return response.getResponse().get(0).getVariable().toString();
            } else {
                System.out.println("Error: No se recibió respuesta");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0";
    }
}