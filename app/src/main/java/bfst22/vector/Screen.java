package bfst22.vector;

import java.awt.Window;

//Screen class is for defining the users screen as a box in the program
public class Screen {
    private float x, y;
    private int w,h;
    
    public Screen(float x, float y, int w, int h) {
      this.x = x;
      this.y = y;
      this.w = Window.WIDTH;
      this.h = Window.HEIGHT;
    }
    
    public float getLeft(){
      return x - (w / 2);  
    }
    public float getRight(){
      return x + (w / 2);  
    }
    public float getTop(){
      return y - (h / 2);  
    }
    public float getBottom(){
      return y + (h / 2);  
    }
  }
