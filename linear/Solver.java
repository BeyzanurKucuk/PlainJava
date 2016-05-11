import number.Number;
import number.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.JTableHeader;
import javax.swing.border.EmptyBorder;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;

public class Solver implements Runnable {

    enum Mode { none, exch, mult, addR }
    
    static final String 
        MSG  = "Linear Equation Solver -- V1.0 May 2016",
        HELP = "X: exchange  M: multiply  A: add row",
        EXCH = "exchange R",
        MULT = "multiply R",
        ADDR = "add to R";
    static final Clipboard CB = Toolkit.getDefaultToolkit().getSystemClipboard();
    static final DataFlavor STR = DataFlavor.stringFlavor;
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
    final JFrame frm = new JFrame("Solver");
    final JLabel msg = new JLabel(MSG);
    final JButton but = new JButton("New");
    final JLabel lab1 = new JLabel(HELP);
    final JLabel lab2 = new JLabel("  x R");
    final JTextField txt1 = new JTextField();
    final JTextField txt2 = new JTextField();
    final JLabel det = new JLabel();
    Mode mode;
    
    public Solver() { this(W, H); }
    public Solver(int x, int y) { this(matrixFromCB(), x, y); }
    public Solver(Matrix m, int x, int y) {
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
        //System.out.println(d);
        int w = mat.getColumnCount() * W;
        int h = mat.getRowCount() * H + (d.height+3);
        scr.setPreferredSize(new Dimension(w, h));
        //tab.getSelectionModel().addListSelectionListener(ear);
        tab.addKeyListener(ear);
        tab.addMouseListener(ear);
        pan.add(scr, "Center");
        
        pan.add(topPanel(), "North");
        pan.add(bottomPanel(), "South");
        
        pan.setBorder(new EmptyBorder(GAP, GAP, GAP, GAP));
        tab.setToolTipText("Coefficients");
        det.setToolTipText("The result");

        frm.setContentPane(pan); 
        frm.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frm.setLocation(x, y);
        frm.pack(); 
        setMode(Mode.none);  //displayFields(false);
        frm.setVisible(true);
    }
    JPanel topPanel() {
        JPanel top = new JPanel();
        
        msg.setFont(NORMAL);
        msg.setHorizontalAlignment(SwingConstants.CENTER);
        top.add(msg);
        
        but.setFont(SMALL);
        but.addActionListener(ear);
        top.add(but);
        return top;
    }
    JPanel composite() {
        JPanel p = new JPanel();
        lab1.setFont(SMALL);
        p.add(lab1);
        txt1.setFont(SMALL);
        txt1.setColumns(3);
        txt1.addActionListener(ear);
        p.add(txt1);
        lab2.setFont(SMALL);
        p.add(lab2);
        txt2.setFont(SMALL);
        txt2.setColumns(3);
        txt2.addActionListener(ear);
        p.add(txt2);
        return p;
    }
    JPanel bottomPanel() {
        JPanel bot = new JPanel();
        bot.setLayout(new BoxLayout(bot, BoxLayout.X_AXIS));
        
        bot.add(composite());
        bot.add(Box.createHorizontalGlue());
        
        det.setText(mat.toString());
        det.setFont(NORMAL);
        det.setHorizontalAlignment(SwingConstants.CENTER);
        bot.add(det);
        //bot.add(Box.createHorizontalStrut(H));
        
        return bot;
    }
    public void run() {
        int k = 0; boolean done = false;
        while (!done) {
            done = mat.forward(k);
            tab.getSelectionModel().setSelectionInterval(k, k);
            k++;  //det.setText("Row "+k);
            display();  //tab.repaint();
            try { Thread.sleep(1500);
            } catch (InterruptedException e) {
            }
        }
        if (mat.getRowCount() == mat.getColumnCount()) return;
        mat.backward(); display();
    }
    public void solve() {
        System.out.println("Begin Solver");
        new Thread(this).start(); 
    }
    public void exchange(int i, int k) {
        System.out.printf("exchange row %s by row %s \n", i, k);
        mat.exchange(i, k); display();
    }
    public void multiply(int i, int k) { multiply(i, new Whole(k));}
    public void multiply(int i, Number k) {
        System.out.printf("multiply row %s by %s \n", i, k);
        mat.divide(i, k.inverse()); display();
    }
    public void addRow(int i, int j) { addRow(i, new Whole(1), j); }
    public void addRow(int i, Number k, int j) {
        System.out.printf("add to row %s: %s x row %s \n", i, k, j);
        mat.row[i].addRow(k, mat.row[j]); display();
    }
    void display() {
        det.setText("|A| = "+mat.det); tab.repaint(); 
    }
    void report() {
        int i = tab.getSelectedRow(); int j = tab.getSelectedColumn();
        System.out.printf("row:%s, col:%s\n", i, j);
    }
    void displayFields(boolean b) {
        if (b) { txt1.selectAll(); txt2.selectAll(); }
        txt1.setVisible(b); lab2.setVisible(b); txt2.setVisible(b); 
    }
    void setMode(Mode m) {
        if (mode == m) return;
        mode = m; 
        int i = tab.getSelectedRow();
        if (m == Mode.exch) {
            lab1.setText(EXCH+i+" by R");
            txt1.selectAll(); txt1.setVisible(true);
        } else if (m == Mode.mult) {
            lab1.setText(MULT+i);
            txt1.selectAll(); txt1.setVisible(true);
        } else if (m == Mode.addR) {
            lab1.setText(ADDR+i+"  "); 
            displayFields(true);
        } else {   //  Mode.none
            lab1.setText(HELP);
            displayFields(false);
        }
    }
    void doAction() {
        int i = tab.getSelectedRow();
        if (mode == Mode.exch) {
            Number n = Factory.parseNumber(txt1.getText());
            exchange(i, (int)n.value()); 
        } else if (mode == Mode.mult) {
            Number k = Factory.parseNumber(txt1.getText());
            multiply(i, k); 
        } else if (mode == Mode.addR) {
            Number k = Factory.parseNumber(txt1.getText());
            Number n = Factory.parseNumber(txt2.getText());
            addRow(i, k, (int)n.value());
        }
        setMode(Mode.none);
    }

