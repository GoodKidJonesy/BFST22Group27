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
        gc.setFill(Color.LIGHTBLUE);
        gc.fillRect(0, 0, getWidth(), getHeight());
        gc.setTransform(trans);

        /*
         * gc.setFill(Color.RED);
         * gc.fillRect(screen.getLeft(), screen.getTop(), screen.getRight() -
         * screen.getLeft(),
         * screen.getBottom() - screen.getTop());
         */
/*
        for (var line : model.iterable(WayType.COASTLINE)) {
            gc.setStroke(Color.BLACK);
            line.draw(gc);
        }
        for (var line : model.iterable(WayType.LANDUSE)) {
            gc.setFill(Color.BEIGE);
            line.fill(gc);
        }
        for (var line : model.iterable(WayType.FOREST)) {
            gc.setFill(Color.LIGHTGREEN);
            line.fill(gc);
        }
        for (var line : model.iterable(WayType.CITY)) {
            gc.setFill(Color.WHITE);
            line.fill(gc);
        }
        for (var line : model.iterable(WayType.STONE)) {
            gc.setFill(Color.LIGHTGREY);
            line.fill(gc);
        }

        for (var line : model.iterable(WayType.MOTORWAY)) {
            gc.setStroke(Color.RED);
            line.draw(gc);
        }
        for (var line : model.iterable(WayType.HIGHWAY)) {
            gc.setStroke(Color.ORANGE);
            line.draw(gc);
        }
        if (zoomedIn > 150) {
            for (var line : model.iterable(WayType.BUILDING)) {
                if (zoomedIn > 200) {
                    gc.setStroke(Color.GREY);
                    line.draw(gc);
                }
                gc.setFill(Color.LIGHTGREY);
                line.fill(gc);
            }
        }
        */
        for (WayType e : WayType.values()) {
            if(e.equals(WayType.UNKNOWN) || e.equals(WayType.LAKE)){
                break;
            }
            else{
            for(var line : model.iterable(e)){
                if(e.fillTrue()){
                    gc.setFill(e.getColor());
                    line.fill(gc);
                }else{
                    gc.setStroke(e.getColor());
                    line.draw(gc);
                }
            }
        }
        }
        /*
        if (zoomedIn > 170) {
            for (var line : model.iterable(WayType.CITYWAY)) {
                gc.setStroke(Color.BLACK);
                line.draw(gc);
            }
            for (var line : model.iterable(WayType.DIRTTRACK)) {
                gc.setStroke(Color.BURLYWOOD);
                line.draw(gc);
            }
        }
        */
        gc.setLineWidth(1 / Math.sqrt(trans.determinant()));
        /*
         * if (zoomedIn > 150) {
         * for (var line : model.iterable(WayType.UNKNOWN)) {
         * gc.setStroke(Color.BLACK);
         * line.draw(gc);
         * }
         * }
         */
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