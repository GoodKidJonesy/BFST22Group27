package bfst22.vector;

import javafx.scene.canvas.GraphicsContext;

public abstract class Drawable {
    public Drawable left = null;
    public Drawable right = null;

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
    void trace(GraphicsContext gc){

    }

    public float getAvgX(){
        return 0;
    }
    public float getAvgY(){
        return 0;
    }
}
