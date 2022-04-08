package bfst22.vector;

public class Vertex {
    private final float x, y;

    private int[] neighboringEdges = new int[0];

    public Vertex(float x, float y){
        this.x = x;
        this.y = y;
    }


    public void addEdge(int id){
        int length = neighboringEdges.length;
        int[] newLength = new int[length+1];

        for (int i = 0; i < length; i++) {
            newLength[i] = neighboringEdges[i];
        }
        neighboringEdges = newLength;
        neighboringEdges[length] = id;

    }

    public float[] getCoords(){
        return new float[]{x, y};
    }

    public int[] getNeighboringEdges(){
        return neighboringEdges;
    }

}
