import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa una alerta climática que puede afectar la operación
 * de una o varias atracciones del parque.
 */
public class AlertaClimatica {
    private String id;
    private TipoAlerta tipo;
    private LocalDateTime fechaHora;
    private boolean activa;
    private List<Atraccion> atraccionesAfectadas;

    /**
     * Crea una alerta climática inicialmente inactiva.
     *
     * @param id identificador de la alerta
     * @param tipo tipo de alerta
     * @param fechaHora fecha y hora de generación
     * @param atraccionesAfectadas lista de atracciones afectadas
     */
    public AlertaClimatica(String id, TipoAlerta tipo, LocalDateTime fechaHora, List<Atraccion> atraccionesAfectadas) {
        this.id = id;
        this.tipo = tipo;
        this.fechaHora = fechaHora;
        this.activa = false;
        this.atraccionesAfectadas = atraccionesAfectadas != null ? atraccionesAfectadas : new ArrayList<>();
    }

    /**
     * Activa la alerta climática.
     */
    public void activar() {
        this.activa = true;
    }

    /**
     * Desactiva la alerta climática.
     */
    public void desactivar() {
        this.activa = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TipoAlerta getTipo() {
        return tipo;
    }

    public void setTipo(TipoAlerta tipo) {
        this.tipo = tipo;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public List<Atraccion> getAtraccionesAfectadas() {
        return atraccionesAfectadas;
    }

    public void setAtraccionesAfectadas(List<Atraccion> atraccionesAfectadas) {
        this.atraccionesAfectadas = atraccionesAfectadas != null ? atraccionesAfectadas : new ArrayList<>();
    }
}
