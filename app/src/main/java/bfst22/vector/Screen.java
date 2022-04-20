package bfst22.vector;

import java.awt.Window;

import javafx.scene.canvas.Canvas;

//Screen class is for defining the users screen as a box in the program
public class Screen {
    private double x1, y1, x2, y2;
    
    public Screen(double x1, double y1, double x2, double y2) {
      this.x1 = x1;
      this.y1 = y1;
      this.x2 = x2;
      this.y2 = y2;
    }

    public void update(double _x1, double _y1, double _x2, double _y2){
      x1 = _x1;
      y1 = _y1;
      x2 = _x2;
      y2 = _y2;

      //System.out.println(x1 + " " + y1);
      //System.out.println(x2 + " " + y2);
    }
    
    public double getLeft(){
      return x1; 
    }
    public double getRight(){
      return x2; 
    }
    public double getTop(){
      return y1;
    }
    public double getBottom(){
      return y2; 
    }
  }
