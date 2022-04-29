package bfst22.vector;

import java.util.*;

public class OSMNodeSorter {

  public OSMNodeSorter() {
  }

  public void sortX(ArrayList<OSMNode> l) {
    Collections.sort(l, new Comparator<OSMNode>() {
      public int compare(OSMNode n1, OSMNode n2) {
        return Float.valueOf(n1.getX()).compareTo(n2.getX());
      }
    }
    );
  }

  public void sortY(ArrayList<OSMNode> l) {
    Collections.sort(l, new Comparator<OSMNode>() {
      public int compare(OSMNode n1, OSMNode n2) {
        return Float.valueOf(n1.getY()).compareTo(n2.getY());
      }
    }
    );
  }
}
