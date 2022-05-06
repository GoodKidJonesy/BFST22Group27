package bfst22.vector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class OSMWay implements Serializable {
    public static final long serialVersionUID = 42;
    public long length;
    public int speedLimit;
    public Boolean oneWay;

    List<OSMNode> nodes;

    public OSMWay(List<OSMNode> nodes) {
        this.nodes = new ArrayList<>(nodes);
    }
}
