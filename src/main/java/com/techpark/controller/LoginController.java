package com.techpark.controller;

import java.util.Optional;

import com.techpark.controller.LoginController.RolUsuario;
import com.techpark.model.Administrador;
import com.techpark.model.Operador;
import com.techpark.model.Ticket;
import com.techpark.model.TipoTicket;
import com.techpark.model.Visitante;
import com.techpark.service.ParkDataService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

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

    @FXML
    public void mostrarRegistro() {
        // Solo permitir registro como visitante
        if (!btnVisitante.isSelected()) {
            mostrarError("Solo puedes crear cuentas como Visitante.\nSelecciona 'Visitante' primero.");
            return;
        }
        
        // Crear diálogo de registro
        Dialog<Visitante> dialog = new Dialog<>();
        dialog.setTitle("Crear Cuenta - Tech-Park UQ");
        dialog.setHeaderText("Regístrate como nuevo visitante");
        
        // Campos del formulario
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20));
        
        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Nombre completo");
        TextField txtEmailReg = new TextField();
        txtEmailReg.setPromptText("correo@ejemplo.com");
        PasswordField txtPassReg = new PasswordField();
        txtPassReg.setPromptText("Contraseña");
        TextField txtEdad = new TextField();
        txtEdad.setPromptText("Edad");
        TextField txtEstatura = new TextField();
        txtEstatura.setPromptText("Ej: 1.75");
        
        // NUEVO: Campo de saldo inicial
        TextField txtSaldo = new TextField();
        txtSaldo.setPromptText("Ej: 50000");
        txtSaldo.setText("10000"); // Valor por defecto
        
        // Selector de tipo de ticket
        ComboBox<TipoTicket> selectorTicket = new ComboBox<>();
        selectorTicket.getItems().addAll(TipoTicket.values());
        selectorTicket.setValue(TipoTicket.GENERAL);
        selectorTicket.setPrefWidth(200);
        
        // Celda personalizada para mostrar descripción
        selectorTicket.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(TipoTicket tipo, boolean empty) {
                super.updateItem(tipo, empty);
                if (empty || tipo == null) {
                    setText(null);
                } else {
                    setText(switch (tipo) {
                        case GENERAL -> "🎫 General - $0";
                        case FAMILIAR -> "👨‍👩‍👧‍👦 Familiar - 15% descuento";
                        case FAST_PASS -> "⚡ Fast-Pass - Prioridad en filas";
                    });
                }
            }
        });
        selectorTicket.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(TipoTicket tipo, boolean empty) {
                super.updateItem(tipo, empty);
                if (empty || tipo == null) {
                    setText(null);
                } else {
                    setText(switch (tipo) {
                        case GENERAL -> "🎫 General";
                        case FAMILIAR -> "👨‍👩‍👧‍👦 Familiar";
                        case FAST_PASS -> "⚡ Fast-Pass";
                    });
                }
            }
        });
        
        // Distribución de campos
        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(txtNombre, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(txtEmailReg, 1, 1);
        grid.add(new Label("Contraseña:"), 0, 2);
        grid.add(txtPassReg, 1, 2);
        grid.add(new Label("Edad:"), 0, 3);
        grid.add(txtEdad, 1, 3);
        grid.add(new Label("Estatura (m):"), 0, 4);
        grid.add(txtEstatura, 1, 4);
        grid.add(new Label("Saldo inicial ($):"), 0, 5);
        grid.add(txtSaldo, 1, 5);
        grid.add(new Label("Tipo de Ticket:"), 0, 6);
        grid.add(selectorTicket, 1, 6);
        
        dialog.getDialogPane().setContent(grid);
        
        ButtonType btnRegistrar = new ButtonType("Registrarse", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnRegistrar, ButtonType.CANCEL);
        
        // Convertir resultado
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnRegistrar) {
                try {
                    String nombre = txtNombre.getText().trim();
                    String email = txtEmailReg.getText().trim();
                    String pass = txtPassReg.getText();
                    int edad = Integer.parseInt(txtEdad.getText().trim());
                    double estatura = Double.parseDouble(txtEstatura.getText().trim());
                    double saldo = Double.parseDouble(txtSaldo.getText().trim());
                    TipoTicket tipoTicket = selectorTicket.getValue();
                    
                    if (nombre.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                        mostrarError("Todos los campos son obligatorios.");
                        return null;
                    }
                    
                    if (edad < 0 || edad > 120) {
                        mostrarError("Edad inválida (0-120).");
                        return null;
                    }
                    
                    if (estatura < 0.5 || estatura > 2.5) {
                        mostrarError("Estatura inválida (0.5m - 2.5m).");
                        return null;
                    }
                    
                    if (saldo < 0 || saldo > 1000000) {
                        mostrarError("Saldo inválido ($0 - $1,000,000).");
                        return null;
                    }
                    
                    // Verificar email único
                    for (Visitante v : dataService.getVisitantes()) {
                        if (v.getEmail().equalsIgnoreCase(email)) {
                            mostrarError("Este email ya está registrado.");
                            return null;
                        }
                    }
                    
                    // Generar ID único
                    int maxId = 0;
                    for (Visitante v : dataService.getVisitantes()) {
                        String idStr = v.getId().replace("V", "");
                        try {
                            int numId = Integer.parseInt(idStr);
                            if (numId > maxId) maxId = numId;
                        } catch (NumberFormatException ignored) {}
                    }
                    String id = "V" + String.format("%03d", maxId + 1);
                    
                    // Usar el saldo ingresado por el usuario
                    Visitante nuevo = new Visitante(id, nombre, email, pass, edad, estatura, saldo, null);
                    
                    // Asignar ticket según selección
                    Ticket ticket = new Ticket("T_" + id, tipoTicket, 0.0, java.time.LocalDate.now(), nuevo);
                    nuevo.setTicket(ticket);
                    
                    return nuevo;
                    
                } catch (NumberFormatException e) {
                    mostrarError("Edad, estatura y saldo deben ser números válidos.");
                    return null;
                }
            }
            return null;
        });
        
        Optional<Visitante> resultado = dialog.showAndWait();
        resultado.ifPresent(visitante -> {
            // Guardar el nuevo visitante
            dataService.agregarVisitante(visitante);
            dataService.guardarDatos();
            
            // Autocompletar campos de login
            txtEmail.setText(visitante.getEmail());
            txtPassword.setText(visitante.getContrasena());
            
            mostrarError("");
            
            // Mensaje personalizado según tipo de ticket
            String mensaje = switch (visitante.getTicket().getTipo()) {
                case FAST_PASS -> "¡Premium! Disfruta de acceso prioritario en las filas.";
                case FAMILIAR -> "¡En familia! Tienes 15% de descuento en atracciones.";
                case GENERAL -> "¡Bienvenido! Explora el parque.";
            };
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("¡Bienvenido!");
            alert.setHeaderText(null);
            alert.setContentText("Cuenta creada exitosamente.\n" + mensaje);
            alert.showAndWait();
            
            // Login automático
            if (callback != null) {
                callback.onLoginExitoso(RolUsuario.VISITANTE, visitante);
            }
        });
    }
}