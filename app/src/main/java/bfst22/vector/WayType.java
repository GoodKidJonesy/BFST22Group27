package bfst22.vector;

import javafx.scene.paint.Color;

public enum WayType {
    LAND(Color.rgb(187, 226, 198), 0, true, 1f),

    FOREST(Color.rgb(0, 204, 102), 1, true, 1f),

    LAKE(Color.rgb(51, 153, 255), 0, true, 1f),

    CITY(Color.rgb(192, 192, 192), 0, true, 1f),

    UNKNOWN(Color.rgb(255, 180, 180), 0, false, 1f),

    BUILDING(Color.rgb(100, 100, 100), 8, false, 1f),

    PATH(Color.rgb(60, 60, 60), 4, false, 1f),

    HIGHWAY(Color.rgb(255, 180, 50), 0, false, 2f),

    SUBWAY(Color.rgb(233, 132, 31), 6, false, 1f),

    CITYWAY(Color.rgb(0, 0, 0), 6, false, 1f),

    ROAD(Color.rgb(0, 0, 0), 5, false, 1f),

    MOTORWAY(Color.rgb(255, 0, 0), 0, false, 2f),

    COASTLINE(Color.rgb(0, 0, 0), 0, false, 2f),

    STONE(Color.rgb(192, 192, 192), 1, true, 1f),

    DIJKSTRA(Color.rgb(255, 255, 100), 0, false, 2f);

    private final Color color;
    private final int requiredZoom;
    private final boolean fill;
    private final float width;

    WayType(Color color, int requiredZoom, boolean fill, float width) {
        this.color = color;
        this.requiredZoom = requiredZoom;
        this.fill = fill;
        this.width = width;
    }

    public Color getColor() {
        return color;
    }

    public boolean fillTrue() {
        return fill;
    }

    public float getRequiredZoom() {
        return requiredZoom;
    }

    public float getWidth() {
        return width;
    }
}
