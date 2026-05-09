package com.techpark.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AlertaClimatica {
    private String id;
    private TipoAlerta tipo;
    private LocalDateTime fechaHora;
    private boolean activa;
    private List<Atraccion> atraccionesAfectadas;

    public AlertaClimatica(String id, TipoAlerta tipo, LocalDateTime fechaHora, List<Atraccion> atraccionesAfectadas) {
        this.id = id;
        this.tipo = tipo;
        this.fechaHora = fechaHora;
        this.activa = false;
        this.atraccionesAfectadas = atraccionesAfectadas != null ? atraccionesAfectadas : new ArrayList<>();
    }

    /**
     * Activa la alerta y cierra las atracciones afectadas.
     */
    public void activar() {
        this.activa = true;
        for (Atraccion a : atraccionesAfectadas) {
            if (a.getEstado() == EstadoAtraccion.ACTIVA) {
                a.cambiarEstado(EstadoAtraccion.CERRADA, MotivosCierre.CLIMA);
            }
        }
    }

    /**
     * Desactiva la alerta y reactiva las atracciones que estaban cerradas por clima.
     */
    public void desactivar() {
        this.activa = false;
        for (Atraccion a : atraccionesAfectadas) {
            if (a.getEstado() == EstadoAtraccion.CERRADA && 
                a.getMotivoCierre() == MotivosCierre.CLIMA) {
                a.cambiarEstado(EstadoAtraccion.ACTIVA, null);
            }
        }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public TipoAlerta getTipo() { return tipo; }
    public void setTipo(TipoAlerta tipo) { this.tipo = tipo; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }

    public List<Atraccion> getAtraccionesAfectadas() { return atraccionesAfectadas; }
    public void setAtraccionesAfectadas(List<Atraccion> atraccionesAfectadas) {
        this.atraccionesAfectadas = atraccionesAfectadas != null ? atraccionesAfectadas : new ArrayList<>();
    }

    /**
     * Obtiene los visitantes que deben ser notificados.
     * Incluye visitantes en cola de las atracciones afectadas.
     * Requisito PDF: notificar a visitantes con tickets activos o reservas en cola.
     */
    public List<Visitante> obtenerVisitantesParaNotificar() {
        List<Visitante> visitantes = new ArrayList<>();
        for (Atraccion a : atraccionesAfectadas) {
            if (a.getCola() != null && !a.getCola().estaVacia()) {
                for (Visitante v : a.getCola().aLista()) {
                    if (!visitantes.contains(v)) {
                        visitantes.add(v);
                    }
                }
            }
        }
        return visitantes;
    }

    /**
     * Genera mensajes de notificación para cada visitante afectado.
     * Requisito PDF: notificar cambios en el estado de las atracciones.
     */
    public List<Notificacion> generarNotificaciones() {
        List<Notificacion> notificaciones = new ArrayList<>();
        List<Visitante> afectados = obtenerVisitantesParaNotificar();
        
        for (Visitante v : afectados) {
            String mensaje = "Alerta " + tipo + ": Las siguientes atracciones han sido cerradas: ";
            for (Atraccion a : atraccionesAfectadas) {
                mensaje += a.getNombre() + ", ";
            }
            mensaje = mensaje.substring(0, mensaje.length() - 2);
            
            Notificacion n = new Notificacion(
                "NOT_" + System.currentTimeMillis() + "_" + v.getId(),
                mensaje,
                LocalDateTime.now(),
                v
            );
            notificaciones.add(n);
        }
        return notificaciones;
    }

    @Override
    public String toString() {
        return (activa ? "🔴 " : "🟢 ") + tipo + " - " + 
               atraccionesAfectadas.size() + " atracciones afectadas";
    }
}