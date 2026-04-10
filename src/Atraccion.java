import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Representa una atracción del parque con sus reglas de operación,
 * seguridad, disponibilidad y mantenimiento preventivo.
 */
public class Atraccion {
    
    private String id;
    private String nombre;
    private TipoAtraccion tipo;
    private int capacidadMaxPorCiclo;
    private double alturaMinima;
    private int edadMinima;
    private double costoAdicional;
    private int contadorVisitantes;
    private int tiempoEsperaEstimado;
    private EstadoAtraccion estado;
    private MotivosCierre motivoCierre;
    private Zona zona;
    private ColaPrioridad<Visitante> cola;

    /**
     * Crea una atracción con valores por defecto para capacidad y restricciones.
     *
     * @param nombre nombre de la atracción
     */
    public Atraccion(String nombre) {
        this("ATR_" + System.currentTimeMillis(), nombre, TipoAtraccion.OTRA, 20, 0.0, 0, 0.0, null);
    }

    /**
     * Crea una atracción con configuración completa.
     *
     * @param id identificador único
     * @param nombre nombre de la atracción
     * @param tipo tipo de atracción
     * @param capacidadMaxPorCiclo capacidad máxima por ciclo
     * @param alturaMinima altura mínima requerida
     * @param edadMinima edad mínima requerida
     * @param costoAdicional costo adicional de acceso
     * @param zona zona del parque asociada
     */
    public Atraccion(String id,
                     String nombre,
                     TipoAtraccion tipo,
                     int capacidadMaxPorCiclo,
                     double alturaMinima,
                     int edadMinima,
                     double costoAdicional,
                     Zona zona) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.capacidadMaxPorCiclo = capacidadMaxPorCiclo;
        this.alturaMinima = alturaMinima;
        this.edadMinima = edadMinima;
        this.costoAdicional = costoAdicional;
        this.contadorVisitantes = 0;
        this.tiempoEsperaEstimado = 0;
        this.estado = EstadoAtraccion.ACTIVA;
        this.motivoCierre = MotivosCierre.SIN_MOTIVO;
        this.zona = zona;
        this.cola = new ColaPrioridad<>();
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

    public TipoAtraccion getTipo() {
        return tipo;
    }

    public void setTipo(TipoAtraccion tipo) {
        this.tipo = tipo;
    }

    public int getCapacidadMaxPorCiclo() {
        return capacidadMaxPorCiclo;
    }

    public void setCapacidadMaxPorCiclo(int capacidadMaxPorCiclo) {
        this.capacidadMaxPorCiclo = capacidadMaxPorCiclo;
    }

    public double getAlturaMinima() {
        return alturaMinima;
    }

    public void setAlturaMinima(double alturaMinima) {
        this.alturaMinima = alturaMinima;
    }

    public int getEdadMinima() {
        return edadMinima;
    }

    public void setEdadMinima(int edadMinima) {
        this.edadMinima = edadMinima;
    }

    public double getCostoAdicional() {
        return costoAdicional;
    }

    public void setCostoAdicional(double costoAdicional) {
        this.costoAdicional = costoAdicional;
    }

    public int getContadorVisitantes() {
        return contadorVisitantes;
    }

    public void setContadorVisitantes(int contadorVisitantes) {
        this.contadorVisitantes = contadorVisitantes;
    }

    public int getTiempoEsperaEstimado() {
        return tiempoEsperaEstimado;
    }

    public void setTiempoEsperaEstimado(int tiempoEsperaEstimado) {
        this.tiempoEsperaEstimado = tiempoEsperaEstimado;
    }

    public EstadoAtraccion getEstado() {
        return estado;
    }

    public void setEstado(EstadoAtraccion estado) {
        this.estado = estado;
    }

    public MotivosCierre getMotivoCierre() {
        return motivoCierre;
    }

    public void setMotivoCierre(MotivosCierre motivoCierre) {
        this.motivoCierre = motivoCierre;
    }

    public Zona getZona() {
        return zona;
    }

    public void setZona(Zona zona) {
        this.zona = zona;
    }

    public ColaPrioridad<Visitante> getCola() {
        return cola;
    }

    public void setCola(ColaPrioridad<Visitante> cola) {
        this.cola = cola;
    }

    /**
     * Cambia el estado operativo de la atracción.
     * Si se establece en cerrada, registra el motivo de cierre.
     *
     * @param estado nuevo estado de la atracción
     * @param motivo motivo del cambio cuando aplica cierre
     */
    public void cambiarEstado(EstadoAtraccion estado, MotivosCierre motivo) {
        if (estado == null) {
            throw new IllegalArgumentException("El estado no puede ser nulo.");
        }

        this.estado = estado;

        if (estado == EstadoAtraccion.CERRADA) {
            this.motivoCierre = (motivo != null) ? motivo : MotivosCierre.OTRO;
        } else {
            this.motivoCierre = MotivosCierre.SIN_MOTIVO;
        }
    }

    /**
     * Incrementa el contador acumulado de visitantes y recalcula reglas derivadas
     * como mantenimiento preventivo y tiempo estimado de espera.
     */
    public void incrementarContador() {
        this.contadorVisitantes++;
        verificarMantenimientoPreventivo();
        actualizarTiempoEspera();
    }

    /**
     * Verifica si la atracción alcanzó el umbral de mantenimiento preventivo.
     * Al llegar a 500 visitantes acumulados cambia el estado a mantenimiento.
     *
     * @return true si se activó mantenimiento, false en caso contrario
     */
    public boolean verificarMantenimientoPreventivo() {
        if (this.contadorVisitantes >= 500) {
            this.estado = EstadoAtraccion.EN_MANTENIMIENTO;
            this.motivoCierre = MotivosCierre.REVISION_TECNICA;
            return true;
        }
        return false;
    }

   
}

