import number.Number;
import number.Whole;
import number.Factory;
import javax.swing.table.TableModel;
import javax.swing.event.TableModelListener;

class Row {
   final int N;
   final Number[] data;
   public Row(int[] d) { 
      N = d.length; data = new Number[N]; 
      for (int j=0; j<N; j++) 
          data[j] = new Whole(d[j]);
   }
   public Row(Number[] d) { 
      N = d.length; data = new Number[N]; 
      for (int j=0; j<N; j++) 
          data[j] = d[j];
   }
   public void multiply(Number c) {
      for (int j=0; j<N; j++)  {
          data[j] = data[j].mult(c);  // *= c;
      }
   }
   public void addRow(Number c, Row r) {
      for (int j=0; j<N; j++)  {
          data[j] = data[j].add(r.data[j].mult(c));  //  += c * r.data[j];
      }
   }
   public String toString() { 
      String s = "";
      for (int j=0; j<N; j++)  {
         if (j > 0) s += " \t";
         s += data[j].toString();
      }
      return s;
   }
}

class Matrix implements TableModel {
   final int M;
   final Row[] row;
   final boolean notTooManyVars;
   Number det = new Whole(1);
   public Matrix() { this(B); }
   public Matrix(int[][] a) {
      M = a.length; row = new Row[M];
      for (byte i=0; i<M; i++) row[i] = new Row(a[i]);
      notTooManyVars = row[0].N < NAME.length;
      System.out.println(this);
   }
   public Matrix(String[] a) {
      M = a.length; row = new Row[M];
      for (byte i=0; i<M; i++) row[i] = new Row(Factory.parseRow(a[i]));
      notTooManyVars = row[0].N < NAME.length;
      System.out.println(this);
   }
   public void printData() {
      for (int i=0; i<M; i++) {
         System.out.println(row[i]);
      }
      System.out.println();
   }
   float abs_val(int i, int j) {
      return Math.abs(row[i].data[j].value());
   }
   int pickRow(int k) {
      int m = k;
      for (int i=k+1; i<M; i++) 
          if (abs_val(m, k) < abs_val(i, k)) m = i;
      return m;
   }
   public void exchange(int i, int k) {
      if (i == k) return;
      det = minus(det);  //-det;
      Row r = row[i]; row[i] = row[k]; row[k] = r; 
      System.out.printf("exchange row %s <=> row %s \n", i, k);
   }
   public void divide(int i, Number p) {
      Number c = p.inverse();
      row[i].multiply(c); det = det.mult(p);
      System.out.printf("mult row %s by %s \n", i, c);
   }
   public void addRow(int i, Number c, int k) {
      row[i].addRow(minus(row[i].data[k]), row[k]);  //-val(i, k)
      System.out.printf("add %s x row %s to row %s \n", c, k, i);
   }
   boolean forward(int k) { //returns true if work is done
       //if (k == 0) det = new Whole(1); 
       int j = pickRow(k);
       if (abs_val(j, k) < 1E-10) return true;
       exchange(j, k); 
       Number p = row[k].data[k];
       divide(k, p);  //multiply(1/p);
       for (int i=k+1; i<M; i++) 
           addRow(i, minus(row[i].data[k]), k);  //-val(i, k)
       return (k == M-1);
   }
   void backward() {
       for (int k=M-1; k>0; k--) 
           for (int i=0; i<k; i++) 
               row[i].addRow(minus(row[i].data[k]), row[k]);
   }
   public void solve() {
       printData(); int k = 0; 
       boolean done = false;
       while (!done) {
           done = forward(k);
           k++; printData();
       }
       System.out.printf("det = %s \n", det);
       if (M == getColumnCount()) return;
       backward(); printData();
   }
   public Class<?> getColumnClass(int j) { return Number.class; }
   public int getRowCount() { return M; }
   public int getColumnCount() { return row[0].N; }
   public Object getValueAt(int i, int j) { return row[i].data[j]; }
   public void setValueAt(Object v, int i, int j) { 
       row[i].data[j] = (Number)v; 
   }
   public String getColumnName(int j) { 
       if (notTooManyVars) return NAME[j]; 
       return Character.toString((char)('p'+j)); 
   }
   public boolean isCellEditable(int i, int j) { return false; }
   public void addTableModelListener(TableModelListener l) { }
   public void removeTableModelListener(TableModelListener l) { }
   public String toString() {
       return M+"x"+row[0].N+" matrix";
   }

   static final int[][] 
      A = { { 1, 4, 1 }, { 2, 2, 1 }, { 3, 0, 3 } },
      B = { { 2, 2, 1, 1, 2 }, { 1, 4, 0, 1,-1 }, 
            { 3, 1, 2,-2,-2 }, { 3, 0, 1, 2, 7 } },
      C = { { 2, 2, 1, 1 }, { 1, 4, 0, 1 }, { 3, 1, 2,-2 }, { 3, 0, 1, 2 } };
   static final String[] 
      NAME = { "x", "y", "z", "s", "t" };
   static final Number MINUS = new Whole(-1);      
   static Number minus(Number n) {
      return n.mult(MINUS);
   }
   public static void main(String[] args) {
      new Matrix().solve();
   }
}
