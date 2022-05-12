package bfst22.vector;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.Arrays;
import java.awt.*;

import org.controlsfx.control.ToggleSwitch;
import org.controlsfx.control.textfield.*;
import org.controlsfx.control.Notifications;

public class Controller {
    private Point2D lastMouse;
    private Model model;
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
    VBox sidepanel;

    @FXML
    private Button carBtn,
            bikeBtn,
            walkBtn,
            searchButton,
            resetButton,
            plusBtn,
            minusBtn,
            loadCustomBtn,
            loadDenmarkBtn;

    @FXML
    private TextField rute1,
            rute2;

    @FXML
    TextArea logger;

    @FXML
    Label totalDistanceLabel,
            totalTimeLabel,
            FPSLabel,
            zoomValue,
            addressLabel;

    @FXML
    ListView directionList;

    @FXML
    ProgressBar zoomBar;

    @FXML
    CheckBox FPSBox, KdBox, Nearest, DarkTheme;

    @FXML
    MenuItem loadCustom,
            aboutBtn;

    App app;

    @FXML
    BorderPane root;

    private boolean vehicleSelected;

    String[] address;

    private boolean rute1Found;

    private boolean rute2Found;

    public void init(Model model) {
        this.model = model;
        canvas.init(model);
        TextFields.bindAutoCompletion(rute1, model.trie.searchMultiple(rute1.getText()));
        TextFields.bindAutoCompletion(rute2, model.trie.searchMultiple(rute2.getText()));
    }

    @FXML
    private void onScroll(ScrollEvent e) throws InterruptedException {

        startFPS();
        var factor = e.getDeltaY();

        if (factor > 0) {
            if (canvas.getZoomedIn() + 1 <= canvas.getMaxZoom()) {
                canvas.zoom(Math.pow(1.01, 50), e.getX(), e.getY());
                canvas.getZoom(factor);
                zoomBarValue();
            }
        } else {
            if (canvas.getZoomedIn() - 1 >= canvas.getMinZoom()) {

                canvas.zoom(Math.pow(1.01, -50), e.getX(), e.getY());
                canvas.getZoom(factor);
                zoomBarValue();
            }
        }
        Thread.sleep(300);
        canvas.repaint();

    }

    @FXML
    private void onMouseMoved(MouseEvent e) {
        canvas.updateMousePos(new Point2D(e.getX(), e.getY()));
        String name = canvas.getClosestStreet();
        addressLabel.setText(name);
    }

    @FXML
    private void onMouseDragged(MouseEvent e) {
        startFPS();
        var dx = e.getX() - lastMouse.getX();
        var dy = e.getY() - lastMouse.getY();
        canvas.pan(dx, dy);
        lastMouse = new Point2D(e.getX(), e.getY());
        canvas.updateMousePos(lastMouse);
    }

    @FXML
    private void onMousePressed(MouseEvent e) {
        lastMouse = new Point2D(e.getX(), e.getY());
        canvas.updateMousePos(lastMouse);
        PolyLine n = (PolyLine) model.getRoadTree().getNearestNeighbor(canvas.getMousePos());
        int id2 = ((PolyLine) n).getFrom().getID2();

        if (e.isPrimaryButtonDown() && e.isControlDown()) {
            canvas.setDest(id2);
        }

        if (e.isSecondaryButtonDown() && e.isControlDown()) {
            canvas.setOrigin(id2);
        }

        // Draw route if there is an origin and destination
        if (canvas.getDest() != 0 && canvas.getOrigin() != 0) {
            canvas.drawRoute(canvas.getOrigin(), canvas.getDest(), model.getGraf());
        }

        canvas.repaint();
        startFPS();
    }

    @FXML
    private void addTextFieldandLabel(MouseEvent e) {
        if (ruteSwitch.isSelected()) {
            rute1.setPromptText("Start destination");
            rute2.setVisible(true);
            rute2.setPromptText("End destination");
            vehicleBox.setVisible(true);
            totalDistanceLabel.setVisible(true);
            totalTimeLabel.setVisible(true);
            directionList.setVisible(true);
            searchButton.setText("Route");
        } else {
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
            vehicleSelected = false;
        }
    }

