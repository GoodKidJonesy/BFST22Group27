package bfst22.vector;

public class KDTree {

  private OSMNode root;

  public KDTree() {
    this.root = null;
  }

  public OSMNode getRoot() {
    return root;
  }

  public void insert(OSMNode r, OSMNode n, int depth) {
    if (root == null) {
      root = n;
      n.parent = new OSMNode(0, 0, 0);
      System.out.print("First OSMNode");
      System.out.print(n.getX() + " " + n.getY());
    } else {
      //Should we compare x-values?
      System.out.print(depth % 2);
      System.out.print(n.getX() + " " + n.getY());
      if (depth % 2 == 0) {
        //if new x-value is less than current
        if (n.getX() < r.getX()) {
          //if current OSMNode has children, call recursivly; else make new OSMNode current OSMNodes child
          if (r.left != null) {
            insert(r.left, n, depth + 1);
          } else {
            System.out.println("inserted left");
            r.left = n;
            n.parent = r;
          }
        }
        //if new x-value is greater than current
        else {
          //if current OSMNode has children, call recursivly; else make new OSMNode current OSMNodes child
          if (r.right != null) {
            insert(r.right, n, depth + 1);
          } else {
            System.out.println("inserted right");
            r.right = n;
            n.parent = r;
          }
        }
      } 
      //If we should compare y-values
      else {
        //if new y-value is less than current
        if (n.getY() < r.getY()) {
          //if current OSMNode has children, call recursivly; else make new OSMNode current OSMNodes child
          if (r.left != null) {
            insert(r.left, n, depth + 1);
          } else {
            System.out.println("inserted left");
            r.left = n;
            n.parent = r;
          }
        }
        //if new y-value is greater than current
        else {
          //if current OSMNode has children, call recursivly; else make new OSMNode current OSMNodes child
          if (r.right != null) {
            insert(r.right, n, depth + 1);
          } else {
            System.out.println("inserted right");
            r.right = n;
            n.parent = r;
          }
        }
      }
    }
  }

  public void printTree(OSMNode r) {
    if (root == null) {
      System.out.println("Tree is empty");
    } else {
      System.out.println(r.getX() + " " + r.getY());
      if (r.left != null) {
        printTree(r.left);
      }
      if (r.right != null) {
        printTree(r.right);
      }
    }
  }

  /*public void drawTree(OSMNode r, int depth) {
    if (root == null) {
      System.out.println("Tree is empty");
    } else {

      if (depth % 2 == 0) {
        drawLine(r, depth);
        drawCircle(r);
      } else {
        drawLine(r, depth);
        drawCircle(r);
        
      }

      if (r.left != null) {
        drawTree(r.left, depth + 1);
      }
      if (r.right != null) {
        drawTree(r.right, depth + 1);
      }
    }
  }


  private void drawCircle(OSMNode r) {
    fill(0);
    noStroke();
    ellipse(r.getX(), r.getY(), 7, 7);
  }
  
  private void drawLine(OSMNode r, int depth) {
    if (depth % 2 == 0) {
      stroke(0, 0, 255);
      if (r.getY() < r.parent.getY()) {
        line(r.getX(), 0, r.getX(), r.parent.getY());
      } else {
        line(r.getX(), height, r.getX(), r.parent.getY());
      }
    } else {
      stroke(255, 0, 0);
      if (r.getX() < r.parent.getX()) {
        line(0, r.getY(), r.parent.getX(), r.getY());
      } else {
        line(width, r.getY(), r.parent.getX(), r.getY());
      }
    }
  }*/
}
