package com.techpark.datastructures;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementación genérica de lista enlazada simple.
 *
 * @param <T> tipo de elemento almacenado
 */
public class ListaEnlazada<T> {
    private NodoLista<T> cabeza;
    private int tamano;

    /**
     * Inserta un elemento al final de la lista.
     *
     * @param valor elemento a insertar
     */
    public void insertar(T valor) {
        if (valor == null) {
            return;
        }

        NodoLista<T> nuevo = new NodoLista<>(valor);
        if (cabeza == null) {
            cabeza = nuevo;
            tamano++;
            return;
        }

        NodoLista<T> actual = cabeza;
        while (actual.siguiente != null) {
            actual = actual.siguiente;
        }
        actual.siguiente = nuevo;
        tamano++;
    }

    /**
     * Elimina la primera ocurrencia del elemento indicado.
     *
     * @param valor elemento a eliminar
     * @return true si se eliminó correctamente, false si no se encontró
     */
    public boolean eliminar(T valor) {
        if (cabeza == null || valor == null) {
            return false;
        }

        if (cabeza.valor.equals(valor)) {
            cabeza = cabeza.siguiente;
            tamano--;
            return true;
        }

        NodoLista<T> actual = cabeza;
        while (actual.siguiente != null) {
            if (actual.siguiente.valor.equals(valor)) {
                actual.siguiente = actual.siguiente.siguiente;
                tamano--;
                return true;
            }
            actual = actual.siguiente;
        }

        return false;
    }

    /**
     * Verifica si el elemento existe en la lista.
     *
     * @param valor elemento a buscar
     * @return true si existe, false en caso contrario
     */
    public boolean contiene(T valor) {
        NodoLista<T> actual = cabeza;
        while (actual != null) {
            if (actual.valor.equals(valor)) {
                return true;
            }
            actual = actual.siguiente;
        }
        return false;
    }

    /**
     * Obtiene la cantidad de elementos almacenados.
     *
     * @return tamaño de la lista
     */
    public int tamano() {
        return tamano;
    }

    /**
     * Indica si la lista no contiene elementos.
     *
     * @return true si está vacía, false si tiene elementos
     */
    public boolean estaVacia() {
        return tamano == 0;
    }

    /**
     * Convierte el contenido de la lista enlazada a una lista estándar de Java.
     *
     * @return lista con los elementos en el mismo orden de inserción
     */
    public List<T> aLista() {
        List<T> resultado = new ArrayList<>();
        NodoLista<T> actual = cabeza;
        while (actual != null) {
            resultado.add(actual.valor);
            actual = actual.siguiente;
        }
        return resultado;
    }

    private static class NodoLista<T> {
        private final T valor;
        private NodoLista<T> siguiente;

        private NodoLista(T valor) {
            this.valor = valor;
        }
    }
    
    /**
     * Obtiene el primer elemento sin eliminarlo.
     * @return primer elemento o null si está vacía
     */
    public T obtenerPrimero() {
        if (cabeza == null) return null;
        return cabeza.valor;
    }

    /**
     * Elimina el primer elemento de la lista.
     */
    public void eliminarPrimero() {
        if (cabeza == null) return;
        cabeza = cabeza.siguiente;
        tamano--;
    }
}
