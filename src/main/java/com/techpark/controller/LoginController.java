package com.techpark.controller;

import com.techpark.model.Administrador;
import com.techpark.model.Operador;
import com.techpark.model.Visitante;
import com.techpark.service.ParkDataService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginController {

    @FXML private ToggleButton btnVisitante;
    @FXML private ToggleButton btnOperador;
    @FXML private ToggleButton btnAdmin;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;

    private final ParkDataService dataService;
    private final ToggleGroup grupoRoles = new ToggleGroup();
    
    // Callback para notificar al MainController sobre login exitoso
    private LoginCallback callback;

    public LoginController(ParkDataService dataService) {
        this.dataService = dataService;
    }

    @FXML
    public void initialize() {
        // Configurar ToggleGroup para que solo un botón esté seleccionado
        btnVisitante.setToggleGroup(grupoRoles);
        btnOperador.setToggleGroup(grupoRoles);
        btnAdmin.setToggleGroup(grupoRoles);
        
        // Listener para cambiar estilos al seleccionar rol
        grupoRoles.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            actualizarEstilosBotones();
        });
        
        // Permitir iniciar sesión con Enter
        txtPassword.setOnAction(e -> iniciarSesion());
    }

    public void setLoginCallback(LoginCallback callback) {
        this.callback = callback;
    }

    @FXML
    public void iniciarSesion() {
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText();

        // Validar campos vacíos
        if (email.isEmpty() || password.isEmpty()) {
            mostrarError("Por favor completa todos los campos.");
            return;
        }

        RolUsuario rolSeleccionado = obtenerRolSeleccionado();
        
        switch (rolSeleccionado) {
            case VISITANTE -> loginVisitante(email, password);
            case OPERADOR -> loginOperador(email, password);
            case ADMIN -> loginAdmin(email, password);
        }
    }

    private void loginVisitante(String email, String password) {
        for (Visitante v : dataService.getVisitantes()) {
            if (v.login(email, password)) {
                lblError.setText("");
                if (callback != null) {
                    callback.onLoginExitoso(RolUsuario.VISITANTE, v);
                }
                return;
            }
        }
        mostrarError("Visitante no encontrado. Verifica tus credenciales.");
    }

    private void loginOperador(String email, String password) {
        for (Operador o : dataService.getOperadores()) {
            if (o.login(email, password)) {
                lblError.setText("");
                if (callback != null) {
                    callback.onLoginExitoso(RolUsuario.OPERADOR, o);
                }
                return;
            }
        }
        mostrarError("Operador no encontrado. Verifica tus credenciales.");
    }

    private void loginAdmin(String email, String password) {
        for (Administrador a : dataService.getAdministradores()) {
            if (a.login(email, password)) {
                lblError.setText("");
                if (callback != null) {
                    callback.onLoginExitoso(RolUsuario.ADMIN, a);
                }
                return;
            }
        }
        mostrarError("Administrador no encontrado. Verifica tus credenciales.");
    }

    private RolUsuario obtenerRolSeleccionado() {
        if (btnOperador.isSelected()) return RolUsuario.OPERADOR;
        if (btnAdmin.isSelected()) return RolUsuario.ADMIN;
        return RolUsuario.VISITANTE; // Por defecto
    }

    private void actualizarEstilosBotones() {
        String activo = "-fx-background-color: #2563eb; -fx-text-fill: white; -fx-padding: 10 20; -fx-background-radius: 6; -fx-cursor: hand; -fx-font-weight: bold;";
        String inactivo = "-fx-background-color: #94a3b8; -fx-text-fill: white; -fx-padding: 10 20; -fx-background-radius: 6; -fx-cursor: hand;";
        
        btnVisitante.setStyle(btnVisitante.isSelected() ? activo : inactivo);
        btnOperador.setStyle(btnOperador.isSelected() ? activo : inactivo);
        btnAdmin.setStyle(btnAdmin.isSelected() ? activo : inactivo);
    }

    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
    }

    // Enum y callback para comunicación con MainController
    public enum RolUsuario { VISITANTE, OPERADOR, ADMIN }
    
    public interface LoginCallback {
        void onLoginExitoso(RolUsuario rol, Object usuario);
    }
}