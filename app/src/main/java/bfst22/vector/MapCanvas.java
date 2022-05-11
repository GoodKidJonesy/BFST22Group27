package bfst22.vector;

import java.util.ArrayList;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.lang.reflect.Array;
import java.nio.file.WatchKey;
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
    double maxZoom = 1.06;
    double minZoom = -0.06;
    float zoomedIn = 0;
    Range range = new Range(new Point2D(0, 0), new Point2D(0, 0));
    Point2D mousePos = new Point2D(0, 0);
    PolyLine drawable;



    void init(Model model) {
        this.model = model;
        pan(-model.minlon, -model.minlat);
        zoom(640 / (model.maxlon - model.minlon), 0, 0);
        moveRange();
        model.addObserver(this::repaint);
        repaint();
        drawRoute(15285, 551596, model.getGraf());

    }

    void repaint() {

        var gc = getGraphicsContext2D();
        gc.setTransform(new Affine());
        gc.setFill(WayType.LAKE.getColor());
        gc.fillRect(0, 0, getWidth(), getHeight());
        gc.setTransform(trans);

        gc.setLineWidth(1 / Math.sqrt(trans.determinant()));

        for (Drawable d : model.iterable(WayType.LAND)) {
            if (d.getType().fillTrue()) {
                gc.setFill(d.getType().getColor());
                d.fill(gc);
            }
        }

        for (Drawable d : query()) {
            if (d.getType() != WayType.LAND) {
                if (d.getType().getRequiredZoom() <= zoomedIn) {
                    if (d.getType().fillTrue()) {
                        gc.setFill(d.getType().getColor());
                        d.fill(gc);
                    } else {
                        gc.setStroke(d.getType().getColor());
                        d.draw(gc);
                    }
                }
            }
        }

        if(drawable != null){
            drawable.draw(gc);
        }

        /*
         * for (Drawable d : model.roadTree.query(model.roadTree.getRoot(), range, 0)) {
         * if (d.getType().fillTrue()) {
         * gc.setFill(d.getType().getColor());
         * d.fill(gc);
         * } else {
         * gc.setStroke(d.getType().getColor());
         * d.draw(gc);
         * }
         * }
         * 
         * Drawable n = model.roadTree.getNearestNeighbor(mousePos);
         * 
         * gc.setLineWidth(4 / Math.sqrt(trans.determinant()));
         * if (n.getType().fillTrue()) {
         * gc.setFill(Color.RED);
         * n.fill(gc);
         * } else {
         * gc.setStroke(Color.RED);
         * n.draw(gc);
         * }
         */


        gc.setLineWidth(1 / Math.sqrt(trans.determinant()));
        drawRange();
    }

    private ArrayList<Drawable> query() {
        return model.kdTree.query(model.kdTree.getRoot(), range, 0);
    }

    private void moveRange() {
        var gc = getGraphicsContext2D();
        Point2D topLeft = mouseToModel(new Point2D(0, 0));
        Point2D bottomRight = mouseToModel(new Point2D(gc.getCanvas().getWidth(), gc.getCanvas().getHeight()));
        range.update(topLeft, bottomRight);
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

    public void updateMousePos(Point2D m) {
        mousePos = mouseToModel(m);
    }

    void drawEdge(Edge e, GraphicsContext gc) {
        Point2D from = new Point2D(e.getFromC()[0], e.getFromC()[1]);
        Point2D to = new Point2D(e.getToC()[0], e.getToC()[1]);
        Line l = new Line(from, to);
        l.draw(gc);

    }

    void drawRoute(int v, int w, EdgeWeightedDigraph G){
        Dijkstra path = new Dijkstra(G, v, w);
        float distance = 0;
        var gc = getGraphicsContext2D();
        gc.setStroke(Color.GOLD);
        for (int i = 0; i < 615998; i++) {
            if (path.hasPath(i)){
               // System.out.println(i);
            }
        }
        drawable = path.drawablePath(w);

    }




}