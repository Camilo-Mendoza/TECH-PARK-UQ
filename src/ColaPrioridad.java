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

    /**
     * Obtiene la cantidad total de elementos en ambas colas.
     *
     * @return tamaño total de la estructura
     */
    public int tamano() {
        return prioridadAlta.size() + prioridadNormal.size();
    }

    /**
     * Consulta la posición de un elemento en la cola combinada.
     *
     * <p>Las posiciones comienzan en 1. Primero se cuentan los elementos
     * de prioridad alta y luego los de prioridad normal.</p>
     *
     * @param elemento elemento a buscar
     * @return posición del elemento o -1 si no existe
     */
    public int posicionDe(T elemento) {
        if (elemento == null) {
            return -1;
        }

        int posicionAlta = buscarPosicion(prioridadAlta, elemento);
        if (posicionAlta != -1) {
            return posicionAlta;
        }

        int posicionNormal = buscarPosicion(prioridadNormal, elemento);
        if (posicionNormal != -1) {
            return prioridadAlta.size() + posicionNormal;
        }

        return -1;
    }

    private int buscarPosicion(Queue<T> cola, T elemento) {
        List<T> copia = new ArrayList<>(cola);
        for (int i = 0; i < copia.size(); i++) {
            if (elemento.equals(copia.get(i))) {
                return i + 1;
            }
        }
        return -1;
    }
}
