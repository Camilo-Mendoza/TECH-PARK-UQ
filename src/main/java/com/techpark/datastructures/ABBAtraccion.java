package com.techpark.datastructures;

import java.util.ArrayList;
import java.util.List;

/**
 * Árbol Binario de Búsqueda (ABB) para gestionar atracciones por nombre.
 *
 * <p>Permite inserción, búsqueda por nombre y recorrido en orden
 * para obtener atracciones ordenadas alfabéticamente.</p>
 */
public class ABBAtraccion {
    /** Nodo raíz del árbol. */
    private NodoABB raiz;

    /**
     * Inserta una atracción en el ABB usando su nombre como clave de orden.
     *
     * @param atraccion atracción a insertar
     */
    public void insertar(Atraccion atraccion) {
        if (atraccion == null) {
            return;
        }
        raiz = insertarRec(raiz, atraccion);
    }

    /**
     * Busca una atracción por su nombre.
     *
     * @param nombre nombre de la atracción a buscar
     * @return atracción encontrada o null si no existe
     */
    public Atraccion buscarPorNombre(String nombre) {
        NodoABB actual = raiz;
        while (actual != null) {
            int comparacion = nombre.compareToIgnoreCase(actual.valor.getNombre());
            if (comparacion == 0) {
                return actual.valor;
            }
            actual = comparacion < 0 ? actual.izquierdo : actual.derecho;
        }
        return null;
    }

    /**
     * Obtiene todas las atracciones ordenadas por nombre
     * mediante recorrido in-order del árbol.
     *
     * @return lista de atracciones ordenadas
     */
    public List<Atraccion> inOrden() {
        List<Atraccion> resultado = new ArrayList<>();
        inOrdenRec(raiz, resultado);
        return resultado;
    }

    private NodoABB insertarRec(NodoABB nodo, Atraccion atraccion) {
        if (nodo == null) {
            return new NodoABB(atraccion);
        }

        int comparacion = atraccion.getNombre().compareToIgnoreCase(nodo.valor.getNombre());
        if (comparacion < 0) {
            nodo.izquierdo = insertarRec(nodo.izquierdo, atraccion);
        } else if (comparacion > 0) {
            nodo.derecho = insertarRec(nodo.derecho, atraccion);
        }

        return nodo;
    }

    private void inOrdenRec(NodoABB nodo, List<Atraccion> resultado) {
        if (nodo == null) {
            return;
        }
        inOrdenRec(nodo.izquierdo, resultado);
        resultado.add(nodo.valor);
        inOrdenRec(nodo.derecho, resultado);
    }

    private static class NodoABB {
        private final Atraccion valor;
        private NodoABB izquierdo;
        private NodoABB derecho;

        private NodoABB(Atraccion valor) {
            this.valor = valor;
        }
    }
}