    @FXML
    private void searchPress(MouseEvent e) {
        rute1Found = model.trie.search(rute1.getText());
        rute2Found = model.trie.search(rute2.getText());
        if (!ruteSwitch.isSelected()) {
            if (rute1.getText().isEmpty()) {
                Notifications.create().title("Error").text("Please enter an address").showError();
            } else if (rute1Found) {
                Point2D currentAddress = model.trie.getCords(rute1.getText());
                canvas.setCurrentAddress(currentAddress);
                Notifications.create().title("Success").text("Address found: " + rute1.getText()).showInformation();
            } else {
                Notifications.create().title("Error").text("No address found").showError();
            }
        } else if (ruteSwitch.isSelected()) {
            if (!vehicleSelected) {
                Notifications.create().title("Error").text("Please select your preferred transportation").showError();
            } else if (rute1.getText().isEmpty() || rute2.getText().isEmpty()) {
                Notifications.create().title("Error").text("Please fill in both fields").showError();
            } else if (!rute1Found && !rute2Found) {
                Notifications.create().title("Error").text("Neither start nor end address found").showError();
                directionList.getItems().clear();
            } else if (!rute1Found) {
                Notifications.create().title("Error").text("No start address found").showError();
                directionList.getItems().clear();
            } else if (!rute2Found) {
                Notifications.create().title("Error").text("No end address found").showError();
                directionList.getItems().clear();
            } else {
                Point2D origin = model.trie.getCords(rute1.getText());
                Point2D dest = model.trie.getCords(rute2.getText());

                canvas.setRoute(origin, dest);
                getDirectionList();
                canvas.drawRoute(canvas.getOrigin(), canvas.getDest(), model.getGraf());
                canvas.repaint();
            }
        }
        canvas.repaint();
    }

    @FXML
    private void highlightVehicle(MouseEvent e) {
        String transparent = "-fx-background-color: transparent; -fx-border-color: black";
        String grey = "-fx-background-color: grey; -fx-border-color: black";

        if (carBtn.isPressed()) {
            walkBtn.setStyle(transparent);
            bikeBtn.setStyle(transparent);
            carBtn.setStyle(grey);
            vehicleSelected = true;
        } else if (bikeBtn.isPressed()) {
            walkBtn.setStyle(transparent);
            carBtn.setStyle(transparent);
            bikeBtn.setStyle(grey);
            vehicleSelected = true;
        } else if (walkBtn.isPressed()) {
            bikeBtn.setStyle(transparent);
            carBtn.setStyle(transparent);
            walkBtn.setStyle(grey);
            vehicleSelected = true;
        }
    }

    private void startFPS() {
        if (FPSBox.isSelected()) {
            FPSBox.setText(FPS.getFrameRate());
        } else if (!FPSBox.isSelected()) {
            FPSBox.setText("FPS");
        }
    }

    @FXML
    private void KdDebugger(ActionEvent e) {
        if (KdBox.isSelected()) {
            canvas.setRangeDebug(true);
        } else {
            canvas.setRangeDebug(false);
        }
        canvas.repaint();
    }

    @FXML
    private void NNDebugger(ActionEvent e) {
        if (Nearest.isSelected()) {
            canvas.getStreetDebug(true);
        } else {
            canvas.getStreetDebug(false);
        }
        canvas.repaint();
    }

    @FXML
    private void DarkTheme(ActionEvent e) {
        if (DarkTheme.isSelected()) {
            sidepanel.setStyle("-fx-background-color: grey");
            canvas.setDarkTheme(true);
        } else {
            sidepanel.setStyle("-fx-background-color: transparent");
            canvas.setDarkTheme(false);
        }
        canvas.repaint();
    }

    private void zoomBarValue() {
        double temp = (double) canvas.getZoomedIn() / 10;
        zoomBar.setProgress(temp);
        zoomValue.setText((canvas.getZoomedIn() * 10) + "%");
    }

