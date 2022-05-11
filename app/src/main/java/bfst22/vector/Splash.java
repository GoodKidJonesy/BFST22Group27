package bfst22.vector;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Splash {


    public Splash(Stage primaryStage) throws IOException {

        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.show();
        var loader = new FXMLLoader(View.class.getResource("Splash.fxml"));
        primaryStage.getIcons().add(new Image(View.class.getResourceAsStream("images/icon.png")));
        primaryStage.setScene(loader.load());
        primaryStage.setTitle("MapIT");
    }
}
