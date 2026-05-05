package com.techpark.service;

import com.google.gson.*;
import com.techpark.datastructures.Grafo;
import com.techpark.model.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio encargado de cargar y gestionar todos los datos del parque.
 */
public class ParkDataService {

    private final Map<Integer, Atraccion> atracciones = new HashMap<>();
    private final Map<Integer, Zona> zonas = new HashMap<>();
    private final Map<String, Visitante> visitantes = new HashMap<>();
    private final Map<String, Operador> operadores = new HashMap<>();
    private final Map<String, Administrador> administradores = new HashMap<>();
    private final Grafo<Atraccion> grafo = new Grafo<>();

    /**
     * Carga todos los datos desde el archivo data.json.
     */
    public void cargarDatos() {
        try {
            InputStream is = getClass().getResourceAsStream("/data/data.json");
            if (is == null) {
                System.err.println("No se encontró data.json");
                return;
            }

            JsonObject root = JsonParser.parseReader(new InputStreamReader(is)).getAsJsonObject();

            cargarAtracciones(root.getAsJsonArray("atracciones"));
            cargarZonas(root.getAsJsonArray("zonas"));
            cargarConexiones(root.getAsJsonArray("conexiones"));
            cargarVisitantes(root.getAsJsonArray("visitantes"));
            cargarOperadores(root.getAsJsonArray("operadores"));
            cargarAdministradores(root.getAsJsonArray("administradores"));

            System.out.println("Datos cargados: " + atracciones.size() + " atracciones, "
                    + zonas.size() + " zonas, " + grafo.cantidadNodos() + " nodos en grafo.");

        } catch (Exception e) {
            System.err.println("Error cargando datos: " + e.getMessage());
        }
    }

    private void cargarAtracciones(JsonArray array) {
        for (JsonElement el : array) {
            JsonObject obj = el.getAsJsonObject();
            Atraccion a = new Atraccion(
                obj.get("id").getAsString(),
                obj.get("nombre").getAsString(),
                TipoAtraccion.valueOf(obj.get("tipo").getAsString()),
                obj.get("capacidadMaxPorCiclo").getAsInt(),
                obj.get("alturaMinima").getAsDouble(),
                obj.get("edadMinima").getAsInt(),
                obj.get("costoAdicional").getAsDouble(),
                null
            );
            a.setEstado(EstadoAtraccion.valueOf(obj.get("estado").getAsString()));
            a.setContadorVisitantes(obj.get("contadorVisitantes").getAsInt());
            atracciones.put(obj.get("id").getAsInt(), a);
            grafo.agregarNodo(a);
        }
    }

    private void cargarZonas(JsonArray array) {
        for (JsonElement el : array) {
            JsonObject obj = el.getAsJsonObject();
            Zona z = new Zona(
                obj.get("id").getAsString(),
                obj.get("nombre").getAsString(),
                obj.get("capacidadMaxima").getAsInt()
            );
            for (JsonElement idEl : obj.getAsJsonArray("atracciones")) {
                Atraccion a = atracciones.get(idEl.getAsInt());
                if (a != null) {
                    z.agregarAtraccion(a);
                    a.setZona(z);
                }
            }
            zonas.put(obj.get("id").getAsInt(), z);
        }
    }

    private void cargarConexiones(JsonArray array) {
        for (JsonElement el : array) {
            JsonObject obj = el.getAsJsonObject();
            Atraccion origen = atracciones.get(obj.get("origen").getAsInt());
            Atraccion destino = atracciones.get(obj.get("destino").getAsInt());
            int peso = obj.get("peso").getAsInt();
            if (origen != null && destino != null) {
                grafo.conectar(origen, destino, peso);
            }
        }
    }

    private void cargarVisitantes(JsonArray array) {
        for (JsonElement el : array) {
            JsonObject obj = el.getAsJsonObject();
            Visitante v = new Visitante(
                obj.get("id").getAsString(),
                obj.get("nombre").getAsString(),
                obj.get("email").getAsString(),
                obj.get("contrasena").getAsString(),
                obj.get("edad").getAsInt(),
                obj.get("estatura").getAsDouble(),
                obj.get("saldoVirtual").getAsDouble(),
                null
            );
            Ticket ticket = new Ticket(
                "T_" + obj.get("id").getAsString(),
                TipoTicket.valueOf(obj.get("tipoTicket").getAsString()),
                0.0,
                java.time.LocalDate.now(),
                v
            );
            v.setTicket(ticket);
            visitantes.put(obj.get("id").getAsString(), v);
        }
    }

    private void cargarOperadores(JsonArray array) {
        for (JsonElement el : array) {
            JsonObject obj = el.getAsJsonObject();
            Zona zona = zonas.get(obj.get("zonaId").getAsInt());
            Operador o = new Operador(
                obj.get("id").getAsString(),
                obj.get("nombre").getAsString(),
                obj.get("email").getAsString(),
                obj.get("contrasena").getAsString(),
                zona
            );
            operadores.put(obj.get("id").getAsString(), o);
        }
    }

    private void cargarAdministradores(JsonArray array) {
        for (JsonElement el : array) {
            JsonObject obj = el.getAsJsonObject();
            Administrador a = new Administrador(
                obj.get("id").getAsString(),
                obj.get("nombre").getAsString(),
                obj.get("email").getAsString(),
                obj.get("contrasena").getAsString()
            );
            administradores.put(obj.get("id").getAsString(), a);
        }
    }

    // Getters
    public List<Atraccion> getAtracciones() { return new ArrayList<>(atracciones.values()); }
    public List<Zona> getZonas() { return new ArrayList<>(zonas.values()); }
    public List<Visitante> getVisitantes() { return new ArrayList<>(visitantes.values()); }
    public List<Operador> getOperadores() { return new ArrayList<>(operadores.values()); }
    public List<Administrador> getAdministradores() { return new ArrayList<>(administradores.values()); }
    public Grafo<Atraccion> getGrafo() { return grafo; }

    public Atraccion getAtraccionPorId(int id) { return atracciones.get(id); }
    public Visitante getVisitantePorId(String id) { return visitantes.get(id); }
}