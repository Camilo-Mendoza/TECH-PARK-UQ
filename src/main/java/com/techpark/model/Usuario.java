package com.techpark.model;

public abstract class Usuario {
    protected String id;
    protected String nombre;
    protected String email;
    protected String contrasena;

    public Usuario(String id, String nombre, String email, String contrasena) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.contrasena = contrasena;
    }
    // Getters y Setters
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

    public boolean login(String email, String contrasena) {
        if (email == null || contrasena == null) {
            return false;
        }

        return this.email.equalsIgnoreCase(email.trim()) && this.contrasena.equals(contrasena);
    }

    public abstract void logout();
}