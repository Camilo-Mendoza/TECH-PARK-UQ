public class Operador {
    private Zona zona;

    public Operador(Zona zona) {
        this.zona = zona;
    }
     //Se actualiza el estado de la atracción y, si es necesario, se asigna un motivo de cierre
    public void gestionarEstadoAtraccion(Atraccion atraccion, EstadoAtraccion estado, MotivosCierre motivo) {
        if (atraccion == null || estado == null) {
            throw new IllegalArgumentException("Atraccion y estado no pueden ser nulos.");
        }

        atraccion.setEstado(estado);

        if (motivo != null) {
            atraccion.setMotivoCierre(motivo);
        }
    }
    
}
