package com.techpark.datastructures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Nodo {
    private String id;
    private String nombre;
    private List<Nodo> vecinos;

    public Nodo(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        this.vecinos = new ArrayList<>();
    }

    public void agregarVecino(Nodo vecino) {
        if (vecino == null || vecino.equals(this)) {
            return;
        }
        if (!vecinos.contains(vecino)) {
            vecinos.add(vecino);
        }
    }

    public void eliminarVecino(Nodo vecino) {
        if (vecino == null) {
            return;
        }
        vecinos.remove(vecino);
    }

    public List<Nodo> getVecinos() {
        return Collections.unmodifiableList(vecinos);
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Nodo)) return false;
        Nodo nodo = (Nodo) o;
        return Objects.equals(id, nodo.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Nodo{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}

