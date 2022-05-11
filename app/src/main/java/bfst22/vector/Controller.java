package bfst22.vector;

import javax.swing.Action;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import java.io.IOException;
//import observableValue
import java.util.Observable;
import java.util.List;
import java.util.ArrayList;
import javafx.animation.FadeTransition;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.ListView;

import org.controlsfx.control.SearchableComboBox;
import org.controlsfx.control.ToggleSwitch;
import org.controlsfx.control.textfield.*;
import org.controlsfx.control.Notifications;

public class Controller {
    private Point2D lastMouse;

    private Model model;

    private Address address;

    private TrieTree trie;

    Framerate FPS = new Framerate();

    @FXML
    private MapCanvas canvas;

    @FXML
    private Range range = new Range(new Point2D(0, 0), new Point2D(0, 0));

    @FXML
    private ToggleSwitch ruteSwitch;

    @FXML
    private HBox vehicleBox;

    @FXML
    private Button  carBtn, 
                    bikeBtn, 
                    walkBtn,
                    searchButton,
                    resetButton,
                    plusBtn,
                    minusBtn;

    @FXML
    private TextField   rute1,
                        rute2;

    @FXML 
    Label totalDistanceLabel,
                totalTimeLabel,
                FPSLabel,
                zoomValue;
    
    @FXML 
    ListView directionList;

    @FXML 
    ProgressBar zoomBar;

    @FXML 
    CheckBox FPSBox, KdBox;

    App app;

    @FXML
    BorderPane root;

    public void init(Model model) {
        canvas.init(model);
        this.model = model;
        String[] address = model.addresses.toString().split(",");
        TextFields.bindAutoCompletion(rute1, address);
        TextFields.bindAutoCompletion(rute2, address);
    }

    @FXML
    private void onScroll(ScrollEvent e) {
        startFPS();
        var factor = e.getDeltaY();

        if(factor > 0) {
            if(canvas.zoomedIn+0.1 < canvas.maxZoom) {
                canvas.getZoom(factor);
                canvas.zoom(Math.pow(1.01, factor), e.getX(), e.getY());
                zoomBarValue();
            }
        } else {
            if(canvas.zoomedIn-0.1 > canvas.minZoom) {
                canvas.getZoom(factor);
                canvas.zoom(Math.pow(1.01, factor), e.getX(), e.getY());
                zoomBarValue();
            }
        }
    }

    @FXML
    private void onMouseDragged(MouseEvent e) {
        startFPS();
        var dx = e.getX() - lastMouse.getX();
        var dy = e.getY() - lastMouse.getY();
        canvas.pan(dx, dy);
        lastMouse = new Point2D(e.getX(), e.getY());
    }

    @FXML
    private void onMousePressed(MouseEvent e) {
        lastMouse = new Point2D(e.getX(), e.getY());
        startFPS();
    }

    @FXML
    private void addTextFieldandLabel(MouseEvent e) {
        if(ruteSwitch.isSelected()) {
            rute1.setPromptText("Start destination");
            rute2.setVisible(true);
            rute2.setPromptText("End destination");
            vehicleBox.setVisible(true);
            totalDistanceLabel.setVisible(true);
            totalTimeLabel.setVisible(true);
            directionList.setVisible(true);
            searchButton.setText("Route");
        }
        else {
            searchButton.setText("Search");
            rute1.setPromptText("Address");
            rute2.setVisible(false);
            vehicleBox.setVisible(false);
            totalDistanceLabel.setVisible(false);
            totalTimeLabel.setVisible(false);
            directionList.setVisible(false);
            String transparent = "-fx-background-color: transparent; -fx-border-color: black";
            walkBtn.setStyle(transparent);
            bikeBtn.setStyle(transparent);
            carBtn.setStyle(transparent);
        }
    }

