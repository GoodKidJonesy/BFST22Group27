package bfst22.vector;

import javafx.scene.paint.Color;

public enum WayType {
    ISLAND(
        Color.rgb(255, 255, 255)
    ),
    COASTLINE(
        Color.rgb(0,0,0)
    ),
    FOREST(
        Color.rgb(0, 204, 102)
    ),
    LANDUSE(
        Color.rgb(255, 204, 153)
    ),
    LAKE(
        Color.rgb(51, 153, 255)
    ),
    CITY(
        Color.rgb(255,255,255)
    ),
    DIRTTRACK(
        Color.rgb(255, 178, 102)
    ),
    CITYWAY(
        Color.rgb(128,128,128)
    ),
    HIGHWAY(
        Color.rgb(233,132,31)
    ),
    MOTORWAY(
        Color.rgb(255, 51, 51)
    ),
    SUBWAY(
        Color.rgb(233,132,31)
    ),
    BUILDING(
        Color.rgb(192,192,192)
    ),
    UNKNOWN(
        Color.rgb(64,64,64)
    ),
    STONE(
        Color.rgb(128,128,128)
    ),
    MILITARY(
        Color.rgb(153,0,0)
    );

    private final Color color;
    WayType(Color color){
        this.color = color;
    }
    public boolean fillTrue(){
        return this == WayType.BUILDING || this == WayType.FOREST || this == WayType.ISLAND || this == WayType.LANDUSE;

    }
    public Color getColor(){
        return color;
    }
}
