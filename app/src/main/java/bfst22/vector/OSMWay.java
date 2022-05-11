package bfst22.vector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OSMWay implements Serializable {
    public static final long serialVersionUID = 42;
    private long length;
    private int speedLimit;
    private boolean oneWay;
    private OSMNode from,to;

    List<OSMNode> nodes;

    public OSMWay(List<OSMNode> nodes) {
        this.nodes = new ArrayList<>(nodes);
        this.from = nodes.get(0);
        this.to = nodes.get(nodes.size() - 1);
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
