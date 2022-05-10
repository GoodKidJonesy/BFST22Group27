package bfst22.vector;

import java.util.*;

public class Dijkstra {

    private Edge[] edgeTo;
    private IndexMinPQ<Double> pq;
    private double[] dist;


    public Dijkstra(EdgeWeightedDigraph G, int s) {
        //set array of distances to hold infinity, and visited vertex boolean to false
        Boolean visited[] = new Boolean[G.V()];
        dist = new double[G.V()];
        edgeTo = new Edge[G.V()];
        for (int i = 0; i < G.V(); i++) {
            dist[i] = Double.POSITIVE_INFINITY;
            visited[i] = false;
        }
        //Distance to source is always 0
        dist[s] = 0.0;

        pq = new IndexMinPQ<Double>(G.V());
        pq.insert(s, dist[s]);

        while(!pq.isEmpty()){
            int v = pq.delMin();
            for (Edge e : G.adj(v)){
                relax(e);
            }
        }












    }

    private void relax(Edge e){
        int v = e.getFrom2(), w = e.getTo2();
        if(dist[w] > dist[v] + e.getWeight()){
            dist[w] = dist[v] + e.getWeight();
            edgeTo[w] = e;
            if (pq.contains(w)) pq.decreaseKey(w, dist[w]);
            else{
                pq.insert(w, dist[w]);
            }
        }
    }

    public double dist(int v){
        return dist[v];
    }

    public boolean hasPath(int v){
        return dist[v] < Double.POSITIVE_INFINITY;
    }

    public Iterable<Edge> pathTo(int v){
        if (!hasPath(v)) return null;
        Stack<Edge> path = new Stack<Edge>();
        for (Edge e = edgeTo[v]; e != null; e = edgeTo[e.getFrom2()]) {
            path.push(e);
        }
        return path;
    }



}
