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

    public boolean login(String email, String contrasena) {
        if (email == null || contrasena == null) {
            return false;
        }

        return this.email.equalsIgnoreCase(email.trim()) && this.contrasena.equals(contrasena);
    }

    public abstract void logout();
}