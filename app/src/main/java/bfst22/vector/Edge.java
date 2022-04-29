package bfst22.vector;

import java.io.Serializable;

public class Edge implements Serializable {
    private final Vertex from, to;
    private final float weight, distance;
    long name;
    //private final boolean forCar, forBike, forWalk;


    public Edge(
                Vertex from,
                Vertex to,
                float weight,
                float distance
                ){

        this.from = from;
        this.to = to;
        this.weight = weight;
        this.distance = distance;

    }

    public Vertex getFrom() {
        return from;
    }
    public Vertex getTo(){
        return to;
    }
    public float getWeight(){
        return weight;
    }
    public float getDistance(){
        return distance;
    }


}
