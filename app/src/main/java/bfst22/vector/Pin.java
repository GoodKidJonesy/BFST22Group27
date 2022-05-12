package bfst22.vector;

import javafx.geometry.Point2D;

public class Pin {
    private float x, y;
    private float size = 0.001f;

    public Pin(Point2D target) {
        this.x = (float) target.getX();
        this.y = (float) target.getY();
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getSize() {
        return size;
    }
}
