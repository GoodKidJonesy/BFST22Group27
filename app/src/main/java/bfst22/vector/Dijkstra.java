package bfst22.vector;

import java.util.*;
import java.util.Map.Entry;

//Initial code from baeldung.com
public class Dijkstra {

}
//graph class, contains a Set of all nodes in the graph.
class Graph{
    private Set<Node> nodes = new HashSet<>();
    public void addNode(Node node){
        nodes.add(node);
    }
    //node class, each node has a name, List of nodes of the shortestpath. Distance from source and a map of adjacent/connected nodes.
    class Node{
        String name;
        List<Node> shortestPath = new LinkedList<>();
    
        Integer distance = Integer.MAX_VALUE;
    
        Map<Node, Integer> adjacentNodes = new HashMap<>();
    
        public void addDestination(Node dest, int dist){
            adjacentNodes.put(dest, dist);
        }
        public Node(String name){
            this.name = name;
        }
        public void setDistance(int dist){
            this.distance = dist;
        }
        public Map<Node, Integer> getAdjacentNodes(){
            return adjacentNodes;
        }
        public Integer getDistance(){
            return distance;
        }
        public List<Node> getShortestPath(){
            return shortestPath;
        }
        public void setShortestPath(List<Node> s){
            this.shortestPath = s;
        }
        
    }
    //method to calculate shortestpath from start node in graph to all nodes in graph.
    public static Graph calculateShortestPath(Graph graph, Node start){
        start.setDistance(0);
        Set<Node> settledNodes = new HashSet<>();
        Set<Node> unsettledNodes = new HashSet<>();

        unsettledNodes.add(start);

        while(unsettledNodes.size() != 0){
            Node currentNode = getLowestDistanceNode(unsettledNodes);
            unsettledNodes.remove(currentNode);

            for(Entry<Node, Integer> adjacency : 
                currentNode.getAdjacentNodes().entrySet()){
                    Node adjacentNode = adjacency.getKey();
                    Integer weight = adjacency.getValue();
                    if(!settledNodes.contains(adjacency)){
                        calculateMinimumDistance(adjacentNode, weight, currentNode);
                        unsettledNodes.add(adjacentNode);
                    }
                }
                settledNodes.add(currentNode);
        }
        return graph;
    }
    //method to calculate the lowestdistance node in the set of unsettled nodes.
    public static Node getLowestDistanceNode(Set<Node> unsettled){
        Node lowestDistanceNode = null;
        int lowestDistance = Integer.MAX_VALUE;
        for(Node n : unsettled){
            int nodeDistance = n.getDistance();
            if(nodeDistance < lowestDistance){
                lowestDistance = nodeDistance;
                lowestDistanceNode = n;
            }
        }
        return lowestDistanceNode;
    }
    //method to calculate the minimum distance from a source node to and evaluation node with the edge weight.
    public static void calculateMinimumDistance(Node eval, Integer weight, Node source){
        Integer sourceDistance = source.getDistance();
        if(sourceDistance + weight < eval.getDistance()){
            eval.setDistance(sourceDistance + weight);
            LinkedList<Node> shortestPath = new LinkedList<>(source.getShortestPath());
            shortestPath.add(source);
            eval.setShortestPath(shortestPath);
        }
    }

}

