package bfst22.vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;

public class MapCanvas extends Canvas {
    private Model model;
    private Affine trans = new Affine();
    private double maxZoom = 1.06;
    private double minZoom = -0.06;
    private float zoomedIn = 0;
    private Range range = new Range(new Point2D(0, 0), new Point2D(0, 0));
    private Range buffer = new Range(new Point2D(0, 0), new Point2D(0, 0));
    private Point2D mousePos = new Point2D(0, 0);

    private Dijkstra path;
    private PolyLine drawable;

    void init(Model model) {
        this.model = model;
        pan(-model.getMinlon(), -model.getMinlat());
        zoom(1280 / (model.getMaxlon() - model.getMinlon()), 0, 0);
        moveRange();
        model.addObserver(this::repaint);
        repaint();


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

        List<Drawable> queryResult = query();
        Collections.sort(queryResult);

        for (Drawable d : queryResult) {
            if (d.getType() != WayType.LAND && d.getType() != WayType.LANDUSE && d.getType() != WayType.FOREST) {
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

        for (Drawable d : model.getRoadTree().query(model.getRoadTree().getRoot(), buffer, 0)) {
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


        //System.out.println(n.getNodes());

        if (drawable != null){
            gc.setLineWidth(0.0006);
            gc.setStroke(Color.AQUAMARINE);
            drawable.draw(gc);
        }
        gc.setLineWidth(4 / Math.sqrt(trans.determinant()));


        //drawRoute(1572, dest, model.getGraf());

        gc.setLineWidth(5 / Math.sqrt(trans.determinant()));
        drawRange(range, Color.BLACK);
        drawRange(buffer, Color.RED);
    }

    public double getMaxZoom() {
        return maxZoom;
    }

    public double getMinZoom() {
        return minZoom;
    }

    public double getZoomedIn() {
        return zoomedIn;
    }

    private ArrayList<Drawable> query() {
        return model.getKdTree().query(model.getKdTree().getRoot(), buffer, 0);
    }

    private void moveRange() {
        var gc = getGraphicsContext2D();
        Point2D topLeft = new Point2D(0, 0);
        Point2D bottomRight = new Point2D(gc.getCanvas().getWidth() - 218, gc.getCanvas().getHeight());
        range.update(mouseToModel(topLeft), mouseToModel(bottomRight));

        topLeft = new Point2D(0 - (bottomRight.getX() - topLeft.getX()),
                0 - (bottomRight.getY() - topLeft.getY()));
        bottomRight = new Point2D(gc.getCanvas().getWidth() + (bottomRight.getX() - topLeft.getX()) - 218,
                gc.getCanvas().getHeight() + (bottomRight.getY() - topLeft.getY()));
        buffer.update(mouseToModel(topLeft), mouseToModel(bottomRight));
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

    private void drawRange(Range r, Color c) {
        var gc = getGraphicsContext2D();
        gc.setStroke(c);
        gc.beginPath();
        gc.moveTo(r.getLeft(), r.getTop());
        gc.lineTo(r.getRight(), r.getTop());
        gc.lineTo(r.getRight(), r.getBottom());
        gc.lineTo(r.getLeft(), r.getBottom());
        gc.lineTo(r.getLeft(), r.getTop());
        gc.stroke();
    }

    public void setRangeDebug(boolean debug) {
        range.updateDebug(debug);
        buffer.updateDebug(debug);
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

    void drawRoute(int v, int w, EdgeWeightedDigraph G) {
        Dijkstra path = new Dijkstra(G, v, w);
        drawable = path.drawablePath(w);

    }

    public Point2D getMousePos(){
        return mousePos;
    }
}