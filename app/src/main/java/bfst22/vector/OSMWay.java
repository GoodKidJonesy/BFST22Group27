package bfst22.vector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.canvas.GraphicsContext;

public class OSMWay implements Serializable {
    public static final long serialVersionUID = 42;
    public long length;
    private int speedLimit;
    public String name;
    public Boolean oneWay;

    List<OSMNode> nodes;

    public OSMWay(List<OSMNode> nodes, String name, int speedLimit) {
        this.nodes = new ArrayList<>(nodes);
        this.name = name;
        this.speedLimit = speedLimit;
    }

    int getSpeedLimit(){
        return speedLimit;
    }

}
