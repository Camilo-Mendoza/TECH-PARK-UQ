import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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

    public Atraccion(String nombre) {
        this("ATR_" + System.currentTimeMillis(), nombre, TipoAtraccion.OTRA, 20, 0.0, 0, 0.0, null);
    }

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
}

