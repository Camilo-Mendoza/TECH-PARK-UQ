package com.techpark.controller;

import com.techpark.service.ParkDataService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class MainController {

    @FXML private StackPane contenidoCentral;
    @FXML private Label statusLabel;
    @FXML private Button btnMapa;
    @FXML private Button btnVisitante;
    @FXML private Button btnAdmin;
    @FXML private Button btnReportes;

    private final ParkDataService dataService = new ParkDataService();

    @FXML
    public void initialize() {
        dataService.cargarDatos();
        mostrarMapa();
    }

    @FXML
    public void cargarDatos() {
        dataService.cargarDatos();
        statusLabel.setText("Datos cargados correctamente");
        mostrarMapa();
    }

    @FXML
    public void mostrarMapa() {
        resaltarBoton(btnMapa);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/map-view.fxml"));
            MapController controller = new MapController(dataService);
            loader.setController(controller);
            Node vista = loader.load();
            contenidoCentral.getChildren().setAll(vista);
            statusLabel.setText("🗺 Mapa del parque");
        } catch (Exception e) {
            statusLabel.setText("Error cargando mapa: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void mostrarVisitante() {
    resaltarBoton(btnVisitante);
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/visitor-view.fxml"));
        VisitorController controller = new VisitorController(dataService);
        loader.setController(controller);
        Node vista = loader.load();
        contenidoCentral.getChildren().setAll(vista);
        statusLabel.setText("👤 Panel de visitante");
    } catch (Exception e) {
        statusLabel.setText("Error: " + e.getMessage());
        e.printStackTrace();
    }
    
    }

    @FXML
    public void mostrarAdmin() {
        resaltarBoton(btnAdmin);
        statusLabel.setText("⚙ Panel de administrador — próximamente");
    }

    @FXML
    public void mostrarReportes() {
        resaltarBoton(btnReportes);
        statusLabel.setText(" Reportes — próximamente");
    }

    private void resaltarBoton(Button activo) {
        String inactivo = "-fx-background-color: transparent; -fx-text-fill: #cbd5e1; -fx-padding: 12 16; -fx-alignment: CENTER-LEFT;";
        String activoStyle = "-fx-background-color: #2563eb; -fx-text-fill: white; -fx-padding: 12 16; -fx-alignment: CENTER-LEFT;";
        btnMapa.setStyle(inactivo);
        btnVisitante.setStyle(inactivo);
        btnAdmin.setStyle(inactivo);
        btnReportes.setStyle(inactivo);
        activo.setStyle(activoStyle);
    }
}