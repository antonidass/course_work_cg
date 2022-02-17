import java.text.DecimalFormat;


public class Vector
{
   private double x = 0;
   private double y = 0;
   private double z = 0;
   

   public Vector(){
      this(0,0,0);
   }
   

   public Vector(int a, int b){
      this((double)a ,(double)b);
   }
   

   public Vector(int a, int b, int c){
      this((double)a,(double)b,(double)c);
   }
   

   public Vector(double a, double b){
      this(a,b,0.0);
   }


   public Vector(double a, double b, double c){
      x = a;
      y = b;
      z = c;
   }
   

   public int getX(){
      return (int)x;
   }
   

   public int getY(){
      return (int)y;
   }
   

   public int getZ(){
      return (int)z;
   }
   

   public double getXD(){
      return x;
   }
   

   public double getYD(){
      return y;
   }
   

   public double getZD(){
      return z;
   }
   

   public double[] toArray() {
      double[] array = {x, y, z};
      return array;
   }
   

   public Vector normalize(){
      return new Vector(x/this.magnitude(),y/this.magnitude(),z/this.magnitude());
   }
   
   public double magnitude() {
      return Math.sqrt((x * x) + (y * y) + (z * z));
   }
   

   public Vector add(Vector a) {
      return new Vector(x + a.x, y + a.y, z + a.z);
   }
   

   public Vector subtract(Vector a) {
      return new Vector(x - a.x, y - a.y, z - a.z);
   }
   

   public Vector scale(double scalar) {
      return new Vector(x * scalar, y * scalar, z * scalar);
   }
   

   public double dot(Vector a){
      return (x * a.x) + (y * a.y) + (z * a.z);
   }
   

   public String toString(){
      DecimalFormat f = new DecimalFormat("0.00000");
      return "\n| " + f.format(x) + " |\n| " + f.format(y) + " |\n| " + f.format(z) + " |\n";
   }
   

   public Vector cross(Vector b){
      return new Vector((y * b.z) - (z * b.y), (z * b.x) - (x * b.z), (x * b.y) - (y * b.x));
   }
   

   public void setFromVector(Vector input) {
      x = input.x;
      y = input.y;
      z = input.z;
   }
   

   public boolean equals(Vector v) {
      return ((x == v.x) && (y == v.y) && (z == v.z));
   }
}
