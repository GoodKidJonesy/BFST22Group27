package bfst22.vector;

import java.awt.Window;

import javafx.scene.canvas.Canvas;

//Screen class is for defining the users screen as a box in the program
public class Screen {
    private float left, right, top, bottom;
    
    public Screen(float left, float right, float top, float bottom) {
      this.left = left;
      this.right = right;
      this.top = top;
      this.bottom = bottom;
    }

    public void update(double dx, double dy, double zoomScale){
      System.out.println("left: " + getLeft());
      System.out.println("right: " + getRight());
      System.out.println("top: " + getTop());
      System.out.println("bottom: " + getBottom());
    }
    
    public double getLeft(){
      return left; 
    }
    public double getRight(){
      return right; 
    }
    public double getTop(){
      return top;
    }
    public double getBottom(){
      return bottom; 
    }
  }
