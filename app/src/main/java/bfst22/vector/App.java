package bfst22.vector;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.awt.*;


public class App extends Application {
    private final long[] frameTimes = new long[100];
    private int frameTimeIndex = 0;
    private boolean arrayFilled = false;


    @Override
    public void start(Stage primaryStage) throws Exception {
        // var filename = getParameters().getRaw().get(0);

        var model = new Model("data/map2.osm");

        new View(model, primaryStage);
    }
}
