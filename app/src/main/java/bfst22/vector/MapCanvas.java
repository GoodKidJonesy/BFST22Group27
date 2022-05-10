package bfst22.vector;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.lang.reflect.Array;
import java.nio.file.WatchKey;
import java.util.ArrayList;
import java.util.Collections;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;

public class MapCanvas extends Canvas {
    Model model;
    Affine trans = new Affine();
    double zoomedIn = 100;

    Dijkstra path;


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
        drawRoute(615787, 615782, model.getGraf());

        gc.setLineWidth(1 / Math.sqrt(trans.determinant()));









       for (var line : model.iterable(WayType.COASTLINE)) {
            gc.setStroke(Color.BLACK);
            line.draw(gc);
        }


        for (var line : model.iterable(WayType.MOTORWAY)) {
            gc.setStroke(Color.RED);
            line.draw(gc);
        }


        for (var line : model.iterable(WayType.HIGHWWAY)) {

                gc.setStroke(Color.BLACK);
                line.draw(gc);



        }
        if (zoomedIn > 100) {
            for (var line : model.iterable(WayType.BUILDING)) {
                gc.setStroke(Color.GREY);
                line.draw(gc);
                gc.setFill(Color.LIGHTGREY);
                line.fill(gc);

            }
        }
        if (zoomedIn > 250) {
            for (var line : model.iterable(WayType.CITYWAY)) {


            }
        }
        if (zoomedIn > 500) {

            for (var line : model.iterable(WayType.UNKNOWN)) {
                line.draw(gc);
                gc.setStroke(Color.BLACK);
            }
        }


        gc.setLineWidth(1 / Math.sqrt(trans.determinant()));








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






    void drawEdge(Edge e, GraphicsContext gc){
        Point2D from = new Point2D(e.getFromC()[0], e.getFromC()[1]);
        Point2D to = new Point2D(e.getToC()[0], e.getToC()[1]);
        Line l = new Line(from, to);
        l.draw(gc);

    }

    void drawRoute(int v, int w, EdgeWeightedDigraph G){
        Dijkstra path = new Dijkstra(G, v);
        float distance = 0;
        var gc = getGraphicsContext2D();
        gc.setStroke(Color.GOLD);
        gc.setLineWidth(0.0005);
        for (Edge e : path.pathTo(w)){
            distance += e.getDistance();
            drawEdge(e, gc);

        }
        System.out.println("Afstand: " + distance + " m?");
    }


}