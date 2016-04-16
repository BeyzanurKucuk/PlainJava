import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.EmptyBorder;

public class Solver implements ListSelectionListener, Runnable {

    static final String MSG = "Linear Equation Solver -- V0 Apr 2016";
    static final int 
        RESOLUTION = Toolkit.getDefaultToolkit().getScreenResolution();
    static final float 
        RES_RATIO = RESOLUTION/96f;  //default resolution is 96
    static final int H = scaled(30), W = scaled(80), GAP = scaled(8);
    static final Font NORMAL = scaledFont("Dialog", 0, 14);
    static final Font LARGE = scaledFont("Dialog", 0, 18);
    
    final Matrix mat = new Matrix();
    final JFrame frm = new JFrame("Solver");
    final JLabel lab = new JLabel(MSG);
    final JTable tab = new JTable(mat);
    final JLabel msg = new JLabel(mat.toString());
    
    public Solver() {
        JPanel pan = new JPanel(new BorderLayout(GAP, GAP));
        
        tab.setRowHeight(H);
        tab.setFont(LARGE);
        Component head = tab.getTableHeader();
        head.setFont(NORMAL);
        //tab.setFillsViewportHeight(true);
        tab.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scr = new JScrollPane(tab);
        int w = mat.getColumnCount() * W;
        int h = (mat.getRowCount() + 1) * H;
        scr.setPreferredSize(new Dimension(w, h));
        tab.getSelectionModel().addListSelectionListener(this);
        pan.add(scr, "Center");
        
        lab.setFont(NORMAL);
        lab.setHorizontalAlignment(SwingConstants.CENTER);
        pan.add(lab, "North");
        msg.setFont(NORMAL);
        msg.setHorizontalAlignment(SwingConstants.CENTER);
        pan.add(msg, "South");
        
        pan.setBorder(new EmptyBorder(GAP, GAP, GAP, GAP));
        tab.setToolTipText("Cofficients");
        msg.setToolTipText("The result");

        frm.setContentPane(pan); 
        frm.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //frm.setLocation(90, 50);
        frm.pack(); frm.setVisible(true);
    }
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) return;
        //System.out.println("row "+tab.getSelectedRow());
    }
    public void run() {
        int k = 0; boolean done = false;
        while (!done) {
            done = mat.forward(k);
            tab.getSelectionModel().setSelectionInterval(k, k);
            k++; msg.setText("Row "+k);
            //System.out.println("Row "+k);
            tab.repaint();
            try { Thread.sleep(1500);
            } catch (InterruptedException e) {
            }
        }
        msg.setText("Determinant = "+mat.det);
        if (mat.getRowCount() == mat.getColumnCount()) return;
        mat.backward(); tab.repaint(); 
    }
    public void solve() {
        System.out.println("Begin Solver");
        new Thread(this).start(); 
    }

    public static float scaled(float k) { return k*RES_RATIO; }
    public static int scaled(int k) { return Math.round(k*RES_RATIO); }
    public static Font scaledFont(String name, int style, float size) {
        Font f =  new Font(name, style, 1); //unit font
        return f.deriveFont(scaled(size));
    }
    public static void main(String[] args) {
        new Solver().solve(); 
    }
}
