package me.petrolingus.rpc;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import me.petrolingus.rpc.lwjgl.Window;

public class Main extends Application {

    public static void main(String[] args) {
        new Thread(() -> {
            try {
                new Window().run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        primaryStage.setTitle("Spherical Wave Interference");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}