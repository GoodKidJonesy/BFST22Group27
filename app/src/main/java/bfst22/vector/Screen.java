package bfst22.vector;

import java.awt.Window;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;

//Screen class is for defining the users screen as a box in the program
public class Screen {
    private Point2D topLeft, bottomRight;
    
    public Screen(Point2D topLeft, Point2D bottomRight) {
      this.topLeft = topLeft;
      this.bottomRight = bottomRight;
    }

    public void update(Point2D tL, Point2D bR){
      topLeft = tL;
      bottomRight = bR;
    }
    
    public double getLeft(){
      return topLeft.getX(); 
    }
    public double getTop(){
      return topLeft.getY();
    }
    public double getRight(){
      return bottomRight.getX(); 
    }
    public double getBottom(){
      return bottomRight.getY(); 
    }
  }
