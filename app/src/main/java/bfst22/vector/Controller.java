package bfst22.vector;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

public class Controller {
    private Point2D lastMouse;

    Framerate FPS = new Framerate();
    @FXML
    private Label FPSLabel;



    @FXML
    private MapCanvas canvas;

    public void init(Model model) {
        canvas.init(model);

    }

    @FXML
    private void onScroll(ScrollEvent e) {
        FPSLabel.setText(FPS.getFrameRate());
        var factor = e.getDeltaY();
        canvas.getZoom(factor);
        canvas.zoom(Math.pow(1.05, factor), e.getX(), e.getY());

    }

    @FXML
    private void onMouseDragged(MouseEvent e) {
        FPSLabel.setText(FPS.getFrameRate());
        var dx = e.getX() - lastMouse.getX();
        var dy = e.getY() - lastMouse.getY();
        canvas.pan(dx, dy);
        lastMouse = new Point2D(e.getX(), e.getY());


    }



    @FXML
    private void onMousePressed(MouseEvent e) {
        lastMouse = new Point2D(e.getX(), e.getY());
        FPSLabel.setText(FPS.getFrameRate());
    }
}
