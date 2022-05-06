package bfst22.vector;

import java.util.ArrayList;
import java.util.Stack;

public class Dijkstra {
private double[] distTo;
private Edge[] edgeTo;
private ArrayList<Double> pq;

    public Dijkstra(EdgeWeightedDigraph G, long s){
        for (Edge e : G.edges()){
            if(e.getWeight() < 0){
                throw new IllegalArgumentException("edge " + e + " has negative weight");
            }

        }
    }

    private void relax(Edge e){}

    public double distTo(int v){
        validateVertex(v);
        return distTo[v];
    }

    public boolean hasPathTo(int v){
        validateVertex(v);
        return distTo[v] < Double.POSITIVE_INFINITY;
    }

    public Iterable<Edge> pathTo(int v){
        validateVertex(v);
        if (!hasPathTo(v)) return null;
        Stack<Edge> path = new Stack<Edge>();
        for (Edge e = edgeTo[v]; e != null; e = edgeTo[e.getFrom2()]){
            path.push(e);
        }
        return path;
    }

    private boolean check(EdgeWeightedDigraph G, int s){
        //Checks if edge weights are not negative
        for(Edge e : G.edges()){
            if (e.getWeight() < 0){
                System.err.println("Negative Edge weight detected");
                return false;
            }
        }

        //checks that distTo[v] and edgeTo[v] are consistent
        if (distTo[s] != 0.0 || edgeTo[s] != null){
            System.err.println("Inconsistensy detected");
            return false;
        }
        //Checks that all Edges satisfy distance to w is smaller than distance to v + edge weight
        for (int i = 0; i < G.V(); i++) {
            for (Edge e : G.adj(i)){
                int w = e.getTo2();
                if (distTo[i] + e.getWeight() < distTo[w]){
                    System.err.println("Edge" + e + " not relaxed");
                    return false;
                }
            }
        }

        for (int i = 0; i < G.V(); i++) {
            if(edgeTo[i] == null) continue;
            Edge e = edgeTo[i];
            int v = e.getFrom2();
            if (i != e.getTo2()) return false;
            if(distTo[v] + e.getWeight() != distTo[i]){
                System.out.println("Edge " + e + " on shortest path not tight");
                return false;
            }
            
        }
        return true;

    }

    private void validateVertex(int v){
        int V = distTo.length;
        if (v < 0 || v >= V){
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V-1));
        }
    }


}
