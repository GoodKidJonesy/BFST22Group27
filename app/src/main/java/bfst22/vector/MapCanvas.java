package bfst22.vector;

import java.util.ArrayList;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
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

    void init(Model model) {
        this.model = model;
        pan(-model.minlon, -model.minlat);
        zoom(640 / (model.maxlon - model.minlon), 0, 0);
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

        for (Drawable d : query()) {
            if (d.getType().fillTrue()) {
                gc.setFill(d.getType().getColor());
                d.fill(gc);
            } else {
                gc.setStroke(d.getType().getColor());
                d.draw(gc);
            }
        }

        drawRange();
    }

    private ArrayList<Drawable> query() {
        return model.drawTree.query(model.drawTree.getRoot(), range, 0);
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
}