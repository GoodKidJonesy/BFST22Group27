package bfst22.vector;

import java.io.Serializable;

public class Edge implements Serializable {
    private final long from, to;
    private final float weight, distance;
    String name;
    //private final boolean forCar, forBike, forWalk;


    public Edge(
                long from,
                long to,
                String name,
                float weight,
                float distance
                ){

        this.from = from;
        this.to = to;
        this.name = name;
        this.weight = weight;
        this.distance = distance;

    }

    public long getFrom() {
        return from;
    }
    public long getTo(){
        return to;
    }
    public float getWeight(){
        return weight;
    }
    public float getDistance(){
        return distance;
    }


}
