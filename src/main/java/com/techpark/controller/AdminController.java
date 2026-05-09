package com.techpark.controller;

import com.techpark.model.*;
import com.techpark.service.ParkDataService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.ArrayList;
import java.util.List;

public class AdminController {

    @FXML private Label lblAdminNombre;
    @FXML private Label lblEstadisticas;
    
    @FXML private TextField txtNombreZona;
    @FXML private TextField txtCapacidadZona;
    @FXML private ListView<String> listaZonas;
    
    @FXML private ListView<String> listaAtraccionesAdmin;
    
    @FXML private ComboBox<Operador> selectorOperador;
    @FXML private ComboBox<Zona> selectorZonaAsignar;
    @FXML private ListView<String> listaOperadoresAdmin;
    
    @FXML private ComboBox<TipoAlerta> selectorAlerta;
    @FXML private ListView<String> listaAtraccionesAfectadas;

    private final ParkDataService dataService;
    private Administrador adminActual;

    public AdminController(ParkDataService dataService) {
        this.dataService = dataService;
    }

    public void setAdminInicial(Administrador admin) {
        this.adminActual = admin;
        if (admin != null) {
            lblAdminNombre.setText("Admin: " + admin.getNombre());
            actualizarEstadisticas();
        }
    }

    @FXML
    public void initialize() {
        actualizarListaZonas();
        actualizarListaAtracciones();
        cargarOperadores();
        cargarTiposAlerta();
    }

    private void actualizarEstadisticas() {
        int totalAtracciones = dataService.getAtracciones().size();
        int totalZonas = dataService.getZonas().size();
        int totalVisitantes = dataService.getVisitantes().size();
        long activas = dataService.getAtracciones().stream()
            .filter(a -> a.getEstado() == EstadoAtraccion.ACTIVA).count();
        
        lblEstadisticas.setText(String.format(
            "%d zonas | %d atracciones (%d activas) | %d visitantes",
            totalZonas, totalAtracciones, activas, totalVisitantes));
    }

    @FXML
    public void crearZona() {
        String nombre = txtNombreZona.getText().trim();
        String capacidadStr = txtCapacidadZona.getText().trim();
        
        if (nombre.isEmpty() || capacidadStr.isEmpty()) {
            mostrarAlerta("Completa todos los campos.");
            return;
        }
        
        try {
            int capacidad = Integer.parseInt(capacidadStr);
            if (capacidad <= 0) {
                mostrarAlerta("La capacidad debe ser mayor a 0.");
                return;
            }
            
            Zona nuevaZona = adminActual.crearZona(nombre);
            nuevaZona.setCapacidadMaxima(capacidad);
            
            dataService.getZonas().add(nuevaZona);
            dataService.guardarDatos();
            
            txtNombreZona.clear();
            txtCapacidadZona.clear();
            actualizarListaZonas();
            actualizarEstadisticas();
            
            mostrarAlerta("Zona creada: " + nombre);
            
        } catch (NumberFormatException e) {
            mostrarAlerta("Capacidad debe ser un numero valido.");
        }
    }

    private void actualizarListaZonas() {
        listaZonas.getItems().clear();
        for (Zona z : dataService.getZonas()) {
            int numAtracciones = z.getAtracciones().tamano();
            int numOperadores = z.getOperadores().tamano();
            listaZonas.getItems().add(String.format(
                "%s | Capacidad: %d | Atracciones: %d | Operadores: %d",
                z.getNombre(), z.getCapacidadMaxima(), numAtracciones, numOperadores));
        }
    }

    private void actualizarListaAtracciones() {
        listaAtraccionesAdmin.getItems().clear();
        for (Atraccion a : dataService.getAtracciones()) {
            String estadoStr = switch (a.getEstado()) {
                case ACTIVA -> "ACTIVA";
                case EN_MANTENIMIENTO -> "MANTENIMIENTO";
                case CERRADA -> "CERRADA";
            };
            String zonaNombre = a.getZona() != null ? a.getZona().getNombre() : "Sin zona";
            listaAtraccionesAdmin.getItems().add(String.format(
                "[%s] %s | %s | %s | Visitantes: %d | Espera: %d min",
                estadoStr, a.getNombre(), a.getTipo(), zonaNombre,
                a.getContadorVisitantes(), a.getTiempoEsperaEstimado()));
        }
    }

