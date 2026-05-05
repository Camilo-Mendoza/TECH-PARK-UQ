package com.techpark.model;

import com.techpark.datastructures.*;
import java.util.List;

public class Visitante extends Usuario {
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

    // Cola
    public boolean unirseAFila(Atraccion atraccion) {
        if (atraccion != null && saldoVirtual >= 0) {
            return atraccion.agregarAFila(this);
        }
        return false;
    }

    public int consultarPosicionEnFila(Atraccion atraccion) {
        if (atraccion != null) return atraccion.obtenerPosicionEnFila(this);
        return -1;
    }

    // Getters y Setters
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