package com.techpark.datastructures;

import java.util.List;

/**
 * Estructura de datos Set propia para gestionar colecciones sin duplicados.
 * Usada para las atracciones favoritas de cada visitante.
 *
 * @param <T> tipo de elemento almacenado
 */
public class CustomSet<T> {
    private final ListaEnlazada<T> elementos;

    public CustomSet() {
        this.elementos = new ListaEnlazada<>();
    }

    /**
     * Agrega un elemento si no existe ya en el set.
     * @return true si fue agregado, false si ya existía
     */
    public boolean agregar(T elemento) {
        if (elemento == null || contiene(elemento)) return false;
        elementos.insertar(elemento);
        return true;
    }

    /**
     * Elimina un elemento del set.
     * @return true si fue eliminado, false si no existía
     */
    public boolean eliminar(T elemento) {
        return elementos.eliminar(elemento);
    }

    /**
     * Verifica si el elemento existe en el set.
     */
    public boolean contiene(T elemento) {
        return elementos.contiene(elemento);
    }

    public int tamano() {
        return elementos.tamano();
    }

    public boolean estaVacio() {
        return elementos.estaVacia();
    }

    public void limpiar() {
        while (!elementos.estaVacia()) {
            elementos.eliminarPrimero();
        }
    }

    public List<T> aLista() {
        return elementos.aLista();
    }

    /**
     * Unión: elementos de este set más los del otro sin repetir.
     */
    public CustomSet<T> union(CustomSet<T> otro) {
        CustomSet<T> resultado = new CustomSet<>();
        for (T e : this.aLista()) resultado.agregar(e);
        for (T e : otro.aLista()) resultado.agregar(e);
        return resultado;
    }

    /**
     * Intersección: solo elementos que existen en ambos sets.
     */
    public CustomSet<T> interseccion(CustomSet<T> otro) {
        CustomSet<T> resultado = new CustomSet<>();
        for (T e : this.aLista()) {
            if (otro.contiene(e)) resultado.agregar(e);
        }
        return resultado;
    }

    @Override
    public String toString() {
        return elementos.aLista().toString();
    }
}