package bfst22.vector;

import java.io.Serializable;

public class OSMNode implements Serializable {
    public static final long serialVersionUID = 9082413;
    long id;
    float lat, lon;
    OSMNode right,left,parent;

    public OSMNode(long id, float lat, float lon) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.right = null;
        this.left = null;
        this.parent = null;
    }

    public float getX() {
        return lat;
    }

    public float getY() {
        return lon;
    }

    public long getId(){
        return id;
    }
}
