import java.util.ArrayList;
import java.util.List;

/**
 * Implementación genérica de lista enlazada simple.
 *
 * @param <T> tipo de elemento almacenado
 */
public class ListaEnlazada<T> {
    private NodoLista<T> cabeza;
    private int tamano;

    /**
     * Inserta un elemento al final de la lista.
     *
     * @param valor elemento a insertar
     */
    public void insertar(T valor) {
        if (valor == null) {
            return;
        }

        NodoLista<T> nuevo = new NodoLista<>(valor);
        if (cabeza == null) {
            cabeza = nuevo;
            tamano++;
            return;
        }

        NodoLista<T> actual = cabeza;
        while (actual.siguiente != null) {
            actual = actual.siguiente;
        }
        actual.siguiente = nuevo;
        tamano++;
    }

    /**
     * Elimina la primera ocurrencia del elemento indicado.
     *
     * @param valor elemento a eliminar
     * @return true si se eliminó correctamente, false si no se encontró
     */
    public boolean eliminar(T valor) {
        if (cabeza == null || valor == null) {
            return false;
        }

        if (cabeza.valor.equals(valor)) {
            cabeza = cabeza.siguiente;
            tamano--;
            return true;
        }

        NodoLista<T> actual = cabeza;
        while (actual.siguiente != null) {
            if (actual.siguiente.valor.equals(valor)) {
                actual.siguiente = actual.siguiente.siguiente;
                tamano--;
                return true;
            }
            actual = actual.siguiente;
        }

        return false;
    }

    /**
     * Verifica si el elemento existe en la lista.
     *
     * @param valor elemento a buscar
     * @return true si existe, false en caso contrario
     */
    public boolean contiene(T valor) {
        NodoLista<T> actual = cabeza;
        while (actual != null) {
            if (actual.valor.equals(valor)) {
                return true;
            }
            actual = actual.siguiente;
        }
        return false;
    }

}
