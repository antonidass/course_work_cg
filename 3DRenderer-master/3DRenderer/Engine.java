import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.AlphaComposite;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Engine extends JPanel implements ActionListener
{
   private Timer clock, updateFps, output;
   private double count = 0;
   private Model satelite, earth;
   private Camera cam;
   private Vector position;
   private Vector[] earthSystem;
   private int speedSystem;
   private long currentTime, lastTime;
   private int fps, maxFps, minFps;
   private int[] avgFps;
   private int fpsIndex;
   private ProjectionWindow parent;
   public boolean isMemento;

   public Engine(ProjectionWindow parentIn) {
      parent = parentIn;
      
      setBackground(Color.BLACK);
      
      fps = 100000;
      maxFps = 0;
      minFps = fps;
      
      avgFps = new int[60];
      fpsIndex = 0;

      speedSystem = 60;
      
      currentTime = 0;
      lastTime = 0;
      
      clock = new Timer(100 / fps, this);
      clock.setCoalesce(true);
      updateFps = new Timer(1500, parent);
      updateFps.setActionCommand("fps timer");
      output = new Timer(5000, this);

      satelite = new Model();
      earth = new Model();
      position = new Vector(0, 400, 0);

      // наклон орбиты
      int inclination = 20;
      
      earthSystem = new Vector[3];
      earthSystem[1] = new Vector(0, 1, 0);
      earthSystem[2] = (new Vector(Math.sin(inclination * Matrix3x3.TORAD), 0, Math.cos(inclination * Matrix3x3.TORAD))).normalize();
      earthSystem[0] = earthSystem[1].cross(earthSystem[2]);
   }


   public void setTimer(Boolean state) {
      if (state) {
         clock.start();
         updateFps.start();
         output.start();
         lastTime = System.nanoTime();
      }else {
         clock.stop();
         updateFps.stop();
         output.stop();
      }
   }

   public void paint(Graphics g) {
      super.paint(g);
      Graphics2D g2D = (Graphics2D) g;

      earth.setDrawStack();
      satelite.setDrawStack();
      try {
         Drawer.drawAll(g2D);
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public void actionPerformed(ActionEvent event) {
      if (event.getSource() == clock){
         step();
      } 
      else if (event.getSource() == output) {
         System.out.println("Max FPS: " + maxFps);
         System.out.println("Min FPS: " + minFps + "\n");
         
         maxFps = 0;
         minFps = fps;
      }
   }

   public void rotateScene(int x, int y, int z) {
      cam.setLocation((new Vector(x, y, z)).scale(2));
      cam.setRight((new Vector(-1, 0, 0)));
      cam.setDown(cam.getLocation().normalize().scale(-1).cross(cam.getRight()));

      setCamera(cam);
      Drawer.setCamera(cam);
   }
   
   private void step() {
      repaint();
      satelite.addYRotation(count * 1.5);
      satelite.addZRotation(count * 1);
      earth.addZRotation(-0.25 * count);
      
      Matrix3x3 rotate = Matrix3x3.rotateZ(count * 1.5);

      Matrix3x3 transform = new Matrix3x3(earthSystem[0], earthSystem[1], earthSystem[2]);
      transform = transform.getTranspose();

      satelite.setPosition(transform.multiply(rotate.multiply(position)));
      count += (speedSystem / (double)fps);
      
      currentTime = System.nanoTime();
      fps = (int)(1000000000 / (currentTime - lastTime));
      
      if (fps > maxFps) {
         maxFps = fps;
      } 
      else if (fps < minFps){
         minFps = fps;
      }
      
      avgFps[fpsIndex] = fps;
      fpsIndex++; 
      if (fpsIndex == avgFps.length) {
         fpsIndex = 0;
      }
      
      lastTime = System.nanoTime();
   }

   
   public void load() {
      satelite = new Model(cam);
      satelite.setPosition(position);
      satelite.readFile("input/rocket5.dat");
      satelite.scale(0.225);
      earth = new Model(cam);
      earth.readFile("input/sphereGen6.dat");
      earth.scale(1.5);


      Vector xAxis = satelite.getPosition().normalize().scale(-1);
      Vector yAxis = earthSystem[2];

      satelite.setCoordinateSystem(xAxis, yAxis, xAxis.cross(yAxis));
   }
   
   public void setCamera(Camera newCam) {
      cam = newCam;
      satelite.setCamera(cam);
      earth.setCamera(cam);
      Drawer.setCamera(cam);
   }
   

   public int getFps() {
      double avg = 0;
      for (int i = 0; i < avgFps.length; i++) {
         avg += avgFps[i];
      }
      avg /= avgFps.length;
      
      return (int)avg;
   }


   public void setSpeedSystem(int speedSystem) {
      this.speedSystem = speedSystem;
   }
}
