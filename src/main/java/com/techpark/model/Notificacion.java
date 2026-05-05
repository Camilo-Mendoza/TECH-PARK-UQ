package com.techpark.model;

import java.time.LocalDateTime;

public class Notificacion {
    private String id;
    private String mensaje;
    private LocalDateTime fechaHora;
    private Visitante destinatario;
    private boolean leida;

    public Notificacion(String id, String mensaje, LocalDateTime fechaHora, Visitante destinatario) {
        this.id = id;
        this.mensaje = mensaje;
        this.fechaHora = fechaHora;
        this.destinatario = destinatario;
        this.leida = false;
    }

    public void marcarLeida() {
        this.leida = true;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public Visitante getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(Visitante destinatario) {
        this.destinatario = destinatario;
    }

    public boolean isLeida() {
        return leida;
    }

    public void setLeida(boolean leida) {
        this.leida = leida;
    }
}
