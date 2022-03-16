package com.example.bfst22group27;

import java.io.Serializable;

public class OSMNode implements Serializable {
    public static final long serialVersionUID = 9082413;
    long id;
    float lat, lon;

    public OSMNode(float lat, float lon) {
        // this.id = id;
        this.lat = lat;
        this.lon = lon;
    }
}
