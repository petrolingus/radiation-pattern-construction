package me.petrolingus.rpc;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextField;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import me.petrolingus.rpc.lwjgl.Window;
import me.petrolingus.rpc.util.MouseInput;

import java.nio.ByteBuffer;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Controller {

    public Canvas canvas;

    public Canvas radiationPatternCanvas;

    public TextField lambdaField;
    public TextField kField;
    public TextField radiusField;

    public static int n = 11;
    public static boolean[][] sources = new boolean[n][n];
    public static boolean isChanged = false;

    public static float lambda = 1;
    public static float omega = 0.5f;
    public static float radius = 200;

    public void initialize() {

        GraphicsContext graphicsContext2D = canvas.getGraphicsContext2D();

        double w = canvas.getWidth() / n;
        double h = canvas.getHeight() / n;

        drawGrid(graphicsContext2D, w, h);

        canvas.setOnMouseClicked(event -> {
            double d = omega * lambda;
            int i = (int) Math.floor(n * event.getX() / canvas.getWidth());
            int j = (int) Math.floor(n * event.getY() / canvas.getHeight());
            double x = i * w;
            double y = j * h;
            double x2 = i * d - 0.5 * d * 10;
            double y2 = j * d - 0.5 * d * 10;
            System.out.println(i + ":" + j + "(" + x2 + ":" + y2 + ")");

            if (sources[i][j]) {
                graphicsContext2D.setFill(Color.WHITE);
            } else {
                graphicsContext2D.setFill(Color.BLACK);
            }

            graphicsContext2D.fillRect(x + 2, y + 2, w - 4, h - 4);
            sources[i][j] = !sources[i][j];

            drawGrid(graphicsContext2D, w, h);

            isChanged = true;
        });

       lambdaField.textProperty().addListener((observable, oldValue, newValue) -> {
           lambda = Float.parseFloat(newValue);
           isChanged = true;
       });

        kField.textProperty().addListener((observable, oldValue, newValue) -> {
            omega = Float.parseFloat(newValue);
            isChanged = true;
        });

        radiusField.textProperty().addListener((observable, oldValue, newValue) -> {
            radius = Float.parseFloat(newValue);
            isChanged = true;
        });

        MouseInput mouseInput = new MouseInput();
        radiationPatternCanvas.addEventFilter(MouseEvent.MOUSE_MOVED, event -> {
            mouseInput.setMousePos(event.getX(), event.getY());
            event.consume();
        });
        radiationPatternCanvas.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
            mouseInput.setMousePos(event.getX(), event.getY());
            event.consume();
        });
        radiationPatternCanvas.addEventFilter(MouseEvent.DRAG_DETECTED, event -> {
            mouseInput.setDragged(true);
            event.consume();
        });
        radiationPatternCanvas.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
            mouseInput.setDragged(false);
            event.consume();
        });

        Window window = new Window();
        window.mouseInput = mouseInput;

        new Thread(() -> {
            try {
                window.run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();

        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
        executor.scheduleWithFixedDelay(() -> {

            if (window.buffer == null) {
                return;
            }

            ByteBuffer buffer = window.buffer.asReadOnlyBuffer();

            int[] pixels = new int[720 * 720];
            for (int y = 0; y < 720; y++) {
                for (int x = 0; x < 720; x++) {
                    int i = (x + (720 * y)) * 4;
                    int r = buffer.get(i) & 0xFF;
                    int g = buffer.get(i + 1) & 0xFF;
                    int b = buffer.get(i + 2) & 0xFF;
                    pixels[(720 - (y + 1)) * 720 + x] = (0xFF << 24) | (r << 16) | (g << 8) | b;
                }
            }

            WritableImage image = new WritableImage(720, 720);
            PixelWriter pixelWriter = image.getPixelWriter();
            pixelWriter.setPixels(0, 0, 720, 720, PixelFormat.getIntArgbInstance(), pixels, 0, 720);
            radiationPatternCanvas.getGraphicsContext2D().drawImage(image, 0, 0);
        }, 0, 16, TimeUnit.MILLISECONDS);
    }

    private void drawGrid( GraphicsContext graphicsContext2D, double w, double h) {
        graphicsContext2D.setStroke(Color.BLACK);
        for (int i = 0; i < n; i++) {
            double y = i * h;
            for (int j = 0; j < n; j++) {
                double x = j * w;
                graphicsContext2D.strokeRect(x, y, w, h);
            }
        }
    }

}
