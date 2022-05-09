package bfst22.vector;

import javafx.scene.paint.Color;

public enum WayType {
    FOREST(Color.rgb(0, 204, 102), .50),

    LANDUSE(Color.rgb(255, 204, 153), 0),

    LAKE(Color.rgb(51, 153, 255), 0),

    UNKNOWN(Color.rgb(64, 64, 64), .2),

    BUILDING(Color.rgb(192, 192, 192), .5),

    HIGHWAY(Color.rgb(233, 132, 31), 0),

    SUBWAY(Color.rgb(233, 132, 31), .2),

    CITYWAY(Color.rgb(128, 128, 128), .4),

    MOTORWAY(Color.rgb(255, 51, 51), 0),

    COASTLINE(Color.rgb(0, 0, 0), 0);

    private final Color color;
    private final double requiredZoom;
    WayType(Color color, double requiredZoom) {
        this.color = color;
        this.requiredZoom = requiredZoom;
    }

    public Color getColor() {
        return color;
    }

    public boolean fillTrue() {
        return this == WayType.LAKE || this == WayType.FOREST || this == WayType.LANDUSE || this == WayType.BUILDING;
    }
    public boolean draw(){
        if(requiredZoom <= MapCanvas.zoomedIn){
            return true;
        }else{
            return false;
        }
    }
}