package bfst22.vector;

import javafx.application.Application;
import javafx.stage.Stage;


public class App extends Application {
    
    private final String defaultMap = "data/map1.osm";


    @Override
    public void start(Stage primaryStage) throws Exception {
        // var filename = getParameters().getRaw().get(0);

        var model = new Model(defaultMap);

        new View(model, primaryStage);


        }
    }

