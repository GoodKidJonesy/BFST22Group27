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
    private int maxZoom = 10;
    private int minZoom = 0;
    private int zoomedIn = 0;
    private Range range = new Range(new Point2D(0, 0), new Point2D(0, 0));
    private Range buffer = new Range(new Point2D(0, 0), new Point2D(0, 0));
    private Point2D mousePos = new Point2D(0, 0);

    private Dijkstra path;
    private PolyLine drawable;

    void init(Model model) {
        this.model = model;
        pan(-model.getMinlon(), -model.getMinlat());
        zoom(640 / (model.getMaxlon() - model.getMinlon()), 0, 0);
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
        System.out.println(zoomedIn);

        for (Drawable d : model.iterable(WayType.LAND)) {
            gc.setLineWidth(d.getType().getWidth() / Math.sqrt(trans.determinant()));
            if (d.getType().fillTrue()) {
                gc.setFill(d.getType().getColor());
                d.fill(gc);
            }
        }
        for (Drawable d : model.iterable(WayType.CITY)) {
            gc.setLineWidth(d.getType().getWidth() / Math.sqrt(trans.determinant()));
            if (d.getType().fillTrue()) {
                gc.setFill(d.getType().getColor());
                d.fill(gc);
            }
        }

        List<Drawable> queryResult = query();
        Collections.sort(queryResult);

        for (Drawable d : queryResult) {
            if (d.getType() != WayType.LAND && d.getType() != WayType.UNKNOWN && d.getType() != WayType.CITY) {
                if (d.getType().getRequiredZoom() <= zoomedIn) {
                    gc.setLineWidth(d.getType().getWidth() / Math.sqrt(trans.determinant()));
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
                gc.setLineWidth(d.getType().getWidth() / Math.sqrt(trans.determinant()));
                if (d.getType().fillTrue()) {
                    gc.setFill(d.getType().getColor());
                    d.fill(gc);
                } else {
                    gc.setStroke(d.getType().getColor());
                    d.draw(gc);
                }
            }
        }

        // System.out.println(n.getNodes());

        if (drawable != null) {
            gc.setLineWidth(0.002);
            gc.setStroke(Color.AQUAMARINE);
            drawable.draw(gc);
        }

        // PolyLine n = (PolyLine) model.getRoadTree().getNearestNeighbor(mousePos);
        // System.out.println(n.getName());
        // System.out.println(n.getNodes());
        /*
         * int dest = ((PolyLine) n).getFrom().getID2();
         * 
         * gc.setLineWidth(4 / Math.sqrt(trans.determinant()));
         * if (n.getType().fillTrue()) {
         * gc.setFill(Color.RED);
         * n.fill(gc);
         * } else {
         * gc.setStroke(Color.RED);
         * n.draw(gc);
         * }
         * 
         * drawRoute(1572, dest, model.getGraf());
         */

        if (range.getDebug()) {
            gc.setLineWidth(5 / Math.sqrt(trans.determinant()));
            drawRange(range, Color.BLACK);
            drawRange(buffer, Color.RED);
        }
    }

    public double getMaxZoom() {
        return maxZoom;
    }

    public double getMinZoom() {
        return minZoom;
    }

    public int getZoomedIn() {
        return zoomedIn;
    }

    private ArrayList<Drawable> query() {
        return model.getKdTree().query(model.getKdTree().getRoot(), buffer, 0);
    }

    private void moveRange() {
        var gc = getGraphicsContext2D();
        int bufferSize = 3;

        Point2D topLeft = new Point2D(0, 0);
        Point2D bottomRight = new Point2D(gc.getCanvas().getWidth() - 218, gc.getCanvas().getHeight());
        range.update(mouseToModel(topLeft), mouseToModel(bottomRight));

        System.out.println(range.getWidth());
        Point2D topLeftBuffer = new Point2D(0 - ((bottomRight.getX() - topLeft.getX()) / bufferSize),
                0 - ((bottomRight.getY() - topLeft.getX()) / bufferSize));
        Point2D bottomRightBuffer = new Point2D(
                gc.getCanvas().getWidth() - 218 + ((bottomRight.getX() - topLeft.getX()) / bufferSize),
                gc.getCanvas().getHeight() + ((bottomRight.getY() - topLeft.getX()) / bufferSize));
        buffer.update(mouseToModel(topLeftBuffer), mouseToModel(bottomRightBuffer));
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
            factor = 1;
        } else if (factor < 0) {
            factor = -1;
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

    public Point2D getMousePos() {
        return mousePos;
    }
}