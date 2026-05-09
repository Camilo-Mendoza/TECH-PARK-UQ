package com.techpark.controller;

import com.techpark.model.*;
import com.techpark.service.ParkDataService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OperatorController {

    @FXML private Label lblOperadorNombre;
    @FXML private Label lblOperadorZona;
    @FXML private ComboBox<Atraccion> selectorAtraccion;
    @FXML private Label lblEstadoActual;
    @FXML private Label lblContadorVisitantes;
    @FXML private Label lblColaVirtual;
    @FXML private ListView<String> listaActividades;

    private final ParkDataService dataService;
    private Operador operadorActual;
    private Atraccion atraccionSeleccionada;

    public OperatorController(ParkDataService dataService) {
        this.dataService = dataService;
    }

    public void setOperadorInicial(Operador operador) {
        this.operadorActual = operador;
        if (operador != null) {
            lblOperadorNombre.setText("Operador: " + operador.getNombre());
            lblOperadorZona.setText("Zona: " + 
                (operador.getZona() != null ? operador.getZona().getNombre() : "Sin asignar"));
            // Cargar atracciones DESPUES de tener el operador
            cargarAtraccionesDeZona();
        }
    }

    @FXML
    public void initialize() {
        // Configurar cómo se muestran las atracciones
        selectorAtraccion.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Atraccion a, boolean empty) {
                super.updateItem(a, empty);
                if (empty || a == null) {
                    setText(null);
                } else {
                    String estadoStr = switch (a.getEstado()) {
                        case ACTIVA -> "ACTIVA";
                        case EN_MANTENIMIENTO -> "MANTENIMIENTO";
                        case CERRADA -> "CERRADA";
                    };
                    setText("[" + estadoStr + "] " + a.getNombre());
                }
            }
        });
        selectorAtraccion.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Atraccion a, boolean empty) {
                super.updateItem(a, empty);
                setText(empty || a == null ? null : a.getNombre());
            }
        });

        selectorAtraccion.setOnAction(e -> actualizarInfoAtraccion());
    }

    private void cargarAtraccionesDeZona() {
        selectorAtraccion.getItems().clear();
        if (operadorActual != null && operadorActual.getZona() != null) {
            Zona zona = operadorActual.getZona();
            // Recargar atracciones desde el servicio para asegurar datos frescos
            for (Atraccion a : dataService.getAtracciones()) {
                if (a.getZona() != null && a.getZona().getId().equals(zona.getId())) {
                    selectorAtraccion.getItems().add(a);
                }
            }
        }
        // Si no hay atracciones en la zona, mostrar mensaje
        if (selectorAtraccion.getItems().isEmpty()) {
            registrarActividad("No hay atracciones asignadas a tu zona.");
        }
    }

    private void actualizarInfoAtraccion() {
        atraccionSeleccionada = selectorAtraccion.getValue();
        if (atraccionSeleccionada == null) {
            lblEstadoActual.setText("Estado actual: --");
            lblContadorVisitantes.setText("Visitantes acumulados: --");
            lblColaVirtual.setText("Personas en cola: --");
            return;
        }

        lblEstadoActual.setText("Estado actual: " + atraccionSeleccionada.getEstado());
        lblContadorVisitantes.setText("Visitantes acumulados: " + 
            atraccionSeleccionada.getContadorVisitantes() + " / 500");
        
        int enCola = atraccionSeleccionada.getCola() != null ? 
            atraccionSeleccionada.getCola().tamano() : 0;
        lblColaVirtual.setText("Personas en cola: " + enCola);
    }

    @FXML
    public void cambiarEstado() {
        if (atraccionSeleccionada == null) {
            mostrarAlerta("Selecciona una atraccion primero.");
            return;
        }

        ChoiceDialog<EstadoAtraccion> dialog = new ChoiceDialog<>(
            atraccionSeleccionada.getEstado(),
            EstadoAtraccion.values()
        );
        dialog.setTitle("Cambiar Estado");
        dialog.setHeaderText("Cambiar estado de: " + atraccionSeleccionada.getNombre());
        dialog.setContentText("Nuevo estado:");

        dialog.showAndWait().ifPresent(nuevoEstado -> {
            MotivosCierre motivo = null;
            if (nuevoEstado == EstadoAtraccion.CERRADA) {
                ChoiceDialog<MotivosCierre> motivoDialog = new ChoiceDialog<>(
                    MotivosCierre.OTRO, MotivosCierre.values()
                );
                motivoDialog.setTitle("Motivo de Cierre");
                motivoDialog.setHeaderText("Por que se cierra " + atraccionSeleccionada.getNombre() + "?");
                motivoDialog.setContentText("Motivo:");
                motivo = motivoDialog.showAndWait().orElse(MotivosCierre.OTRO);
            }

            operadorActual.gestionarEstadoAtraccion(atraccionSeleccionada, nuevoEstado, motivo);
            registrarActividad("Cambio estado de " + atraccionSeleccionada.getNombre() + 
                             " a " + nuevoEstado);
            actualizarInfoAtraccion();
        });
    }

    @FXML
    public void registrarRevision() {
        if (atraccionSeleccionada == null) {
            mostrarAlerta("Selecciona una atraccion primero.");
            return;
        }

        operadorActual.registrarRevisionTecnica(atraccionSeleccionada);
        registrarActividad("Registro revision tecnica de " + atraccionSeleccionada.getNombre() + 
                         " -> Contador reiniciado a 0");
        actualizarInfoAtraccion();
        
        mostrarAlerta("Revision tecnica registrada.\n" +
                     "Atraccion reactivada y contador reiniciado.");
    }

    @FXML
    public void procesarSiguiente() {
        if (atraccionSeleccionada == null) {
            mostrarAlerta("Selecciona una atraccion primero.");
            return;
        }

        Visitante siguiente = operadorActual.procesarSiguienteEnFila(atraccionSeleccionada);
        if (siguiente != null) {
            registrarActividad("Proceso a " + siguiente.getNombre() + 
                             " en " + atraccionSeleccionada.getNombre());
            actualizarInfoAtraccion();
            mostrarAlerta("Visitante procesado: " + siguiente.getNombre());
        } else {
            mostrarAlerta("No hay visitantes en la cola o la atraccion no esta disponible.");
        }
    }

    private void registrarActividad(String actividad) {
        String hora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        listaActividades.getItems().add(0, "[" + hora + "] " + actividad);
        
        if (listaActividades.getItems().size() > 50) {
            listaActividades.getItems().remove(50, listaActividades.getItems().size());
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Tech-Park UQ - Operador");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}