import java.awt.*;

public class Surface
{
   private Vector[] points;
   private Vector[] worldPoints;

   public int getNumbPoints() {
      return numbPoints;
   }

   private int numbPoints;
   private Color fillColor;

   public Color getLightCorrectedFill() {
      return lightCorrectedFill;
   }

   private Color lightCorrectedFill;
   private double depth;
   private boolean colorCorrected;


   public Vector[] getPoints() {
      return points;
   }

   public Surface() {
      points = new Vector[5];
      worldPoints = new Vector[5];
      numbPoints = 0;
      fillColor = Color.WHITE;
      depth = 0;
      colorCorrected = true;
   }

   public void setLightingCorrected(boolean value) {
      colorCorrected = value;
   }
   
   public void add(Vector pointIn, Vector worldPointIn) {
      if (numbPoints == points.length) {
         Vector[] temp = points;
         Vector[] temp1 = worldPoints;
         points = new Vector[numbPoints + 50];
         worldPoints = new Vector[numbPoints + 50];
         for (int i = 0; i < numbPoints; i++) {
            points[i] = temp[i];
            worldPoints[i] = temp1[i];
         }
      }
      points[numbPoints] = pointIn;
      worldPoints[numbPoints] = worldPointIn;
      numbPoints++;
   }
   
   public void setFillColor(Color colorIn) {
      fillColor = colorIn;
      lightCorrectedFill = fillColor;
   }
   

   public void addToDrawStack() {
      if (isVisible()) {
         if ((Light.getNumbLights() > 0) && (!Drawer.getWireframe()) && colorCorrected) {
            setLighting();
         }
         
         if (Drawer.getDrawOrder().length < Drawer.getVisibleSurfaces() + 1) {
            Surface[] temp = Drawer.getDrawOrder();
            Surface[] drawOrder = new Surface[Drawer.getVisibleSurfaces() + 50];
            Drawer.setDrawOrder(drawOrder);

            for (int i = 0; i < Drawer.getVisibleSurfaces(); i++) {
               drawOrder[i] = temp[i];
            }
         }
         
         int left = 0;
         int right = Drawer.getVisibleSurfaces() - 1;
         int insertIndex = 0;
         if (right > left) {
            insertIndex = left + ((right - left) / 2);
            double otherDepth = Drawer.getDrawOrder()[insertIndex].depth;
            while ((otherDepth != depth) && (left != right)) {
               if (depth < otherDepth) {
                  right = (insertIndex - 1 >= left) ? insertIndex - 1 : left;
               }
               if (depth > otherDepth) {
                  left = (insertIndex + 1 <= right) ? insertIndex + 1 : right;
               }
               insertIndex = left + ((right - left) / 2);
               otherDepth = Drawer.getDrawOrder()[insertIndex].depth;
            }
         }
            
         Drawer.setVisibleSurfaces(Drawer.getVisibleSurfaces() + 1);

         Surface[] drawOrder1 = Drawer.getDrawOrder();
         for (int i = Drawer.getVisibleSurfaces() - 1;(i > insertIndex) && (i > 0); i--) {
            drawOrder1[i] = drawOrder1[i - 1];
         }
         drawOrder1[insertIndex] = this;
      }
   }

   private void setLighting() {
      Vector line1 = worldPoints[1].subtract(worldPoints[0]);
      Vector line2 = worldPoints[2].subtract(worldPoints[1]);
      
      Vector surfaceNormal = line1.cross(line2).normalize();
      
      Vector sum = new Vector();
      
      for (int i = 0; i < numbPoints; i++) {
         sum.add(worldPoints[i]);
      }
      
      Vector avg = sum.scale(1 / numbPoints);
      
      double lightingPercentage = 0;
      
      for (int i = 0; i < Light.getNumbLights(); i++) {
         Vector lightNormal = avg.subtract(Light.getLightSource().get(i)).normalize();
         double dot = surfaceNormal.dot(lightNormal);
         
         if (-dot > 0) {
            lightingPercentage += -dot;
         }
      }
      
      if (lightingPercentage < 0) {
         lightingPercentage = 0;
      } 
      else if (lightingPercentage > 1) {
         lightingPercentage = 1;
      }
      
      int red = (int)(fillColor.getRed() * lightingPercentage);
      int green = (int)(fillColor.getGreen() * lightingPercentage);
      int blue = (int)(fillColor.getBlue() * lightingPercentage);
      
      lightCorrectedFill = new Color(red, green, blue);
   }

   
   private boolean isVisible() {
      
      Vector line1 = points[1].subtract(points[0]);
      
      Vector line2 = points[2].subtract(points[1]);
      
      Vector surfaceNormal = line1.cross(line2).normalize();
      
      double dot = surfaceNormal.dot(new Vector(0, 0, 1));
      boolean visible = false;
      double sum = 0;
      
      if (dot < 0 || Drawer.getWireframe()) {
         for (int i = 0; i < numbPoints; i++) {

            if ((points[i].getZ() >= 0) &&
               (points[i].getX() > 0) &&
               (points[i].getX() < Drawer.getCam().getWidth()) &&
               (points[i].getY() > 0) &&
               (points[i].getY() < Drawer.getCam().getHeight())) {
               visible = true;
            }
            sum += points[i].getZD();
         }
      }
      depth = sum / numbPoints;
      
      if (depth < 0) {
         visible = false;
      }
      
      return visible;
   }
}
