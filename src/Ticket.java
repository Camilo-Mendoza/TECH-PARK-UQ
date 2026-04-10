import java.time.LocalDate;

public class Ticket {
    private String id;
    private TipoTicket tipo;
    private double precio;
    private LocalDate fechaCompra;
    private boolean activo;
    private Visitante visitante;

    public Ticket(String id, TipoTicket tipo, double precio, LocalDate fechaCompra, Visitante visitante) {
        this.id = id;
        this.tipo = tipo;
        this.precio = precio;
        this.fechaCompra = fechaCompra;
        this.activo = true;
        this.visitante = visitante;
    }

    public void desactivar() {
        this.activo = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TipoTicket getTipo() {
        return tipo;
    }

    public void setTipo(TipoTicket tipo) {
        this.tipo = tipo;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public LocalDate getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(LocalDate fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Visitante getVisitante() {
        return visitante;
    }

    public void setVisitante(Visitante visitante) {
        this.visitante = visitante;
    }
}
