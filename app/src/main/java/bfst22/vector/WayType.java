package bfst22.vector;

import javafx.scene.paint.Color;

public enum WayType {
    LAND(Color.WHITE, 0f),

    FOREST(Color.rgb(0, 204, 102), 0f),

    LANDUSE(Color.rgb(255, 204, 153), 0f),

    LAKE(Color.rgb(51, 153, 255), 0f),
    
    CITY(Color.rgb(255, 255, 255), 0.5f),

    UNKNOWN(Color.rgb(64, 64, 64), .2f),

    BUILDING(Color.rgb(192, 192, 192), .5f),

    HIGHWAY(Color.rgb(233, 132, 31), 0f),

    SUBWAY(Color.rgb(233, 132, 31), .2f),

    CITYWAY(Color.rgb(0, 0, 0), .4f),

    MOTORWAY(Color.rgb(255, 51, 51), 0f),

    COASTLINE(Color.rgb(0, 0, 0), 0f),

    MILITARY(Color.TRANSPARENT, 1000f),

    STONE(Color.rgb(192, 192, 192), 0f),

    WETLAND(Color.rgb(135, 255, 195), 0f);

    private final Color color;
    private final float requiredZoom;

    WayType(Color color, float requiredZoom) {
        this.color = color;
        this.requiredZoom = requiredZoom;
    }

    public Color getColor() {
        return color;
    }

    public boolean fillTrue() {
        return this == WayType.LAKE || this == WayType.FOREST || this == WayType.LANDUSE || this == WayType.BUILDING
                || this == WayType.CITY || this == WayType.STONE || this == WayType.WETLAND || this == WayType.LAND;
    }

    public float getRequiredZoom() {
        return requiredZoom;
    }
}
