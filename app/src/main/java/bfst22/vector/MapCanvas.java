package bfst22.vector;

import java.awt.geom.Ellipse2D;
import java.lang.reflect.Array;
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
    int i = 0;
    ArrayList<Vertex> vertexList = new ArrayList<>();
    ArrayList<Edge> edgeList = new ArrayList<>();
    int graphsize = 0;

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


    void createGraph(){
        /**
         * constructs Edges from ArrayList highways, and tracks how many vertices there are
         */
        for (OSMWay o : model.highways){
            for (int j = 0; j < o.nodes.size()-1; j++) {
                Edge e = new Edge(o.nodes.get(j).getID(), o.nodes.get(j+1).getID(), o.name, 2.0f, 2.0f);
                edgeList.add(e);

                graphsize++;


            }
        }
        EdgeWeightedDigraph graf = new EdgeWeightedDigraph(graphsize);

        /**
         * Adds edges to the graf.
         */
        for (Edge e : edgeList){
            graf.addEdge(e);
        }
    }
}