import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.JTableHeader;
import javax.swing.border.EmptyBorder;

public class Solver implements Runnable {

    static final String MSG = "Linear Equation Solver -- V1.0 May 2016";
    static final int 
        RESOLUTION = Toolkit.getDefaultToolkit().getScreenResolution();
    static final float 
        RES_RATIO = RESOLUTION/96f;  //default resolution is 96
    static final int H = scaled(30), W = scaled(80), GAP = scaled(8);
    static final Font LARGE = scaledFont("Dialog", 0, 16);
    static final Font NORMAL = scaledFont("Dialog", 0, 13);
    static final Font SMALL = scaledFont("Dialog", 0, 10);
    
    final Matrix mat;
    final JTable tab;
    final Ear ear = new Ear();
    final JFrame frm;
    JLabel lab, msg, res;
    
    public Solver(Matrix m) {
        mat = m;
        JPanel pan = new JPanel(new BorderLayout(GAP, GAP));
        
        tab = new JTable(mat);
        tab.setRowHeight(H);
        tab.setFont(LARGE);
        JTableHeader head = tab.getTableHeader();
        head.setFont(NORMAL);
        head.setReorderingAllowed(false);
        //tab.setFillsViewportHeight(true);
        tab.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JLabel cr = (JLabel)tab.getCellRenderer(0, 0);
        cr.setHorizontalAlignment(JLabel.CENTER);

        JScrollPane scr = new JScrollPane(tab);
        Dimension d = head.getPreferredSize();
        System.out.println(d);
        int w = mat.getColumnCount() * W;
        int h = mat.getRowCount() * H + (d.height+3);
        scr.setPreferredSize(new Dimension(w, h));
        //tab.getSelectionModel().addListSelectionListener(ear);
        tab.addMouseListener(ear);
        pan.add(scr, "Center");
        
        lab = new JLabel(MSG);
        lab.setFont(NORMAL);
        lab.setHorizontalAlignment(SwingConstants.CENTER);
        pan.add(lab, "North");
        
        pan.add(bottomPanel(), "South");
        
        pan.setBorder(new EmptyBorder(GAP, GAP, GAP, GAP));
        tab.setToolTipText("Cofficients");
        msg.setToolTipText("The result");

        frm = new JFrame("Solver");
        frm.setContentPane(pan); 
        frm.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //frm.setLocation(90, 50);
        frm.pack(); frm.setVisible(true);
    }
    JPanel bottomPanel() {
        JPanel bot = new JPanel();
        bot.setLayout(new BoxLayout(bot, BoxLayout.X_AXIS));
        msg = new JLabel("x: exchange  +: add row");
        msg.setFont(SMALL);
        msg.setHorizontalAlignment(SwingConstants.LEFT);
        bot.add(msg);
        
        bot.add(Box.createHorizontalGlue());
        
        res = new JLabel(mat.toString());
        res.setFont(NORMAL);
        res.setHorizontalAlignment(SwingConstants.CENTER);
        bot.add(res);
        bot.add(Box.createHorizontalStrut(H));
        
        JButton b = new JButton("Paste");
        b.setFont(NORMAL);
        b.addActionListener(ear);
        bot.add(b);
        return bot;
    }
    public void run() {
        int k = 0; boolean done = false;
        while (!done) {
            done = mat.forward(k);
            tab.getSelectionModel().setSelectionInterval(k, k);
            k++; res.setText("Row "+k);
            //System.out.println("Row "+k);
            tab.repaint();
            try { Thread.sleep(1500);
            } catch (InterruptedException e) {
            }
        }
        res.setText("|A| = "+mat.det);
        if (mat.getRowCount() == mat.getColumnCount()) return;
        //mat.backward(); tab.repaint(); 
    }
    public void solve() {
        System.out.println("Begin Solver");
        new Thread(this).start(); 
    }
    void report() {
        System.out.printf("at %s-%s\n", tab.getSelectedRow(), tab.getSelectedColumn());
    }

   class Ear extends MouseAdapter implements ActionListener { //ListSelectionListener
     public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) return; report();
     }
     public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() > 1) System.out.println("double click"); 
        else report();
     }
     public void actionPerformed(ActionEvent e) {
        JButton b = (JButton)e.getSource();
        System.out.println(b.getText());
     }
   }

    public static float scaled(float k) { return k*RES_RATIO; }
    public static int scaled(int k) { return Math.round(k*RES_RATIO); }
    public static Font scaledFont(String name, int style, float size) {
        Font f =  new Font(name, style, 1); //unit font
        return f.deriveFont(scaled(size));
    }
    public static Matrix pasteMatrix() {
        String s = pasteString();
        if (s == null) return new Matrix();
        Matrix m = new Matrix(s.split("\\n"));
        System.out.println(m);
        return m;
    }
    public static String pasteString() {
        try {
            return Toolkit.getDefaultToolkit().getSystemClipboard()
            .getData(java.awt.datatransfer.DataFlavor.stringFlavor).toString();
        //UnsupportedFlavorException  IOException
        } catch(Exception e) { 
            return null;  //throw new RuntimeException(e);
        }
    }
    public static void main(String[] args) {
        new Solver(pasteMatrix()).solve(); 
    }
}