    class Ear extends MouseAdapter implements ActionListener, KeyListener {
        public void valueChanged(ListSelectionEvent e) { //ListSelectionListener
            setMode(Mode.none);
            if (e.getValueIsAdjusting()) return; 
            else report();
        }
        public void keyTyped(KeyEvent e) { 
            char c = e.getKeyChar();
            System.out.println("keyTyped "+c);
            if (c == KeyEvent.VK_ESCAPE) setMode(Mode.none);
            else if (c == 'x' || c == ',') setMode(Mode.exch); 
            else if (c == 'm' || c == '*') setMode(Mode.mult); 
            else if (c == 'a' || c == '+') setMode(Mode.addR); 
            else if (c == 'n') but.doClick();
        }
        public void keyPressed(KeyEvent e) { }
        public void keyReleased(KeyEvent e) { }
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() > 1) System.out.println("double click"); 
            else report();
        }
        public void actionPerformed(ActionEvent e) {
            Object s = e.getSource();
            //System.out.println(s.getClass().getName());
            if (s == txt1 || s == txt2) doAction();
            else if (s == but) new Solver(frm.getX()+30, frm.getY()+30);
        }
    }

    public static float scaled(float k) { return k*RES_RATIO; }
    public static int scaled(int k) { return Math.round(k*RES_RATIO); }
    public static Font scaledFont(String name, int style, float size) {
        Font f =  new Font(name, style, 1); //unit font
        return f.deriveFont(scaled(size));
    }
    public static Matrix matrixFromCB() {
        try {
            String s = (String)CB.getData(STR);
            return new Matrix(s.split("\\n"));
        //UnsupportedFlavorException  IOException
        } catch(Exception e) { 
            return new Matrix();  //throw new RuntimeException(e);
        }
    }
    public static void main(String[] args) {
        new Solver().solve(); 
    }
}
