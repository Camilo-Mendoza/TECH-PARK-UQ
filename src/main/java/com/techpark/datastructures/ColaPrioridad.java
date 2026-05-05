package com.techpark.datastructures;

/**
 * Estructura de cola con dos niveles de prioridad.
 * Prioridad alta (Fast-Pass) se atiende antes que prioridad normal (General).
 *
 * @param <T> tipo de elemento almacenado en la cola
 */
public class ColaPrioridad<T> {
    private final ListaEnlazada<T> prioridadAlta;
    private final ListaEnlazada<T> prioridadNormal;

    public ColaPrioridad() {
        this.prioridadAlta = new ListaEnlazada<>();
        this.prioridadNormal = new ListaEnlazada<>();
    }

    /**
     * Encola un elemento según su nivel de prioridad.
     * @param elemento elemento a encolar
     * @param prioridad <= 1 es alta (Fast-Pass); > 1 es normal (General)
     */
    public void encolar(T elemento, int prioridad) {
        if (elemento == null) return;
        if (prioridad <= 1) {
            prioridadAlta.insertar(elemento);
        } else {
            prioridadNormal.insertar(elemento);
        }
    }

    /**
     * Desencola el siguiente elemento respetando la prioridad.
     * @return siguiente elemento o null si está vacía
     */
    public T desencolar() {
        if (!prioridadAlta.estaVacia()) {
            T elemento = prioridadAlta.obtenerPrimero();
            prioridadAlta.eliminarPrimero();
            return elemento;
        }
        if (!prioridadNormal.estaVacia()) {
            T elemento = prioridadNormal.obtenerPrimero();
            prioridadNormal.eliminarPrimero();
            return elemento;
        }
        return null;
    }

    public int tamano() {
        return prioridadAlta.tamano() + prioridadNormal.tamano();
    }

    public boolean estaVacia() {
        return prioridadAlta.estaVacia() && prioridadNormal.estaVacia();
    }

    public int posicionDe(T elemento) {
        if (elemento == null) return -1;
        int pos = posicionEnLista(prioridadAlta, elemento);
        if (pos != -1) return pos;
        int posNormal = posicionEnLista(prioridadNormal, elemento);
        if (posNormal != -1) return prioridadAlta.tamano() + posNormal;
        return -1;
    }

    private int posicionEnLista(ListaEnlazada<T> lista, T elemento) {
        java.util.List<T> items = lista.aLista();
        for (int i = 0; i < items.size(); i++) {
            if (elemento.equals(items.get(i))) return i + 1;
        }
        return -1;
    }
}