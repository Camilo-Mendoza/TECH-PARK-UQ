import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Estructura de cola con dos niveles de prioridad.
 *
 * <p>Los elementos con prioridad alta (valor <= 1) se atienden antes
 * que los elementos en prioridad normal.</p>
 *
 * @param <T> tipo de elemento almacenado en la cola
 */
public class ColaPrioridad<T> {
    private final Queue<T> prioridadAlta;
    private final Queue<T> prioridadNormal;

    /**
     * Crea una cola de prioridad vacía.
     */
    public ColaPrioridad() {
        this.prioridadAlta = new LinkedList<>();
        this.prioridadNormal = new LinkedList<>();
    }

    /**
     * Encola un elemento según su nivel de prioridad.
     *
     * @param elemento elemento a encolar
     * @param prioridad nivel de prioridad (<= 1 es alta; > 1 es normal)
     */
    public void encolar(T elemento, int prioridad) {
        if (elemento == null) {
            return;
        }

        if (prioridad <= 1) {
            prioridadAlta.offer(elemento);
        } else {
            prioridadNormal.offer(elemento);
        }
    }

    /**
     * Desencola el siguiente elemento disponible respetando la prioridad.
     *
     * @return siguiente elemento o null si ambas colas están vacías
     */
    public T desencolar() {
        if (!prioridadAlta.isEmpty()) {
            return prioridadAlta.poll();
        }
        return prioridadNormal.poll();
    }

}
