package bfst22.vector;

import java.io.Serializable;
import java.util.*;

public class KDTree {
  int nodes = 0;

  // Sorter. It is parsed a list of nodes to sort, and an axis for it to sort by
  private static class DrawableSorter {
    public static void sort(ArrayList<Drawable> l, int depth) {
      Collections.sort(l, new Comparator<Drawable>() {
        public int compare(Drawable n1, Drawable n2) {
          if (depth % 2 == 0) {
            return Float.valueOf(n1.getAvgX()).compareTo(n2.getAvgX());
          } else {
            return Float.valueOf(n1.getAvgY()).compareTo(n2.getAvgY());
          }
        }
      });
    }
  }

  private Drawable root;

  public KDTree() {
    this.root = null;
  }

  public Drawable getRoot() {
    return root;
  }

  // Void for outsiders to call when they want to add a node
  public void add(Drawable n) {
    root = add(root, n, 0);
  }

  // Called by void add, this places the node in the tree
  private Drawable add(Drawable r, Drawable n, int depth) { // credit Sedgewick and Wayne
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
  public void fillTree(ArrayList<Drawable> Drawables, int depth) { // Credit to tcla for helping with this fill function
    ArrayList<Drawable> left = new ArrayList<>();
    ArrayList<Drawable> right = new ArrayList<>();
    int median;

    // If 0 nodes are parsed in, getAvg out
    if (Drawables.size() == 0) {
      return;
    }

    // Sort nodes
    DrawableSorter.sort(Drawables, depth);

    // Find the median value
    median = findMedian(Drawables);

    // Declare chosen node
    Drawable n = Drawables.get(median);

    // Add the chosen node to the tree
    add(n);

    for (int i = 0; i < median; i++) {
      left.add(Drawables.get(i));
    }
    for (int i = median + 1; i < Drawables.size(); i++) {
      right.add(Drawables.get(i));
    }

    // Call recursivly with the remaining nodes
    fillTree(left, depth + 1);
    fillTree(right, depth + 1);
  }

  // Calculate the median value of a given list
  private int findMedian(ArrayList<Drawable> Drawables) {
    if (Drawables.size() % 2 == 0) {
      return (Drawables.size() / 2) - 1;
    } else {
      return Drawables.size() / 2;
    }
  }

  // Void for comparing two nodes based on axis
  private int compare(Drawable n1, Drawable n2, int depth) {
    if (depth % 2 == 0) {
      if (n1.getAvgX() < n2.getAvgX())
        return -1;
      else
        return +1;
    } else {
      if (n1.getAvgY() < n2.getAvgY())
        return -1;
      else
        return +1;
    }
  }

  // Check if given node is inside of given Range
  public boolean isInside(Drawable n, Range r) {
    if (n.getAvgX() > r.getLeft())
      if (n.getAvgX() < r.getRight())
        if (n.getAvgY() > r.getTop())
          if (n.getAvgY() < r.getBottom())
            return true;
    return false;
  }

  // Query function, returns list of
  public ArrayList<Drawable> query(Drawable n, Range r, int depth) {
    ArrayList<Drawable> found = new ArrayList<Drawable>();

    if (n == null) {
      return null;
    }

    if (isInside(n, r))
      found.add(n);

    // Call recursivly based on where the range is compared to our node.
    // "Call on left child if its to the left or above (based on axis) of our node"
    if (depth % 2 == 0) {
      if (r.getLeft() < n.getAvgX() && n.left != null) {
        found.addAll(query(n.left, r, depth + 1));
      }
      if (r.getRight() > n.getAvgX() && n.right != null) {
        found.addAll(query(n.right, r, depth + 1));
      }
    } else {
      if (r.getTop() < n.getAvgY() && n.left != null) {
        found.addAll(query(n.left, r, depth + 1));
      }
      if (r.getBottom() > n.getAvgY() && n.right != null) {
        found.addAll(query(n.right, r, depth + 1));
      }
    }
    // return all the found nodes
    return found;
  }

  public void printTree(Drawable n) {
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

  public int getAvgSize(Drawable n) {
    nodes++;
    if (n.left != null)
      getAvgSize(n.left);
    if (n.right != null)
      getAvgSize(n.right);
    return nodes;
  }
}
