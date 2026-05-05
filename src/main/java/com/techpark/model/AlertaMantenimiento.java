package com.techpark.model;

import java.time.LocalDateTime;

/**
 * Representa una alerta de mantenimiento asociada a una atracción.
 * Permite identificar su estado de resolución y su nivel de prioridad.
 */
public class AlertaMantenimiento {
    private String id;
    private Atraccion atraccion;
    private LocalDateTime fechaGeneracion;
    private boolean resuelta;

    /**
     * Crea una nueva alerta de mantenimiento no resuelta.
     *
     * @param id identificador de la alerta
     * @param atraccion atracción asociada
     * @param fechaGeneracion fecha y hora de creación
     */
    public AlertaMantenimiento(String id, Atraccion atraccion, LocalDateTime fechaGeneracion) {
        this.id = id;
        this.atraccion = atraccion;
        this.fechaGeneracion = fechaGeneracion;
        this.resuelta = false;
    }

    /**
     * Marca la alerta como resuelta.
     */
    public void resolver() {
        this.resuelta = true;
    }

    /**
     * Calcula la prioridad de atención de la alerta.
     * Prioridad menor representa mayor urgencia.
     *
     * @return 0 si está resuelta o sin atracción,
     *         1 si contador >= 500,
     *         2 si contador >= 450,
     *         3 en otros casos
     */
    public int getPrioridad() {
        if (resuelta || atraccion == null) {
            return 0;
        }

        int contador = atraccion.getContadorVisitantes();
        if (contador >= 500) {
            return 1;
        }
        if (contador >= 450) {
            return 2;
        }
        return 3;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Atraccion getAtraccion() {
        return atraccion;
    }

    public void setAtraccion(Atraccion atraccion) {
        this.atraccion = atraccion;
    }

    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(LocalDateTime fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public boolean isResuelta() {
        return resuelta;
    }

    public void setResuelta(boolean resuelta) {
        this.resuelta = resuelta;
    }
}
