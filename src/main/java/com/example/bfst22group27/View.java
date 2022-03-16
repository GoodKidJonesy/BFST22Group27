package com.example.bfst22group27;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

public class View {
    public View(Parser parser, Stage primaryStage) throws IOException {
        primaryStage.show();
        var loader = new FXMLLoader(
                View.class.getResource("View.fxml"));
        primaryStage.setScene(loader.load());
        Controller controller = loader.getController();
        controller.init(parser);
        primaryStage.setTitle("Kort");
    }
}