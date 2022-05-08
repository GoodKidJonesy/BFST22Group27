package bfst22.vector;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;

public class Controller {
    private Point2D lastMouse;

    Framerate FPS = new Framerate();

    @FXML
    private MapCanvas canvas;

    @FXML
    private ToggleButton ruteButton;

    @FXML
    private HBox vehicleBox;

    @FXML
    private Button  carBtn, 
                    bikeBtn, 
                    walkBtn,
                    searchButton;

    @FXML
    private TextField   rute1, 
                        rute2;

    @FXML Label totalDistanceLabel,
                totalTimeLabel,
                FPSLabel,
                zoomValue;
    
    @FXML ListView directionList;

    @FXML ProgressBar zoomBar;

    @FXML CheckBox FPSBox;


    public void init(Model model) {
        canvas.init(model);
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
    private void addTextFieldandLabel(ActionEvent e) {
        if(ruteButton.isSelected()) {
            rute1.setPromptText("Start destination");
            rute2.setVisible(true);
            rute2.setPromptText("End destination");
            vehicleBox.setVisible(true);
            totalDistanceLabel.setVisible(true);
            totalTimeLabel.setVisible(true);
            directionList.setVisible(true);
        }
        else {
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
        System.out.println(rute1.getText());
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

    private void zoomBarValue() {
        zoomBar.setProgress(canvas.zoomedIn);
        zoomValue.setText(Math.round(canvas.zoomedIn * 100) + "%");
    }

    @FXML
    private void onMouseMoved(MouseEvent e){
        lastMouse = new Point2D(e.getX(), e.getY());
        System.out.println(lastMouse);

    }
}
