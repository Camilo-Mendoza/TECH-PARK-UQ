package com.techpark.model;

public class Operador extends Usuario {
    private Zona zona;

    public Operador(String id, String nombre, String email, String contrasena, Zona zona) {
        super(id, nombre, email, contrasena);
        this.zona = zona;
    }

    // Getters y Setters
    public Zona getZona() {
         return zona; 
        }
    public void setZona(Zona zona) {
         this.zona = zona; 
        }

    /**
     * Cambia el estado de una atracción con su motivo de cierre.
     * Requisito PDF: Control de Acceso y Gestión de Estado.
     */
    public void gestionarEstadoAtraccion(Atraccion atraccion, EstadoAtraccion estado, MotivosCierre motivo) {
        if (atraccion == null || estado == null) {
            throw new IllegalArgumentException("Atraccion y estado no pueden ser nulos.");
        }
        atraccion.cambiarEstado(estado, motivo);
    }

    /**
     * Registra una revisión técnica satisfactoria.
     * Reinicia el contador y reactiva la atracción.
     * Requisito PDF: Mantenimiento preventivo al alcanzar 500 visitantes.
     */
    public void registrarRevisionTecnica(Atraccion atraccion) {
        if (atraccion == null) {
            throw new IllegalArgumentException("Atraccion no puede ser nula.");
        }
        atraccion.registrarRevisionTecnica();
    }

    /**
     * Procesa al siguiente visitante en la cola de la atracción.
     * Requisito PDF: Atención Prioritaria (Fast-Pass primero).
     */
    public Visitante procesarSiguienteEnFila(Atraccion atraccion) {
        if (atraccion == null) {
            throw new IllegalArgumentException("Atraccion no puede ser nula.");
        }
        return atraccion.procesarSiguienteEnFila();
    }

    /**
     * Valida si un visitante cumple requisitos de acceso.
     * Requisito PDF: Control de Acceso - altura, edad.
     */
    public boolean validarAcceso(Visitante visitante, Atraccion atraccion) {
        if (visitante == null || atraccion == null) {
            throw new IllegalArgumentException("Visitante y atracción no pueden ser nulos.");
        }
        return atraccion.validarAcceso(visitante);
    }

    /**
     * Verifica si hay capacidad disponible en el ciclo actual.
     * Requisito PDF: Control de Acceso - capacidad por ciclo.
     */
    public boolean hayCapacidadDisponible(Atraccion atraccion) {
        if (atraccion == null) return false;
        int enCola = atraccion.getCola() != null ? atraccion.getCola().tamano() : 0;
        return enCola < atraccion.getCapacidadMaxPorCiclo();
    }

    @Override
    public void logout() {
        System.out.println("Operador " + nombre + " ha cerrado sesión.");
    }
}