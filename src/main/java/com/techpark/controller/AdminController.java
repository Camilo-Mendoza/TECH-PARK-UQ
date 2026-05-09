package com.techpark.controller;

import com.techpark.model.*;
import com.techpark.service.ParkDataService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AdminController {

    @FXML private Label lblAdminNombre;
    @FXML private Label lblEstadisticas;
    
    // Pestaña Zonas
    @FXML private TextField txtNombreZona;
    @FXML private TextField txtCapacidadZona;
    @FXML private ListView<String> listaZonas;
    
    // Pestaña Atracciones
    @FXML private ListView<String> listaAtraccionesAdmin;
    
    // Pestaña Operadores
    @FXML private ComboBox<Operador> selectorOperador;
    @FXML private ComboBox<Zona> selectorZonaAsignar;
    @FXML private ListView<String> listaOperadoresAdmin;
    
    // Pestaña Alertas
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
            lblAdminNombre.setText("⚙ " + admin.getNombre());
            actualizarEstadisticas();
        }
    }

    @FXML
    public void initialize() {
        // Cargar listas
        actualizarListaZonas();
        actualizarListaAtracciones();
        cargarOperadores();
        cargarTiposAlerta();
    }

    // ============ ESTADÍSTICAS ============
    private void actualizarEstadisticas() {
        int totalAtracciones = dataService.getAtracciones().size();
        int totalZonas = dataService.getZonas().size();
        int totalVisitantes = dataService.getVisitantes().size();
        long activas = dataService.getAtracciones().stream()
            .filter(a -> a.getEstado() == EstadoAtraccion.ACTIVA).count();
        
        lblEstadisticas.setText(String.format(
            "🏗 %d zonas | 🎢 %d atracciones (%d activas) | 👤 %d visitantes registrados",
            totalZonas, totalAtracciones, activas, totalVisitantes));
    }

    // ============ GESTIÓN DE ZONAS ============
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
            
            // Guardar en el servicio
            dataService.getZonas().add(nuevaZona);
            dataService.guardarDatos();
            
            txtNombreZona.clear();
            txtCapacidadZona.clear();
            actualizarListaZonas();
            actualizarEstadisticas();
            
            mostrarAlerta("✅ Zona creada: " + nombre);
            
        } catch (NumberFormatException e) {
            mostrarAlerta("Capacidad debe ser un número válido.");
        }
    }

    private void actualizarListaZonas() {
        listaZonas.getItems().clear();
        for (Zona z : dataService.getZonas()) {
            int numAtracciones = z.getAtracciones().tamano();
            int numOperadores = z.getOperadores().tamano();
            listaZonas.getItems().add(String.format(
                "🏗 %s | Capacidad: %d | Atracciones: %d | Operadores: %d",
                z.getNombre(), z.getCapacidadMaxima(), numAtracciones, numOperadores));
        }
    }

    // ============ GESTIÓN DE ATRACCIONES ============
    private void actualizarListaAtracciones() {
        listaAtraccionesAdmin.getItems().clear();
        for (Atraccion a : dataService.getAtracciones()) {
            String estadoEmoji = switch (a.getEstado()) {
                case ACTIVA -> "🟢";
                case EN_MANTENIMIENTO -> "🟠";
                case CERRADA -> "🔴";
            };
            String zonaNombre = a.getZona() != null ? a.getZona().getNombre() : "Sin zona";
            listaAtraccionesAdmin.getItems().add(String.format(
                "%s %s | %s | %s | Visitantes: %d | Espera: %d min",
                estadoEmoji, a.getNombre(), a.getTipo(), zonaNombre,
                a.getContadorVisitantes(), a.getTiempoEsperaEstimado()));
        }
    }

    // ============ GESTIÓN DE OPERADORES ============
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
        
        mostrarAlerta("✅ " + operador.getNombre() + " asignado a " + zona.getNombre());
    }

    private void actualizarListaOperadores() {
        listaOperadoresAdmin.getItems().clear();
        for (Operador o : dataService.getOperadores()) {
            String zonaNombre = o.getZona() != null ? o.getZona().getNombre() : "Sin zona";
            listaOperadoresAdmin.getItems().add("🔧 " + o.getNombre() + " → " + zonaNombre);
        }
    }

    // ============ ALERTAS CLIMÁTICAS ============
    private void cargarTiposAlerta() {
        selectorAlerta.getItems().addAll(TipoAlerta.values());
    }

    @FXML
    public void activarAlerta() {
        TipoAlerta tipo = selectorAlerta.getValue();
        if (tipo == null) {
            mostrarAlerta("Selecciona un tipo de alerta.");
            return;
        }
        
        // Determinar qué atracciones se ven afectadas
        List<Atraccion> afectadas = new ArrayList<>();
        for (Atraccion a : dataService.getAtracciones()) {
            boolean debeCerrar = false;
            
            switch (tipo) {
                case LLUVIA_FUERTE -> {
                    if (a.getTipo() == TipoAtraccion.ACUATICA) debeCerrar = true;
                }
                case TORMENTA_ELECTRICA -> {
                    if (a.getTipo() == TipoAtraccion.ACUATICA || 
                        a.getTipo() == TipoAtraccion.MECANICA_ALTURA) debeCerrar = true;
                }
                case OTRO -> debeCerrar = false;
            }
            
            if (debeCerrar && a.getEstado() == EstadoAtraccion.ACTIVA) {
                a.cambiarEstado(EstadoAtraccion.CERRADA, MotivosCierre.CLIMA);
                afectadas.add(a);
            }
        }
        
        // Mostrar resultado
        listaAtraccionesAfectadas.getItems().clear();
        if (afectadas.isEmpty()) {
            listaAtraccionesAfectadas.getItems().add("✅ Ninguna atracción activa fue afectada.");
        } else {
            for (Atraccion a : afectadas) {
                listaAtraccionesAfectadas.getItems().add("🔴 " + a.getNombre() + " → CERRADA por " + tipo);
            }
        }
        
        adminActual.activarAlertaClimatica(tipo);
        dataService.guardarDatos();
        actualizarListaAtracciones();
        actualizarEstadisticas();
        
        mostrarAlerta("⚠ Alerta '" + tipo + "' activada.\n" + afectadas.size() + 
                     " atracciones cerradas automáticamente.");
    }

    @FXML
    public void desactivarAlerta() {
        // Reactivar todas las atracciones que están cerradas por clima
        int reactivadas = 0;
        for (Atraccion a : dataService.getAtracciones()) {
            if (a.getEstado() == EstadoAtraccion.CERRADA && 
                a.getMotivoCierre() == MotivosCierre.CLIMA) {
                a.cambiarEstado(EstadoAtraccion.ACTIVA, null);
                reactivadas++;
            }
        }
        
        dataService.guardarDatos();
        actualizarListaAtracciones();
        actualizarEstadisticas();
        
        listaAtraccionesAfectadas.getItems().clear();
        listaAtraccionesAfectadas.getItems().add("Alerta desactivada. " + reactivadas + " atracciones reactivadas.");
        
        mostrarAlerta("lerta climática desactivada.\n" + reactivadas + " atracciones han sido reactivadas.");
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Tech-Park UQ - Administrador");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}