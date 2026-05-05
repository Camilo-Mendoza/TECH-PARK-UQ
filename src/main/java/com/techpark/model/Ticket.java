package com.techpark.model;

import java.time.LocalDate;

/**
 * Representa un ticket de ingreso al parque para un visitante.
 * Incluye información de tipo, precio, fecha de compra y estado de validez.
 */
public class Ticket {
    private String id;
    private TipoTicket tipo;
    private double precio;
    private LocalDate fechaCompra;
    private boolean activo;
    private Visitante visitante;

    /**
     * Crea un ticket y lo deja activo por defecto.
     *
     * @param id identificador del ticket
     * @param tipo tipo de ticket
     * @param precio precio del ticket
     * @param fechaCompra fecha de compra
     * @param visitante visitante asociado al ticket
     */
    public Ticket(String id, TipoTicket tipo, double precio, LocalDate fechaCompra, Visitante visitante) {
        this.id = id;
        this.tipo = tipo;
        this.precio = precio;
        this.fechaCompra = fechaCompra;
        this.activo = true;
        this.visitante = visitante;
    }

    /**
     * Desactiva el ticket para impedir su uso posterior.
     */
    public void desactivar() {
        this.activo = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TipoTicket getTipo() {
        return tipo;
    }

    public void setTipo(TipoTicket tipo) {
        this.tipo = tipo;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public LocalDate getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(LocalDate fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Visitante getVisitante() {
        return visitante;
    }

    public void setVisitante(Visitante visitante) {
        this.visitante = visitante;
    }

    /**
     * Calcula el valor del descuento aplicable según el tipo de ticket.
     *
     * @return valor del descuento sobre el precio actual
     */
    public double calcularDescuento() {
        if (tipo == null) {
            return 0.0;
        }

        switch (tipo) {
            case FAMILIAR:
                return precio * 0.15;
            case FAST_PASS:
                return 0.0;
            case GENERAL:
            default:
                return 0.0;
        }
    }

    /**
     * Verifica si el ticket puede considerarse válido para operar en el sistema.
     *
     * @return true si está activo y tiene visitante y fecha de compra
     */
    public boolean esValido() {
        return activo && visitante != null && fechaCompra != null;
    }
}
