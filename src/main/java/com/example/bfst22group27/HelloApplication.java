package com.example.bfst22group27;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException, XMLStreamException {
        var parser = new Parser("data/map.osm");
        new View(parser, primaryStage);
    }

    public static void main(String[] args) {
        launch();
    }
}