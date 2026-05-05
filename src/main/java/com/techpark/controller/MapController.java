package com.techpark.controller;

import com.techpark.datastructures.Grafo;
import com.techpark.model.Atraccion;
import com.techpark.model.EstadoAtraccion;
import com.techpark.service.ParkDataService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapController {

    @FXML private Pane canvas;
    @FXML private Label pathStatusLabel;

    private final ParkDataService dataService;
    private final Map<Atraccion, double[]> posiciones = new HashMap<>();
    private Atraccion nodoOrigen = null;
    private List<Atraccion> rutaResaltada = null;

    private static final double RADIO_NODO = 18.0;

    public MapController(ParkDataService dataService) {
        this.dataService = dataService;
    }

    @FXML
    public void initialize() {
        canvas.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) renderMapa();
        });
        canvas.widthProperty().addListener((obs, o, n) -> renderMapa());
        canvas.heightProperty().addListener((obs, o, n) -> renderMapa());
    }

    private void renderMapa() {
        canvas.getChildren().clear();
        List<Atraccion> atracciones = dataService.getAtracciones();
        Grafo<Atraccion> grafo = dataService.getGrafo();

        calcularPosiciones(atracciones);
        dibujarAristas(grafo, atracciones);
        dibujarRuta();
        dibujarNodos(atracciones);
    }

    private void calcularPosiciones(List<Atraccion> atracciones) {
        posiciones.clear();
        int total = atracciones.size();
        if (total == 0) return;

        double ancho = canvas.getWidth() > 0 ? canvas.getWidth() : 800;
        double alto = canvas.getHeight() > 0 ? canvas.getHeight() : 560;
        double centroX = ancho / 2;
        double centroY = alto / 2;
        double radio = Math.min(ancho, alto) * 0.35;

        for (int i = 0; i < total; i++) {
            double angulo = (2 * Math.PI * i) / total;
            double x = centroX + radio * Math.cos(angulo);
            double y = centroY + radio * Math.sin(angulo);
            posiciones.put(atracciones.get(i), new double[]{x, y});
        }
    }

    private void dibujarAristas(Grafo<Atraccion> grafo, List<Atraccion> atracciones) {
        for (Atraccion origen : atracciones) {
            double[] posOrigen = posiciones.get(origen);
            if (posOrigen == null) continue;

            for (Grafo.Arista<Atraccion> arista : grafo.obtenerVecinos(origen)) {
                double[] posDestino = posiciones.get(arista.destino);
                if (posDestino == null) continue;

                Line linea = new Line(posOrigen[0], posOrigen[1], posDestino[0], posDestino[1]);
                linea.setStroke(Color.web("#94a3b8"));
                linea.setStrokeWidth(2);
                canvas.getChildren().add(linea);

                // Mostrar peso
                double mx = (posOrigen[0] + posDestino[0]) / 2;
                double my = (posOrigen[1] + posDestino[1]) / 2;
                Text peso = new Text(mx, my, String.valueOf(arista.peso));
                peso.setFill(Color.web("#64748b"));
                peso.setStyle("-fx-font-size: 10px;");
                canvas.getChildren().add(peso);
            }
        }
    }

    private void dibujarRuta() {
        if (rutaResaltada == null || rutaResaltada.size() < 2) return;
        for (int i = 0; i < rutaResaltada.size() - 1; i++) {
            double[] posA = posiciones.get(rutaResaltada.get(i));
            double[] posB = posiciones.get(rutaResaltada.get(i + 1));
            if (posA == null || posB == null) continue;
            Line linea = new Line(posA[0], posA[1], posB[0], posB[1]);
            linea.setStroke(Color.web("#2563eb"));
            linea.setStrokeWidth(5);
            canvas.getChildren().add(linea);
        }
    }

    private void dibujarNodos(List<Atraccion> atracciones) {
        for (Atraccion a : atracciones) {
            double[] pos = posiciones.get(a);
            if (pos == null) continue;

            Circle circulo = new Circle(pos[0], pos[1], RADIO_NODO);
            circulo.setFill(colorPorEstado(a.getEstado()));
            circulo.setStroke(esNodoEnRuta(a) ? Color.web("#1d4ed8") : Color.web("#0f172a"));
            circulo.setStrokeWidth(esNodoEnRuta(a) ? 4 : 2);

            circulo.setOnMouseClicked(e -> seleccionarNodo(a));
            circulo.setOnMouseEntered(e -> circulo.setOpacity(0.8));
            circulo.setOnMouseExited(e -> circulo.setOpacity(1.0));

            Text nombre = new Text(pos[0] - RADIO_NODO, pos[1] + RADIO_NODO + 14, a.getNombre());
            nombre.setFill(Color.web("#0f172a"));
            nombre.setStyle("-fx-font-size: 11px; -fx-font-weight: bold;");

            canvas.getChildren().addAll(circulo, nombre);
        }
    }

    private void seleccionarNodo(Atraccion a) {
        if (nodoOrigen == null) {
            nodoOrigen = a;
            pathStatusLabel.setText("Origen: " + a.getNombre() + " — ahora selecciona el destino");
        } else {
            List<Atraccion> ruta = dataService.getGrafo().dijkstra(nodoOrigen, a);
            rutaResaltada = ruta;
            pathStatusLabel.setText(ruta.isEmpty()
                ? "No hay ruta entre " + nodoOrigen.getNombre() + " y " + a.getNombre()
                : "Ruta: " + ruta.stream().map(Atraccion::getNombre)
                    .reduce((x, y) -> x + " → " + y).orElse(""));
            nodoOrigen = null;
            renderMapa();
        }
    }

    private boolean esNodoEnRuta(Atraccion a) {
        return rutaResaltada != null && rutaResaltada.contains(a);
    }

    private Color colorPorEstado(EstadoAtraccion estado) {
        return switch (estado) {
            case ACTIVA -> Color.web("#16a34a");
            case EN_MANTENIMIENTO -> Color.web("#f97316");
            case CERRADA -> Color.web("#dc2626");
        };
    }
}