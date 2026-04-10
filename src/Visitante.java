import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;

public class Visitante extends Usuario {
    // Atributos
    private int edad;
    private double estatura;
    private double saldoVirtual;
    private String fotoPase;
    private Ticket ticket;
    private Set<Atraccion> favoritos;
    private ListaEnlazada<RegistroVisita> historialVisitas;
    private Nodo ubicacionActual;

    // Constructor
    public Visitante(String id, String nombre, String email, String contrasena,
                     int edad, double estatura, double saldoVirtual, String fotoPase) {
        super(id, nombre, email, contrasena);
        this.edad = edad;
        this.estatura = estatura;
        this.saldoVirtual = saldoVirtual;
        this.fotoPase = fotoPase;
        this.ticket = null;
        this.favoritos = new HashSet<>();
        this.historialVisitas = new ListaEnlazada<>();
        this.ubicacionActual = null;
    }

    // Métodos de gestión de favoritos
    public void registrarFavorito(Atraccion atraccion) {
        if (atraccion != null) {
            favoritos.add(atraccion);
        }
    }

    public void eliminarFavorito(Atraccion atraccion) {
        if (atraccion != null) {
            favoritos.remove(atraccion);
        }
    }

    public Set<Atraccion> consultarFavoritos() {
        return new HashSet<>(favoritos);
    }

    // Métodos de gestión del historial
    public ListaEnlazada<RegistroVisita> consultarHistorial() {
        return historialVisitas;
    }

    // Métodos de gestión de saldo
    public void recargarSaldo(double monto) {
        if (monto > 0) {
            saldoVirtual += monto;
        }
    }

    public double descontarSaldo(double monto) {
        if (monto > 0 && saldoVirtual >= monto) {
            saldoVirtual -= monto;
            return monto;
        }
        return 0;
    }

    // Métodos de gestión de filas y ubicación
    public boolean unirseAFila(Atraccion atraccion) {
        if (atraccion != null && saldoVirtual > 0) {
            return atraccion.agregarAFila(this);
        }
        return false;
    }

    public int consultarPosicionEnFila(Atraccion atraccion) {
        if (atraccion != null) {
            return atraccion.obtenerPosicionEnFila(this);
        }
        return -1;
    }

    // Método para calcular ruta con el algoritmo de Dijkstra
    public List<Nodo> calcularRuta(Nodo destino) {
        List<Nodo> ruta = new ArrayList<>();
        if (ubicacionActual == null || destino == null) {
            return ruta;
        }

        if (ubicacionActual.equals(destino)) {
            ruta.add(ubicacionActual);
            return ruta;
        }

        Map<Nodo, Double> distancias = new HashMap<>();
        Map<Nodo, Nodo> previos = new HashMap<>();
        Set<Nodo> visitados = new HashSet<>();

        PriorityQueue<Nodo> colaPrioridad = new PriorityQueue<>(
                (a, b) -> Double.compare(
                        distancias.getOrDefault(a, Double.POSITIVE_INFINITY),
                        distancias.getOrDefault(b, Double.POSITIVE_INFINITY)
                )
        );

        distancias.put(ubicacionActual, 0.0);
        colaPrioridad.offer(ubicacionActual);

        while (!colaPrioridad.isEmpty()) {
            Nodo actual = colaPrioridad.poll();

            if (!visitados.add(actual)) {
                continue;
            }

            if (actual.equals(destino)) {
                break;
            }

            for (Nodo vecino : actual.getVecinos()) {
                if (visitados.contains(vecino)) {
                    continue;
                }

                double nuevaDistancia = distancias.get(actual) + 1.0;
                double distanciaActual = distancias.getOrDefault(vecino, Double.POSITIVE_INFINITY);

                if (nuevaDistancia < distanciaActual) {
                    distancias.put(vecino, nuevaDistancia);
                    previos.put(vecino, actual);
                    colaPrioridad.offer(vecino);
                }
            }
        }

        if (!distancias.containsKey(destino)) {
            return ruta;
        }

        LinkedList<Nodo> camino = new LinkedList<>();
        Nodo paso = destino;

        while (paso != null) {
            camino.addFirst(paso);
            paso = previos.get(paso);
        }

        if (!camino.isEmpty() && camino.getFirst().equals(ubicacionActual)) {
            ruta.addAll(camino);
        }

        return ruta;
    }

    // Getters y Setters
    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public double getEstatura() {
        return estatura;
    }

    public void setEstatura(double estatura) {
        this.estatura = estatura;
    }

    public double getSaldoVirtual() {
        return saldoVirtual;
    }

    public void setSaldoVirtual(double saldoVirtual) {
        this.saldoVirtual = saldoVirtual;
    }

    public String getFotoPase() {
        return fotoPase;
    }

    public void setFotoPase(String fotoPase) {
        this.fotoPase = fotoPase;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public Nodo getUbicacionActual() {
        return ubicacionActual;
    }

    public void setUbicacionActual(Nodo ubicacionActual) {
        this.ubicacionActual = ubicacionActual;
    }

    @Override
    public void logout() {
        // Implementar logout específico del visitante
        System.out.println("Visitante " + nombre + " ha cerrado sesión.");
    }
}
