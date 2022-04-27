package bfst22.vector;

import java.nio.file.WatchKey;
import java.util.ArrayList;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;

public class MapCanvas extends Canvas {
    Model model;
    Affine trans = new Affine();
    double zoomedIn = 100;
    Screen screen = new Screen(new Point2D(0, 0), new Point2D(0, 0));

    void init(Model model) {
        this.model = model;
        pan(-model.minlon, -model.minlat);
        zoom(640 / (model.maxlon - model.minlon), 0, 0);
        model.addObserver(this::repaint);
        repaint();
    }

    void repaint() {
        var gc = getGraphicsContext2D();
        gc.setTransform(new Affine());
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, getWidth(), getHeight());
        gc.setTransform(trans);

        gc.setFill(Color.PURPLE);
        gc.fillRect(screen.getLeft(), screen.getBottom(), screen.getRight() - screen.getLeft(), screen.getTop() - screen.getBottom());
        System.out.println(query().size());
        for (OSMNode n : query()) {
            double size = 0.001;
            gc.setFill(Color.RED);
            gc.fillOval(n.getX() - (size / 2), n.getY() - (size / 2), size, size);
        }

        System.out.println("left: " + screen.getLeft() + " top: " + screen.getTop());
        System.out.println("right: " + screen.getRight() + " bottom: " + screen.getBottom());

        for (var line : model.iterable(WayType.LAKE)) {
            gc.setFill(Color.LIGHTBLUE);
            line.fill(gc);
        }
        for (var line : model.iterable(WayType.COASTLINE)) {
            gc.setStroke(Color.BLACK);
            line.draw(gc);
        }
        for (var line : model.iterable(WayType.MOTORWAY)) {
            gc.setStroke(Color.RED);
            line.draw(gc);
        }
        for (var line : model.iterable(WayType.HIGHWWAY)) {
            gc.setStroke(Color.ORANGE);
            line.draw(gc);
        }
        if (zoomedIn > 110) {
            for (var line : model.iterable(WayType.BUILDING)) {
                gc.setStroke(Color.GREY);
                line.draw(gc);
                gc.setFill(Color.LIGHTGREY);
                line.fill(gc);
            }
        }
        if (zoomedIn > 120) {
            for (var line : model.iterable(WayType.CITYWAY)) {
                gc.setStroke(Color.BLACK);
                line.draw(gc);

            }
        }

        gc.setLineWidth(1 / Math.sqrt(trans.determinant()));
        if (zoomedIn > 150) {
            for (var line : model.iterable(WayType.UNKNOWN)) {
                line.draw(gc);
                gc.setStroke(Color.BLACK);
            }
        }
    }

    private void moveScreen() {
        var gc = getGraphicsContext2D();
        screen.update(mouseToModel(new Point2D(0, 0)),
                mouseToModel(new Point2D(gc.getCanvas().getWidth(), gc.getCanvas().getHeight())));
    }

    private ArrayList<OSMNode> query() {
        return model.OSMNodeTree.query(model.OSMNodeTree.getRoot(), screen, 0);
    }

    void pan(double dx, double dy) {
        trans.prependTranslation(dx, dy);
        moveScreen();
        repaint();
    }

    void zoom(double factor, double x, double y) {
        trans.prependTranslation(-x, -y);
        trans.prependScale(factor, factor);
        trans.prependTranslation(x, y);
        moveScreen();
        repaint();
    }

    void getZoom(double factor) {
        zoomedIn += factor;
    }

    public Point2D mouseToModel(Point2D point) {
        try {
            return trans.inverseTransform(point);
        } catch (NonInvertibleTransformException e) {
            throw new RuntimeException(e);
        }
    }
}