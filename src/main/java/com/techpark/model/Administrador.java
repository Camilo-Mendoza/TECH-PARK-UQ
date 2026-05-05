package com.techpark.model;

public class Administrador extends Usuario {
    public Administrador(String id, String nombre, String email, String contrasena) {
        super(id, nombre, email, contrasena);
    }

    // Métodos específicos del administrador
    public Zona crearZona(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la zona no puede estar vacío.");
        }
        return new Zona(nombre);
    }

    public Atraccion crearAtraccion(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la atracción no puede estar vacío.");
        }
        return new Atraccion(nombre);
    }

    public void asignarOperadorAZona(Operador operador, Zona zona) {
        if (operador == null || zona == null) {
            throw new IllegalArgumentException("Operador y zona no pueden ser nulos.");
        }
        operador.setZona(zona);
        zona.asignarOperador(operador);
     }

    public void activarAlertaClimatica(TipoAlerta tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de alerta no puede ser nulo.");
        }
        System.out.println("Alerta climática activada: " + tipo);
    }

    public Reporte generarReporteDiario() {
        return new Reporte();
    }

    public GrafoParque consultarGrafo() {
        return new GrafoParque();
    }
// Método para gestionar empleados, que podría incluir operadores y otros administradores
    public void gestionarEmpleado(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo.");
        }
        System.out.println("Gestionando empleado: " + usuario.getNombre());
    }
// Método para consultar el árbol de atracciones, que podría ser útil para la gestión y organización del parque
    public ABBAtraccion consultarArbolAtracciones() {
        return new ABBAtraccion();
    }

    @Override
    public void logout() {
        System.out.println("Administrador " + nombre + " ha cerrado sesión.");
    }
}

