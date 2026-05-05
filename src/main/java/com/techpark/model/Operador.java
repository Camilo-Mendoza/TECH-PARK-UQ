package com.techpark.model;

public class Operador extends Usuario {
    private Zona zona;
     // Constructor
    public Operador(String id, String nombre, String email, String contrasena, Zona zona) {
        super(id, nombre, email, contrasena);
        this.zona = zona;
    }
    // Getters y Setters
    public Zona getZona() {
        return zona;
    }

    public void setZona(Zona zona) {
        this.zona = zona;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    // Se actualiza el estado de la atracción y, si es necesario, se asigna un motivo de cierre
    public void gestionarEstadoAtraccion(Atraccion atraccion, EstadoAtraccion estado, MotivosCierre motivo) {
        if (atraccion == null || estado == null) {
            throw new IllegalArgumentException("Atraccion y estado no pueden ser nulos.");
        }

        atraccion.setEstado(estado);

        if (motivo != null) {
            atraccion.setMotivoCierre(motivo);
        }
    }

    // Se registra una revisión técnica en la atracción, lo que puede incluir la actualización de su estado si se detectan problemas durante la revisión.
    public void registrarRevisionTecnica(Atraccion atraccion) {
        if (atraccion == null) {
            throw new IllegalArgumentException("Atraccion no puede ser nula.");
        }

        atraccion.registrarRevisionTecnica();
    }

    // Se procesa al siguiente visitante en la fila de una atracción, lo que implica verificar su ticket, actualizar su ubicación y gestionar su acceso a la atracción.
    public Visitante procesarSiguienteEnFila(Atraccion atraccion) {
        if (atraccion == null) {
            throw new IllegalArgumentException("Atraccion no puede ser nula.");
        }

        return atraccion.procesarSiguienteEnFila();
    }

    // Se valida el acceso de un visitante a una atracción, lo que implica verificar si cumple con los requisitos de edad, estatura y otros criterios establecidos para esa atracción.
    public boolean validarAcceso(Visitante visitante, Atraccion atraccion) {
        if (visitante == null || atraccion == null) {
            throw new IllegalArgumentException("Visitante y atracción no pueden ser nulos.");
        }

        return atraccion.validarAcceso(visitante);
    }

    @Override
    public void logout() {
        System.out.println("Operador " + nombre + " ha cerrado sesión.");
    }
}


