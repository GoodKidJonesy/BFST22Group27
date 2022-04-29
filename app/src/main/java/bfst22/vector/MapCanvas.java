package bfst22.vector;

import java.awt.geom.Ellipse2D;
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
    int i = 0;
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
        for (var line : model.iterable(WayType.LAKE)) {
            gc.setFill(Color.LIGHTBLUE);
            line.fill(gc);
        }
        for (Vertex v : model.vertexList){
            double x1 = v.getX();
            double y1 = v.getY();
            double size = 0.0001;
//            Point2D from = new Point2D(x1, y1);
//            Point2D to = new Point2D(x1, y1);
//            Line l = new Line(from, to);
            gc.setFill(Color.RED);
            gc.fillOval(x1-(size/2), y1-(size/2), size, size);




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
        if (zoomedIn > 400) {
            for (var line : model.iterable(WayType.BUILDING)) {
                gc.setStroke(Color.GREY);
                line.draw(gc);
                gc.setFill(Color.LIGHTGREY);
                line.fill(gc);

            }
        }
        if (zoomedIn > 250) {
            for (var line : model.iterable(WayType.CITYWAY)) {
                gc.setStroke(Color.BLACK);
                line.draw(gc);

            }
        }

        gc.setLineWidth(1 / Math.sqrt(trans.determinant()));
        if (zoomedIn > 500) {
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