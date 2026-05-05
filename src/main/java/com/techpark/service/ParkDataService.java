package com.techpark.service;

import com.google.gson.*;
import com.techpark.datastructures.Grafo;
import com.techpark.model.*;

import java.io.File;
import java.io.FileWriter;

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
        // LIMPIAR estructuras antes de cargar
        atracciones.clear();
        zonas.clear();
        // visitantes.clear();     // No limpiamos visitantes para mantener favoritos
        // operadores.clear();     // No limpiamos operadores
        // administradores.clear(); // No limpiamos administradores
        
        // Limpiar grafo completamente
        for (Atraccion nodo : new ArrayList<>(grafo.obtenerNodos())) {
            grafo.eliminarNodo(nodo);
        }
        
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
            String id = obj.get("id").getAsString();
            
            // Verificar si el visitante ya existe (para mantener sus favoritos en memoria)
            Visitante existente = visitantes.get(id);
            
            Visitante v = new Visitante(
                id,
                obj.get("nombre").getAsString(),
                obj.get("email").getAsString(),
                obj.get("contrasena").getAsString(),
                obj.get("edad").getAsInt(),
                obj.get("estatura").getAsDouble(),
                obj.get("saldoVirtual").getAsDouble(),
                null
            );
            
            Ticket ticket = new Ticket(
                "T_" + id,
                TipoTicket.valueOf(obj.get("tipoTicket").getAsString()),
                0.0,
                java.time.LocalDate.now(),
                v
            );
            v.setTicket(ticket);
            
            // CARGAR FAVORITOS DESDE JSON (si existen)
            if (obj.has("favoritos")) {
                JsonArray favArray = obj.getAsJsonArray("favoritos");
                for (JsonElement favEl : favArray) {
                    String nombreAtraccion = favEl.getAsString();
                    // Buscar la atracción por nombre
                    for (Atraccion a : atracciones.values()) {
                        if (a.getNombre().equals(nombreAtraccion)) {
                            v.registrarFavorito(a);
                            break;
                        }
                    }
                }
            } 
            // Si no hay favoritos en JSON, mantener los que tenía en memoria
            else if (existente != null) {
                for (Atraccion a : existente.consultarFavoritos().aLista()) {
                    v.registrarFavorito(a);
                }
            }
            
            visitantes.put(id, v);
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

    /**
    * Guarda los favoritos actuales en el archivo JSON.
    */
    public void guardarFavoritos() {
        guardarDatos();
    }

        /**
     * Guarda todos los datos actuales en el archivo data.json,
     * incluyendo favoritos de visitantes, estado de atracciones, etc.
     */
    public void guardarDatos() {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonObject root = new JsonObject();

            // ============ ATRAcciones ============
            JsonArray atrArray = new JsonArray();
            for (Map.Entry<Integer, Atraccion> entry : atracciones.entrySet()) {
                Atraccion a = entry.getValue();
                JsonObject obj = new JsonObject();
                obj.addProperty("id", entry.getKey());
                obj.addProperty("nombre", a.getNombre());
                obj.addProperty("tipo", a.getTipo().name());
                obj.addProperty("capacidadMaxPorCiclo", a.getCapacidadMaxPorCiclo());
                obj.addProperty("alturaMinima", a.getAlturaMinima());
                obj.addProperty("edadMinima", a.getEdadMinima());
                obj.addProperty("costoAdicional", a.getCostoAdicional());
                obj.addProperty("zonaId", a.getZona() != null ? Integer.parseInt(a.getZona().getId()) : 1);
                obj.addProperty("estado", a.getEstado().name());
                obj.addProperty("contadorVisitantes", a.getContadorVisitantes());
                obj.addProperty("posX", 0.0);
                obj.addProperty("posY", 0.0);
                atrArray.add(obj);
            }
            root.add("atracciones", atrArray);

            // ============ ZONAS ============
            JsonArray zonasArray = new JsonArray();
            for (Map.Entry<Integer, Zona> entry : zonas.entrySet()) {
                Zona z = entry.getValue();
                JsonObject obj = new JsonObject();
                obj.addProperty("id", entry.getKey());
                obj.addProperty("nombre", z.getNombre());
                obj.addProperty("capacidadMaxima", z.getCapacidadMaxima());
                JsonArray ids = new JsonArray();
                for (Atraccion a : z.getAtracciones().aLista()) {
                    for (Map.Entry<Integer, Atraccion> atrEntry : atracciones.entrySet()) {
                        if (atrEntry.getValue().equals(a)) {
                            ids.add(atrEntry.getKey());
                            break;
                        }
                    }
                }
                obj.add("atracciones", ids);
                zonasArray.add(obj);
            }
            root.add("zonas", zonasArray);

            // ============ CONEXIONES ============
            JsonArray conexArray = new JsonArray();
            for (Atraccion origen : grafo.obtenerNodos()) {
                for (Grafo.Arista<Atraccion> arista : grafo.obtenerVecinos(origen)) {
                    int idOrigen = -1;
                    int idDestino = -1;
                    
                    for (Map.Entry<Integer, Atraccion> entry : atracciones.entrySet()) {
                        if (entry.getValue().equals(origen)) idOrigen = entry.getKey();
                        if (entry.getValue().equals(arista.destino)) idDestino = entry.getKey();
                    }
                    
                    if (idOrigen < idDestino && idOrigen != -1 && idDestino != -1) {
                        JsonObject obj = new JsonObject();
                        obj.addProperty("origen", idOrigen);
                        obj.addProperty("destino", idDestino);
                        obj.addProperty("peso", arista.peso);
                        conexArray.add(obj);
                    }
                }
            }
            root.add("conexiones", conexArray);

            // ============ VISITANTES ============
            JsonArray visArray = new JsonArray();
            for (Visitante v : visitantes.values()) {
                JsonObject obj = new JsonObject();
                obj.addProperty("id", v.getId());
                obj.addProperty("nombre", v.getNombre());
                obj.addProperty("email", v.getEmail());
                obj.addProperty("contrasena", v.getContrasena());
                obj.addProperty("edad", v.getEdad());
                obj.addProperty("estatura", v.getEstatura());
                obj.addProperty("saldoVirtual", v.getSaldoVirtual());
                obj.addProperty("tipoTicket", v.getTicket() != null
                        ? v.getTicket().getTipo().name() : "GENERAL");
                
                // FAVORITOS
                JsonArray favArray = new JsonArray();
                for (Atraccion a : v.consultarFavoritos().aLista()) {
                    favArray.add(a.getNombre());
                }
                obj.add("favoritos", favArray);
                
                visArray.add(obj);
            }
            root.add("visitantes", visArray);

            // ============ OPERADORES ============
            JsonArray opArray = new JsonArray();
            for (Operador o : operadores.values()) {
                JsonObject obj = new JsonObject();
                obj.addProperty("id", o.getId());
                obj.addProperty("nombre", o.getNombre());
                obj.addProperty("email", o.getEmail());
                obj.addProperty("contrasena", o.getContrasena());
                obj.addProperty("zonaId", o.getZona() != null 
                        ? Integer.parseInt(o.getZona().getId()) : 1);
                opArray.add(obj);
            }
            root.add("operadores", opArray);

            // ============ ADMINISTRADORES ============
            JsonArray admArray = new JsonArray();
            for (Administrador a : administradores.values()) {
                JsonObject obj = new JsonObject();
                obj.addProperty("id", a.getId());
                obj.addProperty("nombre", a.getNombre());
                obj.addProperty("email", a.getEmail());
                obj.addProperty("contrasena", a.getContrasena());
                admArray.add(obj);
            }
            root.add("administradores", admArray);

            // ============ ESCRIBIR ARCHIVO (CORREGIDO) ============
            // Intentar guardar en target/classes primero (donde Maven ejecuta)
            java.net.URL url = getClass().getResource("/data/data.json");
            
            if (url != null) {
                // Opción 1: Guardar en target/classes/data/data.json
                java.io.File file = new java.io.File(url.toURI());
                java.io.FileWriter writer = new java.io.FileWriter(file);
                writer.write(gson.toJson(root));
                writer.close();
                System.out.println("✅ Datos guardados en: " + file.getAbsolutePath());
                
                // Opción 2: Intentar guardar también en src/main/resources (para persistencia real)
                try {
                    // Buscar la ruta del proyecto
                    String projectPath = System.getProperty("user.dir");
                    java.io.File srcFile = new java.io.File(projectPath, 
                        "src/main/resources/data/data.json");
                    
                    if (srcFile.getParentFile().exists() || srcFile.getParentFile().mkdirs()) {
                        java.io.FileWriter srcWriter = new java.io.FileWriter(srcFile);
                        srcWriter.write(gson.toJson(root));
                        srcWriter.close();
                        System.out.println("✅ También guardado en: " + srcFile.getAbsolutePath());
                    }
                } catch (Exception e) {
                    System.out.println("⚠️ No se pudo guardar en src/main/resources: " + e.getMessage());
                }
            } else {
                // Si no encuentra el recurso, guardar directamente en src/main/resources
                System.err.println("⚠️ No se encontró el recurso en target, guardando en src...");
                
                String projectPath = System.getProperty("user.dir");
                java.io.File srcFile = new java.io.File(projectPath, 
                    "src/main/resources/data/data.json");
                
                if (srcFile.getParentFile().exists() || srcFile.getParentFile().mkdirs()) {
                    java.io.FileWriter writer = new java.io.FileWriter(srcFile);
                    writer.write(gson.toJson(root));
                    writer.close();
                    System.out.println("✅ Datos guardados en: " + srcFile.getAbsolutePath());
                } else {
                    System.err.println("❌ No se pudo crear el directorio para guardar");
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Error guardando datos: " + e.getMessage());
            e.printStackTrace();
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