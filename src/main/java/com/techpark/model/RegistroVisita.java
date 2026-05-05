    package com.techpark.model;

    import java.time.LocalDateTime;

    /**
     * Representa el registro de una visita realizada por un visitante
     * a una atracción del parque en una fecha y hora específica.
     */
    public class RegistroVisita {
        private String id;
        private Visitante visitante;
        private Atraccion atraccion;
        private LocalDateTime fechaHora;
        private double costoTotal;

        /**
         * Crea un registro de visita con todos sus datos.
         *
         * @param id identificador del registro
         * @param visitante visitante asociado
         * @param atraccion atracción visitada
         * @param fechaHora fecha y hora de la visita
         * @param costoTotal costo total generado
         */
        public RegistroVisita(String id, Visitante visitante, Atraccion atraccion, LocalDateTime fechaHora, double costoTotal) {
            this.id = id;
            this.visitante = visitante;
            this.atraccion = atraccion;
            this.fechaHora = fechaHora;
            this.costoTotal = costoTotal;
        }

        /**
         * Obtiene el identificador del registro.
         *
         * @return id del registro
         */
        public String getId() {
            return id;
        }

        /**
         * Actualiza el identificador del registro.
         *
         * @param id nuevo id
         */
        public void setId(String id) {
            this.id = id;
        }

        /**
         * Obtiene el visitante asociado al registro.
         *
         * @return visitante del registro
         */
        public Visitante getVisitante() {
            return visitante;
        }

        /**
         * Actualiza el visitante asociado al registro.
         *
         * @param visitante nuevo visitante
         */
        public void setVisitante(Visitante visitante) {
            this.visitante = visitante;
        }

        /**
         * Obtiene la atracción visitada.
         *
         * @return atracción asociada
         */
        public Atraccion getAtraccion() {
            return atraccion;
        }

        /**
         * Actualiza la atracción asociada al registro.
         *
         * @param atraccion nueva atracción
         */
        public void setAtraccion(Atraccion atraccion) {
            this.atraccion = atraccion;
        }

        /**
         * Obtiene la fecha y hora del registro.
         *
         * @return fecha y hora de la visita
         */
        public LocalDateTime getFechaHora() {
            return fechaHora;
        }

        /**
         * Actualiza la fecha y hora del registro.
         *
         * @param fechaHora nueva fecha y hora
         */
        public void setFechaHora(LocalDateTime fechaHora) {
            this.fechaHora = fechaHora;
        }

        /**
         * Obtiene el costo total de la visita.
         *
         * @return costo total
         */
        public double getCostoTotal() {
            return costoTotal;
        }

        /**
         * Actualiza el costo total de la visita.
         *
         * @param costoTotal nuevo costo total
         */
        public void setCostoTotal(double costoTotal) {
            this.costoTotal = costoTotal;
        }
    }
