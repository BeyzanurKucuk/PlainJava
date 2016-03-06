import java.util.ArrayList;

class Determinant {
   
   ArrayList<Byte> lst; 
   float[][] data;
   int count; float sum; 

   public Determinant(float[][] a) { 
      lst = new ArrayList<Byte>();
      for (byte i=0; i<a.length; i++) lst.add(i);
      data = a; printData();
      count = 0; sum = 0; 
      permute("", 1);  //recursive call
      System.out.printf("%s terms %n", count);
      System.out.printf("sum %s %n", sum); 
   }
   public void printData() {
      for (int i=0; i<data.length; i++) {
         for (int j=0; j<data.length; j++) 
            System.out.printf("%5.0f\t", data[i][j]);
         System.out.println();
      }
   }
   void permute(String perm, float term) {
      int k = perm.length(); 
      if (lst.isEmpty()) { 
          System.out.printf("%s\t%s %n", perm, term); 
          count++; sum += term; 
      } else for (int i=0; i<lst.size(); i++) {
          Byte j = lst.get(i); 
          float t = data[k][j];
          if (t != 0) {
              lst.remove(i); 
              permute(perm+j, term*t);
              lst.add(i, j); 
          }
          term = -term;
      }
   }

   final static float[][] 
      A = { { 1, 4, 1 }, { 2, 2, 1 }, { 3, 0, 3 } },
      B = { { 1, 4, 0, 1 }, { 2, 2, 1, 1 }, { 3, 1, 2,-2 }, { 3, 0, 1, 2 } };
   public static void main(String[] args) {
      new Determinant(B);
   }
}
