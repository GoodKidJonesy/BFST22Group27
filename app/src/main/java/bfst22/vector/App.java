package bfst22.vector;

import javafx.application.Application;
import javafx.stage.Stage;


public class App extends Application {
    private final long[] frameTimes = new long[100];
    private int frameTimeIndex = 0;
    private boolean arrayFilled = false;
    private final String defaultMap = "data/map.osm";


    @Override
    public void start(Stage primaryStage) throws Exception {
        // var filename = getParameters().getRaw().get(0);
<<<<<<< HEAD

        var model = new Model(defaultMap);

=======
        var model = new Model("data/map.zip");
>>>>>>> prototype-luczito
        new View(model, primaryStage);
    }
}
