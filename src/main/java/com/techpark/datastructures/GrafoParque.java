package com.techpark.datastructures;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Representa el mapa del parque como un grafo no dirigido.
 *
 * <p>Cada {@link Nodo} corresponde a un punto del parque (por ejemplo una atracción)
 * y las conexiones representan senderos entre ellos.</p>
 */
public class GrafoParque {
    /** Estructura de adyacencia que almacena vecinos por cada nodo. */
    private final Map<Nodo, Set<Nodo>> adyacencias;

    /**
    * Crea un grafo vacío del parque.
    */
    public GrafoParque() {
        this.adyacencias = new HashMap<>();
    }

    /**
     * Agrega un nodo al grafo si no existe previamente.
     *
     * @param nodo nodo a registrar
     */
    public void agregarNodo(Nodo nodo) {
        if (nodo == null) {
            return;
        }
        adyacencias.putIfAbsent(nodo, new HashSet<>());
    }

    /**
     * Conecta dos nodos de forma bidireccional.
     *
     * @param origen nodo de origen
     * @param destino nodo de destino
     */
    public void conectarNodos(Nodo origen, Nodo destino) {
        if (origen == null || destino == null || origen.equals(destino)) {
            return;
        }

        agregarNodo(origen);
        agregarNodo(destino);
        adyacencias.get(origen).add(destino);
        adyacencias.get(destino).add(origen);
    }

    /**
     * Obtiene los vecinos de un nodo.
     *
     * @param nodo nodo a consultar
     * @return conjunto de vecinos o conjunto vacío si no existe
     */
    public Set<Nodo> obtenerVecinos(Nodo nodo) {
        if (nodo == null || !adyacencias.containsKey(nodo)) {
            return new HashSet<>();
        }
        return new HashSet<>(adyacencias.get(nodo));
    }

    /**
     * Retorna la cantidad de nodos registrados en el grafo.
     *
     * @return número de nodos
     */
    public int cantidadNodos() {
        return adyacencias.size();
    }
}
