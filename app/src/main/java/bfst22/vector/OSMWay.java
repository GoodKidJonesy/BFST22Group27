package bfst22.vector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OSMWay implements Serializable {
    public static final long serialVersionUID = 42;
    public long length;
    private int speedLimit;
    public String name;
    public Boolean oneWay;
    private OSMNode from,to;

    List<OSMNode> nodes;

    public OSMWay(List<OSMNode> nodes, String name, int speedLimit) {
        this.nodes = new ArrayList<>(nodes);
        this.from = nodes.get(0);
        this.to = nodes.get(nodes.size() - 1);
        this.name = name;
        this.speedLimit = speedLimit;

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
    public List<OSMNode> getNodes(){
        return nodes;
    }
}
