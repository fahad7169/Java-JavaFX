package org.example.mp3player;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class Main extends Application {




    @Override
    public void start(Stage stage) throws Exception {




                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/MP3PLAYER.fxml"));
                    Parent root = loader.load();
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.show();

    }














//        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());


    public static void main(String[] args){
        launch(args);
    }

    }














