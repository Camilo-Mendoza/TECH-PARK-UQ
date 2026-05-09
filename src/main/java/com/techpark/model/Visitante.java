package com.techpark.model;

import com.techpark.datastructures.*;

public class Visitante extends Usuario {
    private String documento;
    private int edad;
    private double estatura;
    private double saldoVirtual;
    private String fotoPase;
    private Ticket ticket;
    private CustomSet<Atraccion> favoritos;
    private ListaEnlazada<RegistroVisita> historialVisitas;

    public Visitante(String id, String nombre, String email, String contrasena,
                     int edad, double estatura, double saldoVirtual, String fotoPase) {
        super(id, nombre, email, contrasena);
        this.documento = id; // Por defecto usa el ID como documento
        this.edad = edad;
        this.estatura = estatura;
        this.saldoVirtual = saldoVirtual;
        this.fotoPase = fotoPase;
        this.ticket = null;
        this.favoritos = new CustomSet<>();
        this.historialVisitas = new ListaEnlazada<>();
    }

    // Constructor completo con documento
    public Visitante(String id, String nombre, String email, String contrasena,
                     String documento, int edad, double estatura, double saldoVirtual, String fotoPase) {
        super(id, nombre, email, contrasena);
        this.documento = documento;
        this.edad = edad;
        this.estatura = estatura;
        this.saldoVirtual = saldoVirtual;
        this.fotoPase = fotoPase;
        this.ticket = null;
        this.favoritos = new CustomSet<>();
        this.historialVisitas = new ListaEnlazada<>();
    }

    // Favoritos
    public void registrarFavorito(Atraccion atraccion) {
        if (atraccion != null) favoritos.agregar(atraccion);
    }

    public void eliminarFavorito(Atraccion atraccion) {
        if (atraccion != null) favoritos.eliminar(atraccion);
    }

    public CustomSet<Atraccion> consultarFavoritos() {
        return favoritos;
    }

    // Historial
    public ListaEnlazada<RegistroVisita> consultarHistorial() {
        return historialVisitas;
    }

    public void agregarVisita(RegistroVisita visita) {
        if (visita != null) historialVisitas.insertar(visita);
    }

    // Saldo
    public void recargarSaldo(double monto) {
        if (monto > 0) saldoVirtual += monto;
    }

    public double descontarSaldo(double monto) {
        if (monto > 0 && saldoVirtual >= monto) {
            saldoVirtual -= monto;
            return monto;
        }
        return 0;
    }

    /**
     * Verifica si el visitante tiene saldo suficiente para cubrir un costo.
     * Requisito PDF: verificar saldo antes de acceder a atracción con costo adicional.
     */
    public boolean tieneSaldoSuficiente(double costo) {
        return saldoVirtual >= costo;
    }

    // Cola
    /**
     * Intenta unirse a la fila de una atracción.
     * Verifica saldo si la atracción tiene costo adicional.
     */
    public boolean unirseAFila(Atraccion atraccion) {
        if (atraccion == null) return false;
        
        // Verificar saldo para costo adicional
        if (atraccion.getCostoAdicional() > 0 && !tieneSaldoSuficiente(atraccion.getCostoAdicional())) {
            return false;
        }
        
        // Descontar costo adicional si aplica
        if (atraccion.getCostoAdicional() > 0) {
            descontarSaldo(atraccion.getCostoAdicional());
        }
        
        return atraccion.agregarAFila(this);
    }

    public int consultarPosicionEnFila(Atraccion atraccion) {
        if (atraccion != null) return atraccion.obtenerPosicionEnFila(this);
        return -1;
    }

    // Getters y Setters
    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }

    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }

    public double getEstatura() { return estatura; }
    public void setEstatura(double estatura) { this.estatura = estatura; }

    public double getSaldoVirtual() { return saldoVirtual; }
    public void setSaldoVirtual(double saldoVirtual) { this.saldoVirtual = saldoVirtual; }

    public String getFotoPase() { return fotoPase; }
    public void setFotoPase(String fotoPase) { this.fotoPase = fotoPase; }

    public Ticket getTicket() { return ticket; }
    public void setTicket(Ticket ticket) { this.ticket = ticket; }

    @Override
    public void logout() {
        System.out.println("Visitante " + nombre + " ha cerrado sesión.");
    }
}