package bfst22.vector;

import java.util.*;

import javafx.geometry.Point2D;

public class KDTreeOSMNode {
  private OSMNode root;
  private OSMNode nearest;
  private float shortestDist;

  // Sorter. It is parsed a list of nodes to sort, and an axis for it to sort by
  private static class OSMNodeSorter {
    public static void sort(ArrayList<OSMNode> l, int depth) {
      Collections.sort(l, new Comparator<OSMNode>() {
        public int compare(OSMNode n1, OSMNode n2) {
          if (depth % 2 == 0) {
            return Float.valueOf(n1.getX()).compareTo(n2.getX());
          } else {
            return Float.valueOf(n1.getY()).compareTo(n2.getY());
          }
        }
      });
    }
  }

  public KDTreeOSMNode() {
    this.root = null;
  }

  public OSMNode getRoot() {
    return root;
  }

  public OSMNode getNearest(){
    return nearest;
  }

  public float getShortestDist(){
    return shortestDist;
  }

  // Void for outsiders to call when they want to add a node
  public void add(OSMNode n) {
    root = add(root, n, 0);
  }

  // Called by void add, this places the node in the tree
  private OSMNode add(OSMNode r, OSMNode n, int depth) { // credit Sedgewick and Wayne
    // If we are looking at an empty node, fill it out
    if (r == null) {
      return n;
    }

    // If the node we passed in is less than the current root, call recursivly on
    // left child of root. The opposite for the else statement
    if (compare(r, n, depth) == 1) {
      r.left = add(r.left, n, depth + 1);
    } else {
      r.right = add(r.right, n, depth + 1);
    }

    // Return the root
    return r;
  }

  // Void for filling the tree with nodes
  public void fillTree(ArrayList<OSMNode> OSMNodes, int depth) { // Credit to tcla for helping with this fill function
    ArrayList<OSMNode> left = new ArrayList<>();
    ArrayList<OSMNode> right = new ArrayList<>();
    int median;

    // If 0 nodes are parsed in, getAvg out
    if (OSMNodes.size() == 0) {
      return;
    }

    // Sort nodes
    OSMNodeSorter.sort(OSMNodes, depth);

    // Find the median value
    median = findMedian(OSMNodes);

    // Declare chosen node
    OSMNode n = OSMNodes.get(median);

    // Add the chosen node to the tree
    add(n);

    for (int i = 0; i < median; i++) {
      left.add(OSMNodes.get(i));
    }
    for (int i = median + 1; i < OSMNodes.size(); i++) {
      right.add(OSMNodes.get(i));
    }

    // Call recursivly with the remaining nodes
    fillTree(left, depth + 1);
    fillTree(right, depth + 1);
  }

  // Calculate the median value of a given list
  private int findMedian(ArrayList<OSMNode> OSMNodes) {
    if (OSMNodes.size() % 2 == 0) {
      return (OSMNodes.size() / 2) - 1;
    } else {
      return OSMNodes.size() / 2;
    }
  }

  // Void for comparing two nodes based on axis
  private int compare(OSMNode n1, OSMNode n2, int depth) {
    if (depth % 2 == 0) {
      if (n1.getX() < n2.getX())
        return -1;
      else
        return +1;
    } else {
      if (n1.getY() < n2.getY())
        return -1;
      else
        return +1;
    }
  }

  // Check if given node is inside of given Range
  public boolean isInside(OSMNode n, Range r) {
    if (n.getX() > r.getLeft())
      if (n.getX() < r.getRight())
        if (n.getY() > r.getTop())
          if (n.getY() < r.getBottom())
            return true;
    return false;
  }

  // Query function, returns list of
  public ArrayList<OSMNode> query(OSMNode n, Range r, int depth) {
    ArrayList<OSMNode> found = new ArrayList<OSMNode>();

    if (n == null) {
      return null;
    }

    if (isInside(n, r))
      found.add(n);

    // Call recursivly based on where the range is compared to our node.
    // "Call on left child if its to the left or above (based on axis) of our node"
    if (depth % 2 == 0) {
      if (r.getLeft() < n.getX() && n.left != null) {
        found.addAll(query(n.left, r, depth + 1));
      }
      if (r.getRight() > n.getX() && n.right != null) {
        found.addAll(query(n.right, r, depth + 1));
      }
    } else {
      if (r.getTop() < n.getY() && n.left != null) {
        found.addAll(query(n.left, r, depth + 1));
      }
      if (r.getBottom() > n.getY() && n.right != null) {
        found.addAll(query(n.right, r, depth + 1));
      }
    }
    // return all the found nodes
    return found;
  }

  public void printTree(OSMNode n) {
    if (n == null) {
      System.out.println("Tree is empty");
      return;
    }
    // System.out.println(n);

    if (n.left != null)
      printTree(n.left);
    if (n.right != null)
      printTree(n.right);
  }

  public boolean isEmpty() {
    return root == null ? true : false;
  }

  private double distTo(OSMNode r, Point2D target) {
    return Math.hypot(r.getX() - target.getX(), r.getY() - target.getY());
  }

  // Used to check whether we should check other side of kdtree
  private double distTo(Point2D refference, Point2D target) {
    return Math.hypot(refference.getX() - target.getX(), refference.getY() - target.getY());
  }

  private void nearestNeighbor(Point2D target, OSMNode r, int depth) {
    if (r == null) {
      return;
    }

    if (distTo(r, target) < shortestDist) {
      shortestDist = (float) distTo(r, target);
      nearest = r;
    }

    if (depth % 2 == 0) {
      if (target.getX() < r.getX()) {
        nearestNeighbor(target, r.left, depth + 1);
        if (distTo(new Point2D(r.getX(), target.getY()), target) < distTo(r, target)) {
          nearestNeighbor(target, r.right, depth + 1);
        }
      } else {
        nearestNeighbor(target, r.right, depth + 1);
        if (distTo(new Point2D(r.getX(), target.getY()), target) < distTo(r, target)) {
          nearestNeighbor(target, r.left, depth + 1);
        }
      }

    } else {
      if (target.getY() < r.getY()) {
        nearestNeighbor(target, r.left, depth + 1);
        if (distTo(new Point2D(target.getX(), r.getY()), target) < distTo(r, target)) {
          nearestNeighbor(target, r.right, depth + 1);
        }
      } else {
        nearestNeighbor(target, r.right, depth + 1);
        if (distTo(new Point2D(target.getX(), r.getY()), target) < distTo(r, target)) {
          nearestNeighbor(target, r.left, depth + 1);
        }
      }

    }
  }

  public OSMNode getNearestNeighbor(Point2D target) {
    nearest = root;
    shortestDist = (float) distTo(root, target);
    nearestNeighbor(target, root, 0);
    return nearest;
  }
}
