package me.petrolingus.swi;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;

public class Controller {

    public Canvas canvas;

    public Slider slider1;
    public Slider slider2;
    public Slider slider3;

    public static boolean[][] sources = new boolean[10][10];
    public static boolean isChanged = false;

    public static float sliderValue1 = 0;
    public static float sliderValue2 = 0;
    public static float sliderValue3 = 0;

    public void initialize() {

        GraphicsContext graphicsContext2D = canvas.getGraphicsContext2D();

        double w = canvas.getWidth() / 10.0;
        double h = canvas.getHeight() / 10.0;

        drawGrid(graphicsContext2D, w, h);

        canvas.setOnMouseClicked(event -> {

            int i = (int) Math.floor(10 * event.getX() / canvas.getWidth());
            int j = (int) Math.floor(10 * event.getY() / canvas.getHeight());
            double x = i * w;
            double y = j * h;
            System.out.println(i + ":" + j);

            if (sources[i][j]) {
                graphicsContext2D.setFill(Color.WHITE);
            } else {
                graphicsContext2D.setFill(Color.BLACK);
            }

            graphicsContext2D.fillRect(x, y, w, h);
            sources[i][j] = !sources[i][j];

            drawGrid(graphicsContext2D, w, h);

            isChanged = true;
        });

        slider1.valueProperty().addListener((observable, oldValue, newValue) -> {
            sliderValue1 = newValue.floatValue();
            isChanged = true;
        });

        slider2.valueProperty().addListener((observable, oldValue, newValue) -> {
            sliderValue2 = newValue.floatValue();
            isChanged = true;
        });

        slider3.valueProperty().addListener((observable, oldValue, newValue) -> {
            sliderValue3 = newValue.floatValue();
            isChanged = true;
        });

        sliderValue1 = (float) slider1.getValue();
        sliderValue2 = (float) slider2.getValue();
        sliderValue3 = (float) slider3.getValue();
    }

    private void drawGrid( GraphicsContext graphicsContext2D, double w, double h) {
        graphicsContext2D.setStroke(Color.BLACK);
        for (int i = 0; i < 10; i++) {
            double y = i * h;
            for (int j = 0; j < 10; j++) {
                double x = j * w;
                graphicsContext2D.strokeRect(x, y, w, h);
            }
        }
    }

}
