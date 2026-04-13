import java.util.ArrayList;
import java.util.List;

/**
 * Árbol Binario de Búsqueda (ABB) para gestionar atracciones por nombre.
 *
 * <p>Permite inserción, búsqueda por nombre y recorrido en orden
 * para obtener atracciones ordenadas alfabéticamente.</p>
 */
public class ABBAtraccion {
    /** Nodo raíz del árbol. */
    private NodoABB raiz;

    /**
     * Inserta una atracción en el ABB usando su nombre como clave de orden.
     *
     * @param atraccion atracción a insertar
     */
    public void insertar(Atraccion atraccion) {
        if (atraccion == null) {
            return;
        }
        raiz = insertarRec(raiz, atraccion);
    }

    /**
     * Busca una atracción por su nombre.
     *
     * @param nombre nombre de la atracción a buscar
     * @return atracción encontrada o null si no existe
     */
    public Atraccion buscarPorNombre(String nombre) {
        NodoABB actual = raiz;
        while (actual != null) {
            int comparacion = nombre.compareToIgnoreCase(actual.valor.getNombre());
            if (comparacion == 0) {
                return actual.valor;
            }
            actual = comparacion < 0 ? actual.izquierdo : actual.derecho;
        }
        return null;
    }

   
}
