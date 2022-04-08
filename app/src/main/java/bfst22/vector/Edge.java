package bfst22.vector;

import java.io.Serializable;

public class Edge implements Serializable {
    private final int from, to;
    private final float weight, distance;
    String name;
    private final boolean forCar, forBike, forWalk;


    public Edge(String name,
                int from,
                int to,
                float weight,
                float distance,
                boolean forCar,
                boolean forBike,
                boolean forWalk){
        this.name = name;
        this.from = from;
        this.to = to;
        this.weight = weight;
        this.distance = distance;
        this.forCar = forCar;
        this.forBike = forBike;
        this.forWalk = forWalk;
    }

    public int getFrom() {
        return from;
    }
    public int getTo(){
        return to;
    }
    public float getWeight(){
        return weight;
    }
    public float getDistance(){
        return distance;
    }
    public boolean canDrive(){
        return forCar;
    }
    public boolean canBike(){
        return forBike;
    }
    public boolean canWalk(){
        return forWalk;
    }

}
