package bfst22.vector;

import java.util.ArrayList;

public class OSMNodeParser {

    public OSMNodeParser() {
    }
  
    public void parseOSMNodes(KDTree tree, ArrayList<OSMNode> list, OSMNodeSorter sorter, int depth) {
      int medianVal;
      ArrayList<OSMNode> left = new ArrayList<>();
      ArrayList<OSMNode> right = new ArrayList<>();
      //If the list is not empty go forth
      if (list.size() != 0) {
        //If we need to compare X-values
        if (depth % 2 == 0) {
          //Sort the given list
          sorter.sortX(list);
          //Calculate the median OSMNode
          if (list.size() % 2 == 0) {
            medianVal = (list.size() / 2) - 1;
          } else {
            medianVal = list.size() / 2;
          }
  
          //Insert the median OSMNode in the list
          tree.insert(tree.getRoot(), list.get(medianVal), depth);
  
          //Fill out the left and right sides from the median in new lists
          for (int i = 0; i < medianVal; i++) {
            left.add(list.get(i));
          }
          for (int i = medianVal +1; i < list.size(); i++) {
            right.add(list.get(i));
          }
          //Call recursivly
          parseOSMNodes(tree, left, sorter, depth + 1);
          parseOSMNodes(tree, right, sorter, depth + 1);
        }
        //If we need to compare Y-values
        else {
          //Sort the given list
          sorter.sortY(list);
          //Calculate the median OSMNode
          if (list.size() % 2 == 0) {
            medianVal = (list.size() / 2) - 1;
          } else {
            medianVal = list.size() / 2;
          }
          //Insert the median OSMNode
          tree.insert(tree.getRoot(), list.get(medianVal), depth);
  
          //Fill out the left and right sides from the median in new lists
          for (int i = 0; i < medianVal; i++) {
            left.add(list.get(i));
          }
          for (int i = medianVal +1; i < list.size(); i++) {
            right.add(list.get(i));
          }
          //Call recursivly
          parseOSMNodes(tree, left, sorter, depth + 1);
          parseOSMNodes(tree, right, sorter, depth + 1);
        }
      }
    }
  }