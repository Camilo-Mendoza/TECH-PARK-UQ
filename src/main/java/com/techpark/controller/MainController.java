package com.techpark.controller;

import com.techpark.controller.LoginController.RolUsuario;
import com.techpark.model.Visitante;
import com.techpark.service.ParkDataService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.util.Optional;

public class MainController {

    @FXML private StackPane contenidoCentral;
    @FXML private Label statusLabel;
    @FXML private Button btnMapa;
    @FXML private Button btnVisitante;
    @FXML private Button btnAdmin;
    @FXML private Button btnReportes;

    private final ParkDataService dataService = new ParkDataService();
    private Object usuarioActual;
    private RolUsuario rolActual;

    @FXML
    public void initialize() {
        dataService.cargarDatos();
        mostrarLogin();
    }

    // ============ LOGIN ============
    private void mostrarLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login-view.fxml"));
            LoginController loginController = new LoginController(dataService);
            loader.setController(loginController);
            Node vista = loader.load();
            
            loginController.setLoginCallback((rol, usuario) -> {
                this.rolActual = rol;
                this.usuarioActual = usuario;
                despuesDelLogin(rol, usuario);
            });
            
            contenidoCentral.getChildren().setAll(vista);
            statusLabel.setText("🔐 Inicia sesión para continuar");
            
            // Ocultar botones de navegación
            btnMapa.setVisible(false);
            btnVisitante.setVisible(false);
            btnAdmin.setVisible(false);
            btnReportes.setVisible(false);
            
        } catch (Exception e) {
            statusLabel.setText("Error cargando login: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void despuesDelLogin(RolUsuario rol, Object usuario) {
        // Mostrar botones de navegación
        btnMapa.setVisible(true);
        btnVisitante.setVisible(true);
        btnAdmin.setVisible(true);
        btnReportes.setVisible(true);
        
        String nombreUsuario = obtenerNombreUsuario(usuario);
        statusLabel.setText(" Bienvenido: " + nombreUsuario + " (" + rol + ")");
        
        switch (rol) {
            case VISITANTE -> mostrarVisitante();
            case OPERADOR -> mostrarMapa();
            case ADMIN -> mostrarMapa();
        }
    }

    // ============ NUEVO: CERRAR SESIÓN ============
    @FXML
    public void cerrarSesion() {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Cerrar Sesión");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Estás seguro de que deseas cerrar sesión?\nSe guardarán los datos automáticamente.");
        
        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            // Guardar datos antes de cerrar sesión
            dataService.guardarDatos();
            
            // Limpiar usuario actual
            usuarioActual = null;
            rolActual = null;
            
            // Volver al login
            mostrarLogin();
            statusLabel.setText(" Sesión cerrada. Datos guardados.");
        }
    }

    // ============ NUEVO: SALIR DE LA APP ============
    @FXML
    public void salirApp() {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Salir");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Estás seguro de que deseas salir de la aplicación?\nSe guardarán los datos automáticamente.");
        
        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            // Guardar datos antes de salir
            dataService.guardarDatos();
            System.out.println("Cerrando aplicación.... Datos guardados.");
            
            // Cerrar la aplicación
            Platform.exit();
            System.exit(0);
        }
    }

    @FXML
    public void guardarDatos() {
        dataService.guardarDatos();
        statusLabel.setText("💾 Datos guardados correctamente");
    }

    private String obtenerNombreUsuario(Object usuario) {
        if (usuario instanceof Visitante v) return v.getNombre();
        if (usuario instanceof com.techpark.model.Operador o) return o.getNombre();
        if (usuario instanceof com.techpark.model.Administrador a) return a.getNombre();
        return "Usuario";
    }

    @FXML
    public void cargarDatos() {
        dataService.cargarDatos();
        statusLabel.setText("✅ Datos cargados correctamente");
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
            
            // Pasar visitante logueado
            if (usuarioActual instanceof Visitante visitante) {
                controller.setVisitanteInicial(visitante);
            }
            
            loader.setController(controller);
            Node vista = loader.load();
            
            // Forzar carga de datos
            if (usuarioActual instanceof Visitante) {
                controller.cargarDatosVisitantePublico();
            }
            
            contenidoCentral.getChildren().setAll(vista);
            statusLabel.setText("👤 Panel de visitante - " + 
                (usuarioActual instanceof Visitante v ? v.getNombre() : ""));
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
        statusLabel.setText("📊 Reportes — próximamente");
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