    @FXML private void searchPress(MouseEvent e) {
        if(!ruteSwitch.isSelected()) {
            if(rute1.getText().isEmpty()) {
                Notifications.create().title("Error").text("Please enter an address").showError();
            } else {
            trie.search(rute1.getText());
            }
        } else if(ruteSwitch.isSelected()) {
            if(rute1.getText().isEmpty() || rute2.getText().isEmpty()) {
                Notifications.create().title("Error").text("Please fill in both fields").showError();
            } else {
                getDirectionList();
            }
        }
    }
    
    @FXML
    private void highlightVehicle(MouseEvent e) {
        String transparent = "-fx-background-color: transparent; -fx-border-color: black";
        String grey = "-fx-background-color: grey; -fx-border-color: black";

        if(carBtn.isPressed()) {
            walkBtn.setStyle(transparent);
            bikeBtn.setStyle(transparent);
            carBtn.setStyle(grey);
        }
        else if(bikeBtn.isPressed()) {
            walkBtn.setStyle(transparent);
            carBtn.setStyle(transparent);
            bikeBtn.setStyle(grey);
        }
        else if(walkBtn.isPressed()) {
            bikeBtn.setStyle(transparent);
            carBtn.setStyle(transparent);
            walkBtn.setStyle(grey);
        }
    }

    private void startFPS() {
        if(FPSBox.isSelected()) {
            FPSBox.setText(FPS.getFrameRate());
        }
        else if (!FPSBox.isSelected()) {
            FPSBox.setText("FPS");
        }
    }

    @FXML
    private void KdDebugger(ActionEvent e) {
        if(KdBox.isSelected()) {
            canvas.setRangeDebug(true);
            canvas.repaint();
        }
        else if (!KdBox.isSelected()) {
            canvas.setRangeDebug(false);
            canvas.repaint();
        }
    }

    private void zoomBarValue() {
        zoomBar.setProgress(canvas.zoomedIn);
        zoomValue.setText(Math.round(canvas.zoomedIn * 100) + "%");
    }

    @FXML
    private void buttonZoomIn(ActionEvent e) {
        startFPS();

            if(canvas.zoomedIn+0.1 < canvas.maxZoom) {
                canvas.getZoom(30);
                canvas.zoom(Math.pow(1.01, 30), canvas.getWidth()/2, canvas.getHeight()/2);
                zoomBarValue();
            }
    }

    @FXML
    private void buttonZoomOut(ActionEvent e) {
        startFPS();

            if(canvas.zoomedIn-0.1 > canvas.minZoom) {
                canvas.getZoom(-30);
                canvas.zoom(Math.pow(1.01, -30), canvas.getWidth()/2, canvas.getHeight()/2);
                zoomBarValue();
            }
    }





    @FXML
    private void onMouseMoved(MouseEvent e){
        lastMouse = new Point2D(e.getX(), e.getY());
        System.out.println(lastMouse);
    }

    @FXML
    private void resetZoom(MouseEvent e) {
        //canvas.zoomedIn = 0;
        //zoomBar.setProgress(canvas.zoomedIn);
        //zoomValue.setText(0 + "%");
        //canvas.pan(-model.minlon, -model.minlat);
        //canvas.zoom(640 / (model.maxlon - model.minlon), 0, 0);
        //canvas.repaint();
        
        
        //Aner ikke hvordan jeg resetter kortet, til at resettes til samme zoom og position, som når man starter programmet.
        //TODO: FIX

    }

    private void getDirectionList() {
        //HARDCODED FOR TEST PURPSES
        //TODO: FIX THIS

        directionList.getItems().clear();
        directionList.getItems().add("1. Start point: " + rute1.getText());
        directionList.getItems().add("2. Turn left after 50 meters");
        directionList.getItems().add("3. Turn right after 100 meters");
        directionList.getItems().add("4. Go through the roundabout");
        directionList.getItems().add("5. Take first exit");
        directionList.getItems().add("6. Go straight for 100 meters");
        directionList.getItems().add("7. End point: " + rute2.getText());

        totalDistanceLabel.setText("Total distance: " + "200 meters");
        totalTimeLabel.setText("Total time: " + "20 minutes");
    }
        


}
