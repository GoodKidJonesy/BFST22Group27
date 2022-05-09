package bfst22.vector;

<<<<<<< HEAD
import java.util.ArrayList;

=======
>>>>>>> prototype-luczito
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
<<<<<<< HEAD
        gc.setFill(WayType.LAKE.getColor());
        gc.fillRect(0, 0, getWidth(), getHeight());
        gc.setTransform(trans);

        gc.setLineWidth(1 / Math.sqrt(trans.determinant()));

        for (Drawable d : query()) {
            if(d.getType().getRequiredZoom() <= zoomedIn){
                if (d.getType().fillTrue()) {
                    gc.setFill(d.getType().getColor());
                    d.fill(gc);
                } else {
                    gc.setStroke(d.getType().getColor());
                    d.draw(gc);
                }
            }
        }
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
=======
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
>>>>>>> prototype-luczito
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
}