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
    int i = 0;
    ArrayList<Vertex> vertexList = new ArrayList<>();
    ArrayList<Edge> edgeList = new ArrayList<>();
    int graphsize = 0;
    Dijkstra path;
    EdgeWeightedDigraph graf;

    void init(Model model) {
        this.model = model;
        pan(-model.minlon, -model.minlat);
        zoom(640 / (model.maxlon - model.minlon), 0, 0);
        model.addObserver(this::repaint);
        createGraph();
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

        drawFrom(615787, graf);










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
                double distance = distanceCalc(o.nodes.get(j).getID(), o.nodes.get(j+1).getID());
                Edge e = new Edge(o.nodes.get(j).getID(), o.nodes.get(j+1).getID(), o.nodes.get(j).getID2(), o.nodes.get(j+1).getID2(), o.name, distance/o.getSpeedLimit(), distance);
                    e.addFromC(o.nodes.get(j).lat, o.nodes.get(j).lon);
                    e.addToC(o.nodes.get(j+1).lat, o.nodes.get(j+1).lon);
                Edge f = new Edge(o.nodes.get(j+1).getID(), o.nodes.get(j).getID(), o.nodes.get(j+1).getID2(), o.nodes.get(j).getID2(), o.name, distance/o.getSpeedLimit(), distance);
                    f.addFromC(o.nodes.get(j+1).lat, o.nodes.get(j+1).lon);
                    f.addToC(o.nodes.get(j).lat, o.nodes.get(j).lon);
                edgeList.add(e);
                edgeList.add(f);





            }
        }
        graf = new EdgeWeightedDigraph(model.id2);

        /**
         * Adds edges to the graf.
         */

        for (Edge e : edgeList){
            graf.addEdge(e);

            //System.out.println(e.getWeight());
        }


        var gc = getGraphicsContext2D();
        gc.setStroke(Color.GOLD);
        /* for (int j = 0; j < graf.V(); j++) {
            if (path.hasPath(j)){
                System.out.println(j);
                }
            }*/





        System.out.println("Outdegree: " + graf.outdegree(615782) + " Indegree: " + graf.indegree(615782)
                            + "Edges: " + graf.E() + " Vertices: " + graf.V());
        System.out.println(graf.getAdjacencyMap().get(615782).get(2).name);





        }










    Double distanceCalc(long from, long to){
        double R = 6371*1000;
        double lat1 = model.id2node.get(from).lat*Math.PI/180;
        double lat2 = model.id2node.get(to).lat*Math.PI/180;
        double deltaLat = (lat2-lat1)*Math.PI/180;
        double lon1 = model.id2node.get(from).lon;
        double lon2 = model.id2node.get(to).lon;
        double deltaLon = (lon2-lon1) * Math.PI/180;

        double a = Math.sin(deltaLat/2) * Math.sin(deltaLat/2) + Math.cos(lat1) *
                   Math.cos(lat2) * Math.sin(deltaLon/2) * Math.sin(deltaLon/2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double d = R * c;

        return d;
    }

    void drawEdge(Edge e, GraphicsContext gc){
        Point2D from = new Point2D(e.getFromC()[0], e.getFromC()[1]);
        Point2D to = new Point2D(e.getToC()[0], e.getToC()[1]);
        Line l = new Line(from, to);
        l.draw(gc);

    }

    void drawFrom(int v, EdgeWeightedDigraph G){
        Dijkstra path = new Dijkstra(G, v);
        var gc = getGraphicsContext2D();
        gc.setStroke(Color.GOLD);
        for (Edge e : path.pathTo(v)){
            drawEdge(e, gc);
        }
    }


}