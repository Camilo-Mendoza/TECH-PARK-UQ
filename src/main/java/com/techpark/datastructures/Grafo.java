package com.techpark.datastructures;

import java.util.*;

/**
 * Grafo no dirigido con pesos para representar el mapa del parque.
 * Nodos = Atracciones; Aristas = Caminos con peso (distancia o tiempo)
 *
 * @param <T> tipo de elemento en cada nodo
 */
public class Grafo<T> {

    private final Map<T, List<Arista<T>>> adyacencias;

    public Grafo() {
        this.adyacencias = new HashMap<>();
    }

    /**
     * Agrega un nodo al grafo.
     */
    public void agregarNodo(T nodo) {
        if (nodo == null) return;
        adyacencias.putIfAbsent(nodo, new ArrayList<>());
    }

    /**
     * Conecta dos nodos con un peso bidireccional.
     */
    public void conectar(T origen, T destino, int peso) {
        if (origen == null || destino == null || origen.equals(destino)) return;
        agregarNodo(origen);
        agregarNodo(destino);
        if (!tieneArista(origen, destino)) {
            adyacencias.get(origen).add(new Arista<>(destino, peso));
            adyacencias.get(destino).add(new Arista<>(origen, peso));
        }
    }

    /**
     * Elimina un nodo y todas sus conexiones.
     */
    public void eliminarNodo(T nodo) {
        if (nodo == null) return;
        adyacencias.remove(nodo);
        for (List<Arista<T>> aristas : adyacencias.values()) {
            aristas.removeIf(a -> nodo.equals(a.destino));
        }
    }

    /**
     * Obtiene las aristas de un nodo.
     */
    public List<Arista<T>> obtenerVecinos(T nodo) {
        return adyacencias.getOrDefault(nodo, new ArrayList<>());
    }

    /**
     * Obtiene todos los nodos del grafo.
     */
    public Set<T> obtenerNodos() {
        return adyacencias.keySet();
    }

    public int cantidadNodos() {
        return adyacencias.size();
    }

    public boolean tieneArista(T origen, T destino) {
        List<Arista<T>> aristas = adyacencias.get(origen);
        if (aristas == null) return false;
        for (Arista<T> a : aristas) {
            if (destino.equals(a.destino)) return true;
        }
        return false;
    }

    /**
     * Algoritmo de Dijkstra para encontrar la ruta más corta.
     * @param inicio nodo de inicio
     * @param fin nodo destino
     * @return lista de nodos en la ruta óptima, vacía si no hay ruta
     */
    public List<T> dijkstra(T inicio, T fin) {
        if (inicio == null || fin == null) return new ArrayList<>();
        if (!adyacencias.containsKey(inicio) || !adyacencias.containsKey(fin)) return new ArrayList<>();

        Map<T, Integer> distancias = new HashMap<>();
        Map<T, T> anteriores = new HashMap<>();
        PriorityQueue<NodoDistancia<T>> cola = new PriorityQueue<>();

        for (T nodo : adyacencias.keySet()) {
            distancias.put(nodo, Integer.MAX_VALUE);
        }
        distancias.put(inicio, 0);
        cola.add(new NodoDistancia<>(inicio, 0));

        while (!cola.isEmpty()) {
            NodoDistancia<T> actual = cola.poll();

            if (actual.distancia > distancias.getOrDefault(actual.nodo, Integer.MAX_VALUE)) continue;
            if (actual.nodo.equals(fin)) break;

            for (Arista<T> arista : obtenerVecinos(actual.nodo)) {
                int nuevaDistancia = distancias.get(actual.nodo) + arista.peso;
                if (nuevaDistancia < distancias.getOrDefault(arista.destino, Integer.MAX_VALUE)) {
                    distancias.put(arista.destino, nuevaDistancia);
                    anteriores.put(arista.destino, actual.nodo);
                    cola.add(new NodoDistancia<>(arista.destino, nuevaDistancia));
                }
            }
        }

        return reconstruirRuta(inicio, fin, anteriores, distancias);
    }

    private List<T> reconstruirRuta(T inicio, T fin, Map<T, T> anteriores, Map<T, Integer> distancias) {
        List<T> ruta = new ArrayList<>();
        if (distancias.getOrDefault(fin, Integer.MAX_VALUE) == Integer.MAX_VALUE) return ruta;

        LinkedList<T> camino = new LinkedList<>();
        T actual = fin;
        while (actual != null) {
            camino.addFirst(actual);
            actual = anteriores.get(actual);
        }

        if (!camino.isEmpty() && camino.getFirst().equals(inicio)) {
            ruta.addAll(camino);
        }
        return ruta;
    }

    /**
     * Representa una arista con destino y peso.
     */
    public static class Arista<T> {
        public final T destino;
        public final int peso;

        public Arista(T destino, int peso) {
            this.destino = destino;
            this.peso = peso;
        }
    }

    private static class NodoDistancia<T> implements Comparable<NodoDistancia<T>> {
        T nodo;
        int distancia;

        NodoDistancia(T nodo, int distancia) {
            this.nodo = nodo;
            this.distancia = distancia;
        }

        @Override
        public int compareTo(NodoDistancia<T> otro) {
            return Integer.compare(this.distancia, otro.distancia);
        }
    }
}