    private void cargarOperadores() {
        selectorOperador.getItems().addAll(dataService.getOperadores());
        selectorOperador.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Operador o, boolean empty) {
                super.updateItem(o, empty);
                setText(empty || o == null ? null : o.getNombre());
            }
        });
        selectorOperador.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Operador o, boolean empty) {
                super.updateItem(o, empty);
                setText(empty || o == null ? null : o.getNombre());
            }
        });
        
        selectorZonaAsignar.getItems().addAll(dataService.getZonas());
        selectorZonaAsignar.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Zona z, boolean empty) {
                super.updateItem(z, empty);
                setText(empty || z == null ? null : z.getNombre());
            }
        });
        selectorZonaAsignar.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Zona z, boolean empty) {
                super.updateItem(z, empty);
                setText(empty || z == null ? null : z.getNombre());
            }
        });
        
        actualizarListaOperadores();
    }

    @FXML
    public void asignarOperador() {
        Operador operador = selectorOperador.getValue();
        Zona zona = selectorZonaAsignar.getValue();
        
        if (operador == null || zona == null) {
            mostrarAlerta("Selecciona un operador y una zona.");
            return;
        }
        
        adminActual.asignarOperadorAZona(operador, zona);
        dataService.guardarDatos();
        actualizarListaOperadores();
        actualizarListaZonas();
        
        mostrarAlerta(operador.getNombre() + " asignado a " + zona.getNombre());
    }

    private void actualizarListaOperadores() {
        listaOperadoresAdmin.getItems().clear();
        for (Operador o : dataService.getOperadores()) {
            String zonaNombre = o.getZona() != null ? o.getZona().getNombre() : "Sin zona";
            listaOperadoresAdmin.getItems().add(o.getNombre() + " -> " + zonaNombre);
        }
    }

    private void cargarTiposAlerta() {
        selectorAlerta.getItems().addAll(TipoAlerta.values());
        actualizarListaAlertas();
    }

    @FXML
    public void activarAlerta() {
        TipoAlerta tipo = selectorAlerta.getValue();
        if (tipo == null) {
            mostrarAlerta("Selecciona un tipo de alerta.");
            return;
        }
        
        List<Atraccion> afectadas = new ArrayList<>();
        for (Atraccion a : dataService.getAtracciones()) {
            boolean debeCerrar = switch (tipo) {
                case LLUVIA_FUERTE -> a.getTipo() == TipoAtraccion.ACUATICA;
                case TORMENTA_ELECTRICA -> a.getTipo() == TipoAtraccion.ACUATICA || 
                                           a.getTipo() == TipoAtraccion.MECANICA_ALTURA;
                case OTRO -> false;
            };
            
            if (debeCerrar && a.getEstado() == EstadoAtraccion.ACTIVA) {
                afectadas.add(a);
            }
        }
        
        if (afectadas.isEmpty()) {
            mostrarAlerta("No hay atracciones activas que cerrar.");
            return;
        }
        
        adminActual.activarAlertaClimatica(tipo, afectadas);
        dataService.agregarAlerta(new AlertaClimatica(
            "ALERTA_" + System.currentTimeMillis(), tipo, java.time.LocalDateTime.now(), afectadas));
        dataService.guardarDatos();
        
        actualizarListaAtracciones();
        actualizarListaAlertas();
        actualizarEstadisticas();
        
        mostrarAlerta("Alerta activada. " + afectadas.size() + " atracciones cerradas.");
    }

    @FXML
    public void desactivarAlerta() {
        if (dataService.getAlertasActivas().isEmpty()) {
            mostrarAlerta("No hay alertas activas.");
            return;
        }
        
        ChoiceDialog<AlertaClimatica> dialog = new ChoiceDialog<>(
            dataService.getAlertasActivas().get(0),
            dataService.getAlertasActivas()
        );
        dialog.setTitle("Desactivar Alerta");
        dialog.setHeaderText("Selecciona la alerta:");
        dialog.setContentText("Alerta:");
        
        dialog.showAndWait().ifPresent(alerta -> {
            dataService.desactivarAlerta(alerta);
            dataService.guardarDatos();
            
            actualizarListaAtracciones();
            actualizarListaAlertas();
            actualizarEstadisticas();
            
            mostrarAlerta("Alerta desactivada.");
        });
    }

    private void actualizarListaAlertas() {
        listaAtraccionesAfectadas.getItems().clear();
        
        if (dataService.getAlertasActivas().isEmpty()) {
            listaAtraccionesAfectadas.getItems().add("No hay alertas activas.");
        } else {
            for (AlertaClimatica alerta : dataService.getAlertasActivas()) {
                String estado = alerta.isActiva() ? "ACTIVA" : "INACTIVA";
                listaAtraccionesAfectadas.getItems().add(estado + " " + alerta.getTipo());
                for (Atraccion a : alerta.getAtraccionesAfectadas()) {
                    listaAtraccionesAfectadas.getItems().add("   - " + a.getNombre());
                }
            }
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Tech-Park UQ - Administrador");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}