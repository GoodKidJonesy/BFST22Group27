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
    double maxZoom = 1.06;
    double minZoom = -0.06;
    double zoomedIn = 0;
    Range range = new Range(new Point2D(0,0), new Point2D(0,0));

    void init(Model model) {
        this.model = model;
        pan(-model.minlon, -model.minlat);
        zoom(1280 / (model.maxlon - model.minlon), 0, 0);
        moveRange();
        model.addObserver(this::repaint);
        repaint();
    }

    void repaint() {

        var gc = getGraphicsContext2D();
        gc.setTransform(new Affine());
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, getWidth(), getHeight());
        gc.setTransform(trans);

        if(range.getDebug()) {
            for (OSMNode n : query()) {
                double size = 0.0001;
                gc.setFill(Color.RED);
                gc.fillOval(n.getX() - (size / 2), n.getY() - (size / 2), size, size);
            }
        }
        

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
        for (var line : model.iterable(WayType.FOREST)) {
            gc.setFill(Color.LIGHTGREEN);
            line.fill(gc);
        }
        for (var line : model.iterable(WayType.LANDUSE)) {
            gc.setFill(Color.BEIGE);
            line.fill(gc);
        }

        if (zoomedIn > 0.45) {
            for (var line : model.iterable(WayType.CITYWAY)) {
                gc.setStroke(Color.BLACK);
                line.draw(gc);

            }
        }

        if (zoomedIn > 0.50) {
            for (var line : model.iterable(WayType.BUILDING)) {
                gc.setStroke(Color.GREY);
                line.draw(gc);
                gc.setFill(Color.LIGHTGREY);
                line.fill(gc);
            }
        }

        gc.setLineWidth(1 / Math.sqrt(trans.determinant()));
        if (zoomedIn > 0.55) {
            for (var line : model.iterable(WayType.UNKNOWN)) {
                line.draw(gc);
                gc.setStroke(Color.BLACK);
            }
        }

        drawRange();
    }

    private void moveRange() {
        var gc = getGraphicsContext2D();
        Point2D topLeft = mouseToModel(new Point2D(0, 0));
        Point2D bottomRight = mouseToModel(new Point2D(gc.getCanvas().getWidth(), gc.getCanvas().getHeight()));
        range.update(topLeft, bottomRight);
    }

    private ArrayList<OSMNode> query() {
        return model.OSMNodeTree.query(model.OSMNodeTree.getRoot(), range, 0);
    }

    void pan(double dx, double dy) {
        trans.prependTranslation(dx, dy);
        moveRange();
        repaint();
    }

    void zoom(double factor, double x, double y) {
        trans.prependTranslation(-x, -y);
        trans.prependScale(factor, factor);
        trans.prependTranslation(x, y);
        moveRange();
        repaint();
    }

    void getZoom(double factor) {
        if (factor > 0) {
            factor = 0.05;
        } else if (factor < 0) {
            factor = -0.05;
        }
        zoomedIn += factor;
    }

    public Point2D mouseToModel(Point2D point) {
        try {
            return trans.inverseTransform(point);
        } catch (NonInvertibleTransformException e) {
            throw new RuntimeException(e);
        }
    }

    private void drawRange() {
        var gc = getGraphicsContext2D();
        gc.setStroke(Color.BLACK);
        gc.beginPath();
        gc.moveTo(range.getLeft(), range.getTop());
        gc.lineTo(range.getRight(), range.getTop());
        gc.lineTo(range.getRight(), range.getBottom());
        gc.lineTo(range.getLeft(), range.getBottom());
        gc.lineTo(range.getLeft(), range.getTop());
        gc.stroke();
    }

    public void setRangeDebug(boolean debug) {
        range.updateDebug(debug);
    }
}