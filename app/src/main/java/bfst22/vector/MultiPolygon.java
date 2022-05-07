package bfst22.vector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.canvas.GraphicsContext;

public class MultiPolygon extends Drawable implements Serializable {
    public static final long serialVersionUID = 1325234;
    List<Drawable> parts = new ArrayList<>();

    public MultiPolygon(ArrayList<OSMWay> rel) {
        for (var way : rel) {
            parts.add(new PolyLine(way.nodes));
        }
    }

    public void trace(GraphicsContext gc) {
        for (var part : parts)
            part.trace(gc);
    }

    public float getAvgX(){
        float tempX = 0;

        for (int i = 0; i < parts.size(); i++){
            PolyLine l = (PolyLine) parts.get(i);
            tempX += l.getAvgX();
        }

        float avgX = tempX / parts.size();

        return avgX;
    }
    
    public float getAvgY(){
        float tempY = 0;

        for (int i = 0; i < parts.size(); i++){
            PolyLine l = (PolyLine) parts.get(i);
            tempY += l.getAvgY();
        }

        float avgY = tempY / parts.size();

        return avgY;
    }
}
