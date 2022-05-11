package bfst22.vector;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

import java.awt.*;

public class Line extends Drawable {
    Point2D from, to;

    Line(String line) {
        var parts = line.split(" ");
        var x1 = Double.parseDouble(parts[1]);
        var y1 = Double.parseDouble(parts[2]);
        var x2 = Double.parseDouble(parts[3]);
        var y2 = Double.parseDouble(parts[4]);
        from = new Point2D(x1, y1);
        to = new Point2D(x2, y2);
    }

    public Line(Point2D from, Point2D to) {
        this.from = from;
        this.to = to;
    }

    public void trace(GraphicsContext gc) {
        gc.beginPath();
        gc.moveTo(from.getX(), from.getY());
        gc.lineTo(to.getX(), to.getY());
        gc.stroke();
    }

}