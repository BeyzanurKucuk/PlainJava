import java.util.ArrayList;

class Permutations<T> {

   ArrayList<T> lst;  

   public Permutations(T[] a) { 
      lst = new ArrayList<T>();
      for (T x : a) lst.add(x);
      permute("");
   }
   void permute(String perm) {
      if (lst.isEmpty()) System.out.println(perm);
      else for (int i=0; i<lst.size(); i++) {
         T x = lst.remove(i); 
         permute(perm+x);
         lst.add(i, x); 
      }
   }
   
   final static String[] A = { "a", "b", "c" };
   final static Number[] N = { 0, 1, 2, 3 };
   public static void main(String[] args) {
      new Permutations<String>(A);  
      new Permutations<Number>(N); 
   }
}
