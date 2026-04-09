import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Clase para representar un reporte diario del parque de diversiones
public class Reporte {
    private LocalDate fecha;
    private double ingresosDiarios;
    private List<Atraccion> atraccionesMasVisitadas;
    private Map<String, Double> tiemposPromedioEspera;
    private int cierresPorClima;
    private int alertasMantenimiento;
    private List<Atraccion> atraccionesConIncidentes;
// Constructor por defecto para inicializar el reporte con valores predeterminados
    public Reporte() {
        this.fecha = LocalDate.now();
        this.ingresosDiarios = 0.0;
        this.atraccionesMasVisitadas = new ArrayList<>();
        this.tiemposPromedioEspera = new HashMap<>();
        this.cierresPorClima = 0;
        this.alertasMantenimiento = 0;
        this.atraccionesConIncidentes = new ArrayList<>();
    }
// Constructor completo para inicializar todos los campos del reporte
    public Reporte(LocalDate fecha, double ingresosDiarios, List<Atraccion> atraccionesMasVisitadas,
                   Map<String, Double> tiemposPromedioEspera, int cierresPorClima,
                   int alertasMantenimiento, List<Atraccion> atraccionesConIncidentes) {
        this.fecha = fecha;
        this.ingresosDiarios = ingresosDiarios;
        this.atraccionesMasVisitadas = atraccionesMasVisitadas != null ? atraccionesMasVisitadas : new ArrayList<>();
        this.tiemposPromedioEspera = tiemposPromedioEspera != null ? tiemposPromedioEspera : new HashMap<>();
        this.cierresPorClima = cierresPorClima;
        this.alertasMantenimiento = alertasMantenimiento;
        this.atraccionesConIncidentes = atraccionesConIncidentes != null ? atraccionesConIncidentes : new ArrayList<>();
    }

}

