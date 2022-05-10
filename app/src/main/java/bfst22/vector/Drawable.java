package bfst22.vector;

import javafx.scene.canvas.GraphicsContext;

public abstract class Drawable {
    public Drawable left = null;
    public Drawable right = null;
    public Drawable parent = null;
    private WayType type = null;

    public void draw(GraphicsContext gc) {
        gc.beginPath();
        trace(gc);
        gc.stroke();
    }

    public void fill(GraphicsContext gc) {
        gc.beginPath();
        trace(gc);
        gc.fill();
    }

    void trace(GraphicsContext gc) {

    }

    public float getAvgX() {
        return 0;
    }

    public float getAvgY() {
        return 0;
    }

    public float getMinX() {
        return 0;
    }

    public float getMinY() {
        return 0;
    }

    public float getMaxX() {
        return 0;
    }

    public float getMaxY() {
        return 0;
    }

    public WayType getType() {
        return type;
    }
}
