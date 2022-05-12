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
    private int origin, dest;
    private boolean streetDebug = false;
    private Point2D currentAddress;
    private boolean darkTheme = false;

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
        gc.setFill(calcColor(WayType.LAKE));
        gc.fillRect(0, 0, getWidth(), getHeight());
        gc.setTransform(trans);

        for (Drawable d : model.iterable(WayType.LAND)) {
            gc.setLineWidth(calcWidth(d.getType().getWidth()));
            if (d.getType().fillTrue()) {
                gc.setFill(calcColor(d.getType()));
                d.fill(gc);
            }
        }
        for (Drawable d : model.iterable(WayType.CITY)) {
            gc.setLineWidth(calcWidth(d.getType().getWidth()));
            if (d.getType().fillTrue()) {
                gc.setFill(calcColor(d.getType()));
                d.fill(gc);
            }
        }

        List<Drawable> queryResult = query();
        Collections.sort(queryResult);

        for (Drawable d : queryResult) {
            if (d.getType() != WayType.LAND && d.getType() != WayType.UNKNOWN && d.getType() != WayType.CITY) {
                if (d.getType().getRequiredZoom() <= zoomedIn) {
                    gc.setLineWidth(calcWidth(d.getType().getWidth()));
                    if (d.getType().fillTrue()) {
                        gc.setFill(calcColor(d.getType()));
                        d.fill(gc);
                    } else {
                        gc.setStroke(calcColor(d.getType()));
                        d.draw(gc);
                    }
                }
            }
        }

        for (Drawable d : model.getRoadTree().query(model.getRoadTree().getRoot(), buffer, 0)) {
            if (d.getType().getRequiredZoom() <= zoomedIn) {
                gc.setLineWidth(calcWidth(d.getType().getWidth()));
                if (d.getType().fillTrue()) {
                    gc.setFill(calcColor(d.getType()));
                    d.fill(gc);
                } else {
                    gc.setStroke(calcColor(d.getType()));
                    d.draw(gc);
                }
            }
        }

        if (drawable != null) {
            gc.setLineWidth(calcWidth(drawable.getType().getWidth()));
            gc.setStroke(calcColor(drawable.getType()));
            drawable.draw(gc);
        }

        PolyLine n = (PolyLine) model.getRoadTree().getNearestNeighbor(mousePos);

        if (streetDebug) {
            gc.setLineWidth(calcWidth(n.getType().getWidth()));
            gc.setStroke(Color.RED);
            n.draw(gc);
        }

        if (range.getDebug()) {
            gc.setLineWidth(5 / Math.sqrt(trans.determinant()));
            drawRange(range, Color.BLACK);
            drawRange(buffer, Color.RED);
        }
    }

    private Color calcColor(WayType w) {
        if (darkTheme){
            return w.getSecondColor();
        } else {
            return w.getMainColor();
        }
    }

    public void setDarkTheme(boolean darkTheme){
        this.darkTheme = darkTheme;
    }

    private double calcWidth(float f){
        return f / Math.sqrt(trans.determinant());
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

    public void getStreetDebug(boolean streetDebug) {
        this.streetDebug = streetDebug;
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

    public void drawRoute(int v, int w, EdgeWeightedDigraph G) {
        Dijkstra path = new Dijkstra(G, v, w);
        drawable = path.drawablePath(w);
    }

    public Point2D getMousePos() {
        return mousePos;
    }

    public void setOrigin(int id2) {
        dest = id2;
    }

    public void setDest(int id2) {
        origin = id2;
    }

    public int getOrigin() {
        return origin;
    }

    public int getDest() {
        return dest;
    }

    public void setRoute(Point2D origin, Point2D dest) {
        this.origin = ((PolyLine) model.getRoadTree().getNearestNeighbor(origin)).getFrom().getID2();
        this.dest = ((PolyLine) model.getRoadTree().getNearestNeighbor(dest)).getFrom().getID2();
        repaint();
    }

    public void setCurrentAddress(Point2D currentAddress) {
        this.currentAddress = currentAddress;
    }

    public String getClosestStreet() {
        PolyLine n = (PolyLine) model.getRoadTree().getNearestNeighbor(mousePos);
        return n.getName();
    }
}