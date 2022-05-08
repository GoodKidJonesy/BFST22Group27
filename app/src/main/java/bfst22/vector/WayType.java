package bfst22.vector;

import javafx.scene.paint.Color;

public enum WayType {
    FOREST(Color.rgb(0, 204, 102)),

    LANDUSE(Color.rgb(255, 204, 153)),

    LAKE(Color.rgb(51, 153, 255)),

    UNKNOWN(Color.rgb(64, 64, 64)),

    BUILDING(Color.rgb(192, 192, 192)),

    HIGHWAY(Color.rgb(233, 132, 31)),

    SUBWAY(Color.rgb(233, 132, 31)),

    CITYWAY(Color.rgb(128, 128, 128)),

    MOTORWAY(Color.rgb(255, 51, 51)),

    COASTLINE(Color.rgb(0, 0, 0));

    private final Color color;

    WayType(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public boolean fillTrue() {
        return this == WayType.LAKE || this == WayType.FOREST || this == WayType.LANDUSE || this == WayType.BUILDING;
    }
}