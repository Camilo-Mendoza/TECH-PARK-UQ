package com.techpark.model;

import com.techpark.datastructures.*;

public class Zona {
    private String id;
    private String nombre;
    private int capacidadMaxima;
    private int visitantesActuales;
    private ListaEnlazada<Atraccion> atracciones;
    private ListaEnlazada<Operador> operadores;
    // Constructor
    public Zona(String id, String nombre, int capacidadMaxima) {
        this.id = id;
        this.nombre = nombre;
        this.capacidadMaxima = capacidadMaxima;
        this.visitantesActuales = 0;
        this.atracciones = new ListaEnlazada<>();
        this.operadores = new ListaEnlazada<>();
    }
   // Constructor adicional para crear una zona con un ID generado automáticamente y una capacidad predeterminada
    public Zona(String nombre) {
        this(generarId(), nombre, 100);
    }

    private static String generarId() {
        return "ZONA_" + System.currentTimeMillis();
    }
// Métodos de gestión de atracciones y operadores
    public void agregarAtraccion(Atraccion atraccion) {
        if (atraccion == null) {
            throw new IllegalArgumentException("La atracción no puede ser nula.");
        }
        atracciones.insertar(atraccion);
    }

    public void removerAtraccion(Atraccion atraccion) {
        if (atraccion == null) {
            throw new IllegalArgumentException("La atracción no puede ser nula.");
        }
        atracciones.eliminar(atraccion);
    }
   public void asignarOperador(Operador operador) {
        if (operador == null) {
            throw new IllegalArgumentException("El operador no puede ser nulo.");
        }
        operadores.insertar(operador);
    }

    public boolean estaLlena() {
        return visitantesActuales >= capacidadMaxima;
    }

    public ListaEnlazada<Atraccion> getAtracciones() {
        return atracciones;
    }
     // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public void setCapacidadMaxima(int capacidadMaxima) {
        this.capacidadMaxima = capacidadMaxima;
    }

    public int getVisitantesActuales() {
        return visitantesActuales;
    }

    public void setVisitantesActuales(int visitantesActuales) {
        this.visitantesActuales = visitantesActuales;
    }

    public ListaEnlazada<Operador> getOperadores() {
        return operadores;
    }

    public void setOperadores(ListaEnlazada<Operador> operadores) {
        this.operadores = operadores;
    } 
}


   