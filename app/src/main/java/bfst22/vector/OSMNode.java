package bfst22.vector;

import java.io.Serializable;

public class OSMNode implements Serializable {
    public static final long serialVersionUID = 9082413;
    long id;
    int id2;
    float lat, lon;
    OSMNode right, left;

    public OSMNode(long id, int id2, float lat, float lon) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.id2 = id2;
        this.right = null;
        this.left = null;
    }

    public float getX() {
        return lat;
    }

    public float getY() {
        return lon;
    }

    public long getID() {
        return id;
    }

    public int getID2() {
        return id2;
    }
}
