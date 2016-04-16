import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.EmptyBorder;

public class Solver implements ListSelectionListener {

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
        int w = tab.getRowCount() * W;
        int h = (tab.getColumnCount() + 1) * H;
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
        pan.setToolTipText("Swing components are wonderful");
        lab.setToolTipText("Really");
        tab.setToolTipText("A cute little table");

        frm.setContentPane(pan); 
        frm.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //frm.setLocation(90, 50);
        frm.pack(); frm.setVisible(true);
    }
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) return;
        System.out.println("row "+tab.getSelectedRow());
    }
    public void solve() {
        mat.solve(); tab.repaint();
        msg.setText("solved!");
    }

    public static float scaled(float k) { return k*RES_RATIO; }
    public static int scaled(int k) { return Math.round(k*RES_RATIO); }
    public static Font scaledFont(String name, int style, float size) {
        Font f =  new Font(name, style, 1); //unit font
        return f.deriveFont(scaled(size));
    }
    public static void main(String[] args) {
        System.out.println("Begin Solver");
        new Solver(); 
    }
}
