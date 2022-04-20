package bfst22.vector;

import java.nio.file.WatchKey;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;

public class MapCanvas extends Canvas {
    Model model;
    Affine trans = new Affine();
    double zoomedIn = 100;
    Screen screen = new Screen(0, 0, 0, 0);

    void init(Model model) {
        this.model = model;
        pan(-model.minlon, -model.minlat);
        zoom(640 / (model.maxlon - model.minlon), 0, 0);
        model.addObserver(this::repaint);
        repaint();
        getScreenCords();
        model.OSMNodeTree.query(model.OSMNodeTree.getRoot(), screen, 0);
    }

    private void getScreenCords() {
        double x1 = trans.getTx() / Math.sqrt(trans.determinant());
        double y1 = (-trans.getTy()) / Math.sqrt(trans.determinant());
        double x2 = getWidth() - x1;
        double y2 = getHeight() - y1;

        /*
         * x1 -= 0.1D;
         * y1 -= 0.1D;
         * x2 += 0.1D;
         * y2 += 0.1D;
         */

        screen.update(x1, y1, x2, y2);
    }

    void repaint() {
        var gc = getGraphicsContext2D();
        gc.setTransform(new Affine());
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, getWidth(), getHeight());
        gc.setTransform(trans);

        gc.setFill(Color.RED);
        gc.fillRect(screen.getLeft(), screen.getTop(), screen.getRight() - screen.getLeft(), screen.getBottom() - screen.getTop());

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

    void pan(double dx, double dy) {
        trans.prependTranslation(dx, dy);
        repaint();
    }

    void zoom(double factor, double x, double y) {
        trans.prependTranslation(-x, -y);
        trans.prependScale(factor, factor);
        trans.prependTranslation(x, y);

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