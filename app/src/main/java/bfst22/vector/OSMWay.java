package bfst22.vector;

import java.io.Serializable;
//import java.lang.invoke.ClassSpecializer.SpeciesData;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SpinnerModel;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

public class OSMWay implements Serializable {
    public static final long serialVersionUID = 42;
    private long length;
    private int speedLimit;
    private boolean oneWay;
    private OSMNode from,to;
    private Point2D averagePos;

    List<OSMNode> nodes;

    public OSMWay(List<OSMNode> nodes) {
        this.nodes = new ArrayList<>(nodes);
        this.from = nodes.get(0);
        this.to = nodes.get(nodes.size() - 1);
        this.averagePos = new Point2D(Math.abs(from.getX() - to.getX()) / 2, Math.abs(from.getY() - to.getY()) / 2);
    }

    public long getLength(){
        return length;
    }
    public int getSpeedLimit(){
        return speedLimit;
    }
    public boolean getOneWay(){
        return oneWay;
    }
    public OSMNode getFrom(){
        return from;
    }
    public OSMNode getTo(){
        return to;
    }
}
