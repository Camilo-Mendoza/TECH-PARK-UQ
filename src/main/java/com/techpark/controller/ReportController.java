package com.techpark.controller;

import com.techpark.model.*;
import com.techpark.service.ParkDataService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ReportController {

    @FXML private Label lblIngresos;
    @FXML private Label lblTotalAtracciones;
    @FXML private Label lblTotalVisitantes;
    @FXML private Label lblAlertasActivas;
    @FXML private ListView<String> listaTopAtracciones;
    @FXML private ListView<String> listaTiemposEspera;

    private final ParkDataService dataService;

    public ReportController(ParkDataService dataService) {
        this.dataService = dataService;
    }

    @FXML
    public void initialize() {
        refrescarReportes();
    }

    @FXML
    public void refrescarReportes() {
        cargarEstadisticasGenerales();
        cargarTopAtracciones();
        cargarTiemposEspera();
    }

    private void cargarEstadisticasGenerales() {
        int totalAtracciones = dataService.getAtracciones().size();
        int totalVisitantes = dataService.getVisitantes().size();
        
        long alertas = dataService.getAtracciones().stream()
            .filter(a -> a.getEstado() == EstadoAtraccion.EN_MANTENIMIENTO || 
                        a.getEstado() == EstadoAtraccion.CERRADA)
            .count();
        
        double ingresos = totalVisitantes * 15000;
        
        lblTotalAtracciones.setText(String.valueOf(totalAtracciones));
        lblTotalVisitantes.setText(String.valueOf(totalVisitantes));
        lblAlertasActivas.setText(String.valueOf(alertas));
        lblIngresos.setText("$" + String.format("%.0f", ingresos));
    }

    private void cargarTopAtracciones() {
        listaTopAtracciones.getItems().clear();
        
        dataService.getAtracciones().stream()
            .sorted((a1, a2) -> Integer.compare(a2.getContadorVisitantes(), a1.getContadorVisitantes()))
            .forEach(a -> {
                int index = listaTopAtracciones.getItems().size() + 1;
                String medalla = "";
                if (index == 1) medalla = "1. ";
                else if (index == 2) medalla = "2. ";
                else if (index == 3) medalla = "3. ";
                else medalla = index + ". ";
                
                listaTopAtracciones.getItems().add(String.format(
                    "%s%s - %d visitantes | %s",
                    medalla, a.getNombre(), a.getContadorVisitantes(), a.getEstado()));
            });
    }

    private void cargarTiemposEspera() {
        listaTiemposEspera.getItems().clear();
        
        for (Atraccion a : dataService.getAtracciones()) {
            String estadoEmoji = switch (a.getEstado()) {
                case ACTIVA -> "ACTIVA";
                case EN_MANTENIMIENTO -> "MANTENIMIENTO";
                case CERRADA -> "CERRADA";
            };
            
            int espera = a.getTiempoEsperaEstimado();
            int enCola = a.getCola() != null ? a.getCola().tamano() : 0;
            
            listaTiemposEspera.getItems().add(String.format(
                "[%s] %s - %d min espera | %d en cola",
                estadoEmoji, a.getNombre(), espera, enCola));
        }
    }

    @FXML
    public void generarReporte() {
        Reporte reporte = new Reporte();
        reporte.setIngresosDiarios(dataService.getVisitantes().size() * 15000.0);
        
        long cierresClima = dataService.getAtracciones().stream()
            .filter(a -> a.getMotivoCierre() == MotivosCierre.CLIMA).count();
        long enMantenimiento = dataService.getAtracciones().stream()
            .filter(a -> a.getEstado() == EstadoAtraccion.EN_MANTENIMIENTO).count();
        
        reporte.setCierresPorClima((int) cierresClima);
        reporte.setAlertasMantenimiento((int) enMantenimiento);
        
        dataService.getAtracciones().stream()
            .sorted((a1, a2) -> Integer.compare(a2.getContadorVisitantes(), a1.getContadorVisitantes()))
            .limit(3)
            .forEach(a -> reporte.getAtraccionesMasVisitadas().add(a));
        
        for (Atraccion a : dataService.getAtracciones()) {
            reporte.getTiemposPromedioEspera().put(a.getNombre(), (double) a.getTiempoEsperaEstimado());
        }
        
        String textoReporte = reporte.generar();
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Reporte Diario - Tech-Park UQ");
        alert.setHeaderText("Reporte generado exitosamente");
        alert.setContentText(textoReporte);
        alert.showAndWait();
    }
}