package bfst22.vector;

import javafx.scene.paint.Color;

public enum WayType{
    LAND(Color.rgb(187, 226, 198), 0.1f),

    FOREST(Color.rgb(0, 204, 102), 0.1f),

    LANDUSE(Color.rgb(255, 204, 153), 0.1f),

    LAKE(Color.rgb(51, 153, 255), 0.1f),

    CITY(Color.rgb(192, 192, 192), 0.1f),

    UNKNOWN(Color.rgb(64, 64, 64), .2f),

    BUILDING(Color.rgb(100, 100, 100), .2f),

    HIGHWAY(Color.rgb(233, 132, 31), 0f),

    SUBWAY(Color.rgb(233, 132, 31), 0.2f),

    CITYWAY(Color.rgb(0, 0, 0), 0.05f),

    MOTORWAY(Color.rgb(255, 51, 51), 0f),

    COASTLINE(Color.rgb(0, 0, 0), 0f),

    MILITARY(Color.TRANSPARENT, 2f),

    STONE(Color.rgb(192, 192, 192), 0.1f),

    WETLAND(Color.rgb(135, 255, 195), 0.1f);

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
