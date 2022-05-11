package bfst22.vector;

public class Vertex {
    private final float x, y;
    private final long id;

    private int[] neighboringEdges = new int[0];

    public Vertex(long id, float x, float y) {
        this.x = x;
        this.y = y;
        this.id = id;
    }

    public void addEdge(int id) {
        int length = neighboringEdges.length;
        int[] newLength = new int[length + 1];

        for (int i = 0; i < length; i++) {
            newLength[i] = neighboringEdges[i];
        }
        neighboringEdges = newLength;
        neighboringEdges[length] = id;

    }

    public float[] getCoords() {
        return new float[] { x, y };
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int[] getNeighboringEdges() {
        return neighboringEdges;
    }

}