    @FXML
    private void buttonZoomIn(ActionEvent e) {
        startFPS();

        if (canvas.getZoomedIn() + 1 <= canvas.getMaxZoom()) {
            canvas.getZoom(50);
            canvas.zoom(Math.pow(1.01, 50), canvas.getWidth() / 2, canvas.getHeight() / 2);
            zoomBarValue();
        }
    }

    @FXML
    private void buttonZoomOut(ActionEvent e) {
        startFPS();

        if (canvas.getZoomedIn() - 1 >= canvas.getMinZoom()) {
            canvas.getZoom(-50);
            canvas.zoom(Math.pow(1.01, -50), canvas.getWidth() / 2, canvas.getHeight() / 2);
            zoomBarValue();
        }
    }

    private void getDirectionList() {
        // HARDCODED FOR TEST PURPSES
        // TODO: FIX THIS

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

    @FXML
    private void loadDenmark(ActionEvent e) throws ClassNotFoundException, IOException, XMLStreamException,
            FactoryConfigurationError, InterruptedException {

        Stage splash = (Stage) loadDenmarkBtn.getScene().getWindow();
        // var loader = new FXMLLoader(View.class.getResource("Splash.fxml"));
        // splash.setScene(loader.load());
        // splash.show();

        // Thread thread = new Thread(() -> {
        // try {
        var newModel = new Model(App.defaultMap);
        splash.close();
        Stage stage = new Stage();
        new View(newModel, stage);

        // } catch (ClassNotFoundException | IOException | XMLStreamException |
        // FactoryConfigurationError ex) {
        // System.out.println(ex.getMessage());
        // }
        // });
        // thread.start();

    }

    @FXML
    private void loadCustom(ActionEvent e)
            throws ClassNotFoundException, IOException, XMLStreamException, FactoryConfigurationError {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        fileChooser.setInitialDirectory(new File("./data/"));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("OSM Files", "*.osm"));
        File selectedFile = fileChooser.showOpenDialog(null);
        String filePath = selectedFile.getAbsolutePath();

        Stage splash = (Stage) loadCustomBtn.getScene().getWindow();
        // var loader = new FXMLLoader(View.class.getResource("Splash.fxml"));
        // splash.setScene(loader.load());
        // splash.show();

        // Thread thread = new Thread(() -> {
        // try {
        var newModel = new Model(filePath);
        splash.close();
        Stage stage = new Stage();
        new View(newModel, stage);
        // } catch (ClassNotFoundException | IOException | XMLStreamException |
        // FactoryConfigurationError ex) {
        // System.out.println(ex.getMessage());
        // }
        // });
        // thread.start();
        // }
    }

    @FXML
    private void loadCustom2(ActionEvent e)
            throws ClassNotFoundException, IOException, XMLStreamException, FactoryConfigurationError {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        fileChooser.setInitialDirectory(new File("./data/"));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("OSM Files", "*.osm"));
        File selectedFile = fileChooser.showOpenDialog(null);
        String filePath = selectedFile.getAbsolutePath();

        Stage currentStage = (Stage) searchButton.getScene().getWindow();
        // var loader = new FXMLLoader(View.class.getResource("Splash.fxml"));
        // currentStage.setScene(loader.load());
        // currentStage.show();

        // Thread thread = new Thread(() -> {
        // try {
        var newModel = new Model(filePath);
        currentStage.close();
        Stage stage = new Stage();
        new View(newModel, stage);
        // } catch (ClassNotFoundException | IOException | XMLStreamException |
        // FactoryConfigurationError ex) {
        // System.out.println(ex.getMessage());
        // }
        // });
        // thread.start();
        // }
    }

    @FXML
    private void about() throws IOException {
        // load about window

        Stage stage = new Stage();
        stage.setTitle("About");
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);

        var about = new FXMLLoader(View.class.getResource("About.fxml"));
        stage.setScene(about.load());
        stage.show();
    }

    @FXML
    private void link(ActionEvent e) throws IOException, URISyntaxException {
        Desktop.getDesktop().browse(new URI("https://github.itu.dk/trbj/BFST22Group27"));
    }
}