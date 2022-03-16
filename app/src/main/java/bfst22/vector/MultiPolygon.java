package bfst22.vector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.canvas.GraphicsContext;

public class MultiPolygon implements Drawable, Serializable {
    public static final long serialVersionUID = 1325234;
    List<Drawable> parts = new ArrayList<>();

    public MultiPolygon(ArrayList<OSMWay> rel) {
        for (var way : rel) {
            parts.add(new PolyLine(way.nodes));
        }
    }

    public void trace(GraphicsContext gc) {
        for (var part : parts) part.trace(gc);
    }
}
