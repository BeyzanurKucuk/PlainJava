import number.Number;
import number.Whole;
import number.Rational;
import javax.swing.table.TableModel;
import javax.swing.event.TableModelListener;

class Row {
   final int N;
   final Number[] data;
   public Row(int[] d) { 
      N = d.length; data = new Number[N];  //Arrays.copyOf(d, N);
      for (int j=0; j<N; j++) 
          data[j] = new Whole(d[j]);
   }
   public Number pivot(int j) {
       Number p = data[j];
       multiply(p.inverse());  //multiply(1/p);
       return p;
   }
   public void multiply(Number c) {
      System.out.printf("x %s \n", c);
      for (int j=0; j<N; j++)  {
          data[j] = data[j].mult(c);  // *= c;
      }
   }
   public void addRow(Number c, Row r) {
      //System.out.printf("addRow %s x [%s] \n", c, r);
      for (int j=0; j<N; j++)  {
          data[j] = data[j].add(r.data[j].mult(c));  //  += c * r.data[j];
      }
   }
   public String toString() { 
      String s = "";
      for (int j=0; j<N; j++)  {
         if (j > 0) s += "\t";
         s += data[j].toString();
         //if (s.endsWith(".0")) s = s.substring(0, s.length()-2);
      }
      return s;
   }
}

class Matrix implements TableModel {
   final int M;
   final Row[] row;
   Number det = null;
   public Matrix() { this(B); }
   public Matrix(int[][] a) {
      M = a.length; row = new Row[M];
      for (byte i=0; i<M; i++) row[i] = new Row(a[i]);
      System.out.println(this);
   }
   public void printData() {
      for (int i=0; i<M; i++) {
         //System.out.printf("%5.0f\t", val(i, j));
         System.out.println(row[i]);
      }
      System.out.println();
   }
   float abs_val(int i, int j) {
      return Math.abs(row[i].data[j].value());
   }
   public int pickRow(int k) {
      int m = k;
      for (int i=k+1; i<M; i++) 
          if (abs_val(m, k) < abs_val(i, k)) m = i;
      return m;
   }
   public void exchange(int i, int k) {
      if (i == k) return;
      det = minus(det);  //-det;
      Row r = row[i]; row[i] = row[k]; row[k] = r; 
      System.out.printf("exchange %s <=> %s \n", i, k);
      //printData();
   }
   public void operate(int k) {
      det = det.mult(row[k].pivot(k));
      for (int i=k+1; i<M; i++) {
          row[i].addRow(minus(row[i].data[k]), row[k]);  //-val(i, k)
      }
      printData();
   }
   public void solve() {
      det = new Whole(1);
      for (int k=0; k<M; k++) {
          int i = pickRow(k);
          exchange(i, k);
          operate(k); 
      }
      System.out.printf("det = %s \n", det);
   }
   public Class<?> getColumnClass(int j) { return Number.class; }
   public int getRowCount() { return M; }
   public int getColumnCount() { return row[0].N; }
   public String getColumnName(int j) { return "x"+(j+1); }
   public Object getValueAt(int i, int j) { return row[i].data[j]; }
   public void setValueAt(Object v, int i, int j) { 
       row[i].data[j] = (Number)v; 
   }
   public boolean isCellEditable(int i, int j) { return false; }
   public void addTableModelListener(TableModelListener l) { }
   public void removeTableModelListener(TableModelListener l) { }
   public String toString() {
       return M+"x"+row[0].N+" matrix";
   }

   public static final int[][] 
      A = { { 1, 4, 1 }, { 2, 2, 1 }, { 3, 0, 3 } },
      B = { { 2, 2, 1, 1 }, { 1, 4, 0, 1 }, { 3, 1, 2,-2 }, { 3, 0, 1, 2 } };
   static final Number MINUS = new Whole(-1);
   static Number minus(Number n) {
      return n.mult(MINUS);
   }
   public static void main(String[] args) {
      Matrix m = new Matrix();
      m.printData();
      m.solve();
   }
}
