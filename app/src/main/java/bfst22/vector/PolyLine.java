package bfst22.vector;

import java.io.Serializable;
import java.util.List;

import javafx.scene.canvas.GraphicsContext;

public class PolyLine extends Drawable implements Serializable {
    public static final long serialVersionUID = 134123;
    public float[] coords;
    public PolyLine left, right, parent;
    public WayType type;
    public int size = 0;

    public PolyLine(List<OSMNode> nodes, WayType type) {
        coords = new float[nodes.size() * 2];
        int i = 0;
        for (var node : nodes) {
            coords[i++] = node.lat;
            coords[i++] = node.lon;
        }
        this.type = type;
        this.size = coords.length / 2;
    }

    @Override
    public void trace(GraphicsContext gc) {
        gc.moveTo(coords[0], coords[1]);
        for (var i = 2; i < coords.length; i += 2) {
            gc.lineTo(coords[i], coords[i + 1]);
        }
    }

    @Override
    public float getAvgX() {
        float tempX = 0;

        for (int i = 0; i < coords.length; i++) {
            if (i % 2 == 0) {
                tempX += coords[i];
            }
        }
        float avgX = tempX / (coords.length / 2);
        return avgX;
    }

    @Override
    public float getAvgY() {
        float tempY = 0;

        for (int i = 0; i < coords.length; i++) {
            if (i % 2 == 1) {
                tempY += coords[i];
            }
        }
        float avgY = tempY / (coords.length / 2);
        return avgY;
    }

    @Override
    public float getMinX() {
        float minX = 0;

        for (int i = 0; i < coords.length; i++) {
            if (i % 2 == 0) {
                if (coords[i] < minX)
                    minX = coords[i];
            }
        }
        return minX;
    }

    @Override
    public float getMinY() {
        float minY = 0;

        for (int i = 0; i < coords.length; i++) {
            if (i % 2 == 1) {
                if (coords[i] < minY)
                    minY = coords[i];
            }
        }
        return minY;
    }

    @Override
    public float getMaxX() {
        float maxX = 0;

        for (int i = 0; i < coords.length; i++) {
            if (i % 2 == 0) {
                if (coords[i] < maxX)
                    maxX = coords[i];
            }
        }
        return maxX;
    }

    @Override
    public float getMaxY() {
        float maxY = 0;

        for (int i = 0; i < coords.length; i++) {
            if (i % 2 == 0) {
                if (coords[i] < maxY)
                    maxY = coords[i];
            }
        }
        return maxY;
    }

    @Override
    public WayType getType() {
        return type;
    }

    @Override
    public void setType(WayType type){
        this.type = type;
    }

    @Override
    public float coord(int depth){
        return (depth % 2 == 0) ? getAvgX() : getAvgY();
    }
}