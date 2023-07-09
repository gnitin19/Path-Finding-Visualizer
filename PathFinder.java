import java.awt.Graphics;
import java.awt.Image;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.security.interfaces.DSAKeyPairGenerator;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JComboBox;

public class PathFinder extends JFrame{
    JFrame frame;
    private int cells = 20;
    private int delay = 30;
    private double dense = .5;
    private double density = (cells*cells)*.5;
    private int startx = -1;
    private int starty = -1;
    private int finishx = -1;
    private int finishy = -1;
    private int tool = 0;
    private int checks = 0;
    private int length = 0;
    private int curAlg = 0;
    private int WIDTH = 850;
    private final int HEIGHT = 650;
    private final int MSIZE = 600;
    private int CSIZE = MSIZE/cells;
    
    private String[] algorithms = {"Dijkstra's","A*"};
    private String[] tools = {"Source","Destination","Block", "Erase"};

    private boolean solving = false;
     
    Node[][] map;
    Algorithm Alg = new Algorithm();
    Random r = new Random();

    JSlider size = new JSlider(1,5,2);
    JSlider speed = new JSlider(0,500,delay);
    JSlider obstacles = new JSlider(1,100,50);
    
    JLabel title = new JLabel("Operators");
    JLabel algL = new JLabel("Algorithms");
    JLabel toolL = new JLabel("Toolbox");
    JLabel sizeL = new JLabel("Size:");
    JLabel cellsL = new JLabel(cells+"x"+cells);
    JLabel delayL = new JLabel("Speed:");
    JLabel msL = new JLabel(delay+"ms");
    JLabel obstacleL = new JLabel("Dens:");
    JLabel densityL = new JLabel(obstacles.getValue()+"%");
    JLabel checkL = new JLabel("No of Checks: "+checks);
    JLabel lengthL = new JLabel("Minimum Distance: "+length);

    JButton searchB = new JButton("Search");
    JButton resetB = new JButton("Reset");
    JButton genMapB = new JButton("Generate Map");
    JButton clearMapB = new JButton("Clear Map");
    JButton creditB = new JButton("Details");

    JComboBox algorithmsBx = new JComboBox(algorithms);
    JComboBox toolBx = new JComboBox(tools);

    JPanel toolP = new JPanel();

    Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);

    Map canvas;


    
    public static void main(String[] args) {
       
        new PathFinder();
    }

    public PathFinder() {
        clearMap();
        initialize();
        getContentPane().setBackground(Color.black);
    }

    public void clearMap() {	 
        finishx = -1;	 
        finishy = -1;
        startx = -1;
        starty = -1;
        map = new Node[cells][cells];	 
        for(int x = 0; x < cells; x++) {
            for(int y = 0; y < cells; y++) {
                map[x][y] = new Node(3,x,y);	 
            }
        }
        reset();	 
    }


    public void generateMap() {
        clearMap();	
        for(int i = 0; i < density; i++) {
            Node current;
            do {
                int x = r.nextInt(cells);
                int y = r.nextInt(cells);
                current = map[x][y];	
            } while(current.getType()==2);	
            current.setType(2);	
        }
    }

    public void resetMap() {	
        for(int x = 0; x < cells; x++) {
            for(int y = 0; y < cells; y++) {
                Node current = map[x][y];
                if(current.getType() == 4 || current.getType() == 5)	
                    map[x][y] = new Node(3,x,y);
            }
        }
        if(startx > -1 && starty > -1) {
            map[startx][starty] = new Node(0,startx,starty);
            map[startx][starty].setHops(0);
        }
        if(finishx > -1 && finishy > -1)
            map[finishx][finishy] = new Node(1,finishx,finishy);
             reset();	

    }

    
    private void initialize() {	
        frame = new JFrame();
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setSize(WIDTH,HEIGHT);
        frame.setTitle("Path Finding Visualizer");
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.getContentPane().setLayout(null);
        frame.getContentPane().setBackground(Color.BLACK);
        toolP.setBorder(BorderFactory.createTitledBorder(loweredetched));
      
        int space = 25;
        int buff = 45;

        toolP.setLayout(null);
        toolP.setBounds(620,10,210,600);
        toolP.setBackground(Color.black);

        title.setFont(new Font("Rale way",Font.BOLD,20));
        title.setBounds(20,0,150,35);
        title.setBackground(Color.black);
        title.setForeground(Color.white);
        toolP.add(title);

        searchB.setBounds(25,space+10, 120, 25);
        searchB.setBackground(Color.red);
        searchB.setForeground(Color.white);
        toolP.add(searchB);
        space+=buff;

        resetB.setBounds(25,space+10,120,25);
        resetB.setBackground(Color.red);
        resetB.setForeground(Color.white);
        toolP.add(resetB);
        space+=buff;

        genMapB.setBounds(25,space+10, 120, 25);
        genMapB.setBackground(Color.red);
        genMapB.setForeground(Color.white);
        toolP.add(genMapB);
        space+=buff;

        clearMapB.setBounds(25,space+10, 120, 25);
        clearMapB.setBackground(Color.red);
        clearMapB.setForeground(Color.white);
        toolP.add(clearMapB);
        space+=40;

        algL.setBounds(25,space+10,120,25);
        algL.setForeground(Color.white);
        toolP.add(algL);
        space+=25;

        algorithmsBx.setBounds(25,space+10, 120, 25);
        algorithmsBx.setForeground(Color.white);
        algorithmsBx.setBackground(Color.black);
        algorithmsBx.setEditable(false);
        toolP.add(algorithmsBx);
        space+=40;
        

        toolL.setBounds(25,space+10,120,25);
        toolL.setForeground(Color.white);
        toolP.add(toolL);
        space+=25;

        toolBx.setBounds(25,space+10, 120, 25);
        toolBx.setForeground(Color.white);
        toolBx.setBackground(Color.black);
        toolBx.setEditable(false);
        toolP.add(toolBx);
        space+=25;
        sizeL.setBounds(15,space+50,40,25);
        sizeL.setForeground(Color.white);
        toolP.add(sizeL);
        size.setMajorTickSpacing(10);
        size.setBounds(55,space+50,100,25);
        size.setBackground(Color.black);
        space+=25;
        toolP.add(size);
        cellsL.setBounds(160,space+20,40,25);
        cellsL.setForeground(Color.white);
        toolP.add(cellsL);
        space+=buff;

        delayL.setBounds(15,space+20,50,25);
        delayL.setForeground(Color.white);
        toolP.add(delayL);
        speed.setMajorTickSpacing(10);
        speed.setBounds(55,space+20,100,25);
        speed.setBackground(Color.black);
       
        toolP.add(speed);
        msL.setBounds(160,space+15,40,25);
        msL.setForeground(Color.white);
        toolP.add(msL);
        space+=buff;

        obstacleL.setBounds(15,space+10,100,25);
        obstacleL.setForeground(Color.white);
        toolP.add(obstacleL);
        obstacles.setMajorTickSpacing(5);
        obstacles.setBounds(55,space+10,100,25);

        toolP.add(obstacles);
        densityL.setBounds(160,space+10,100,25);
        densityL.setForeground(Color.white);
        toolP.add(densityL);
        space+=buff;
        obstacles.setBackground(Color.black);

        checkL.setBounds(15,space+10,100,25);
        checkL.setForeground(Color.white);
        toolP.add(checkL);
        space+=buff;

        lengthL.setBounds(15,space+10,200,25);
        lengthL.setForeground(Color.white);
        toolP.add(lengthL);
        space+=buff;

        creditB.setBounds(40, space+10, 120, 25);
        creditB.setBackground(Color.red);
        creditB.setForeground(Color.white);;
        toolP.add(creditB);

        frame.getContentPane().add(toolP);

        canvas = new Map();
        
        canvas.setBounds(10, 10, MSIZE+1, MSIZE+1);
        
        frame.getContentPane().add(canvas);

        searchB.addActionListener(new ActionListener() {		
            @Override
            public void actionPerformed(ActionEvent e) {
                reset();
                if((startx > -1 && starty > -1) && (finishx > -1 && finishy > -1))
                    solving = true;
            }
        });
        resetB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetMap();
                Update();
            }
        });
        genMapB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateMap();
                Update();
            }
        });
        clearMapB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearMap();
                Update();
            }
        });
        algorithmsBx.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                curAlg = algorithmsBx.getSelectedIndex();
                Update();
            }
        });
        toolBx.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                tool = toolBx.getSelectedIndex();
            }
        });
        size.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                cells = size.getValue()*10;
                clearMap();
                reset();
                Update();
            }
        });
        speed.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                delay = speed.getValue();
                Update();
            }
        });
        obstacles.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                dense = (double)obstacles.getValue()/100;
                Update();
            }
        });
        creditB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame, "	                         Pathfinding\n"
                        + "            Mallela Jashwanth"
                        + "                   Java Leraner\n"
                        + "          Build Date : 24,Feb,2023  ", "Credit", JOptionPane.PLAIN_MESSAGE, new ImageIcon(""));
            }
        });

        startSearch();	
    }
    class Node {

       
        private int cellType = 0;
        private int hops;
        private int x;
        private int y;
        private int lastX;
        private int lastY;
        private double dToEnd = 0;

        public Node(int type, int x, int y) {	
            cellType = type;
            this.x = x;
            this.y = y;
            hops = -1;
        }

        public double getEuclidDist() {		
            int xdif = Math.abs(x-finishx);
            int ydif = Math.abs(y-finishy);
            dToEnd = Math.sqrt((xdif*xdif)+(ydif*ydif));
            return dToEnd;
        }
    

        public int getX() {return x;}		
        public int getY() {return y;}
        public int getLastX() {return lastX;}
        public int getLastY() {return lastY;}
        public int getType() {return cellType;}
        public int getHops() {return hops;}

        public void setType(int type) {cellType = type;}		
        public void setLastNode(int x, int y) {lastX = x; lastY = y;}
        public void setHops(int hops) {this.hops = hops;}
    }

    class Map extends JPanel implements MouseListener, MouseMotionListener{	

        public Map() {
            addMouseListener(this);
            addMouseMotionListener(this);
        }

        public void paintComponent(Graphics g) {	
            super.paintComponent(g);
            for(int x = 0; x < cells; x++) {	
                for(int y = 0; y < cells; y++) {
                    switch(map[x][y].getType()) {
                        case 0:
                            g.setColor(Color.RED);
                            break;
                        case 1:
                            g.setColor(Color.GREEN);
                            break;
                        case 2:
                            g.setColor(Color.BLACK);
                            break;
                        case 3:
                            g.setColor(Color.darkGray);
                            break;
                        case 4:
                            g.setColor(Color.CYAN);
                            break;
                        case 5:
                            g.setColor(Color.YELLOW);
                            break;
                    }
                    g.fillRect(x*CSIZE,y*CSIZE,CSIZE,CSIZE);
                    g.setColor(Color.white);
                    g.drawRect(x*CSIZE,y*CSIZE,CSIZE,CSIZE);
                   
                }
            }
        }
        public void mouseDragged(MouseEvent e) {
            try {
                int x = e.getX()/CSIZE;
                int y = e.getY()/CSIZE;
                Node current = map[x][y];
                if((tool == 2 || tool == 3) && (current.getType() != 0 && current.getType() != 1))
                    current.setType(tool);
                Update();
            } catch(Exception z) {}
        }

        @Override
        public void mouseMoved(MouseEvent e) {}

        @Override
        public void mouseClicked(MouseEvent e) {}

        @Override
        public void mouseEntered(MouseEvent e) {}

        @Override
        public void mouseExited(MouseEvent e) {}

        @Override
        public void mousePressed(MouseEvent e) {
            resetMap();	
            try {
                int x = e.getX()/CSIZE;	
                int y = e.getY()/CSIZE;
                Node current = map[x][y];
                switch(tool ) {
                    case 0: {	
                        if(current.getType()!=2) {	
                            if(startx > -1 && starty > -1) {	
                                map[startx][starty].setType(3);
                                map[startx][starty].setHops(-1);
                            }
                            current.setHops(0);
                            startx = x;	
                            starty = y;
                            current.setType(0);	
                        }
                        break;
                    }
                    case 1: {
                        if(current.getType()!=2) {	
                            if(finishx > -1 && finishy > -1)	
                                map[finishx][finishy].setType(3);
                            finishx = x;	
                            finishy = y;
                            current.setType(1);	
                        }
                        break;
                    }
                    default:
                        if(current.getType() != 0 && current.getType() != 1)
                            current.setType(tool);
                        break;
                }
                Update();
            } catch(Exception z) {}	
        }

        @Override
        public void mouseReleased(MouseEvent e) {}
    }
   
    public void startSearch() {	
        if(solving) {
            switch(curAlg) {
                case 0:
                    Alg.Dijkstra();
                    break;
                case 1:
                    Alg.AStar();
                    break;
            }
        }
        pause();	
    }

    public void pause() {	
        int i = 0;
        while(!solving) {
            i++;
            if(i > 500)
                i = 0;
            try {
                Thread.sleep(1);
            } catch(Exception e) {}
        }
        startSearch();	
    }

    public void Update() {	
        density = (cells*cells)*dense;
        CSIZE = MSIZE/cells;
        canvas.repaint();
        cellsL.setText(cells+"x"+cells);
        msL.setText(delay+"ms");
        lengthL.setText("Path Length: "+length);
        densityL.setText(obstacles.getValue()+"%");
        checkL.setText("Checks: "+checks);
    }

    public void reset() {	
        solving = false;
        length = 0;
        checks = 0;
    }

    public void delay() {	
        try {
            Thread.sleep(delay);
        } catch(Exception e) {}
    }

    class Algorithm{
    public void Dijkstra() {
        ArrayList<Node> priority = new ArrayList<Node>();	
        priority.add(map[startx][starty]);	
        while(solving) {
            if(priority.size() <= 0) {	
                solving = false;
                break;
            }
            int hops = priority.get(0).getHops()+1;	
            ArrayList<Node> explored = exploreNeighbors(priority.get(0), hops);
            if(explored.size() > 0) {
                priority.remove(0);	
                priority.addAll(explored);	
                Update();
                delay();
            } else {	
                priority.remove(0);
            }
        }
    }

    public void AStar() {
        ArrayList<Node> priority = new ArrayList<Node>();
        priority.add(map[startx][starty]);
        while(solving) {
            if(priority.size() <= 0) {
                solving = false;
                break;
            }
            int hops = priority.get(0).getHops()+1;
            ArrayList<Node> explored = exploreNeighbors(priority.get(0),hops);
            if(explored.size() > 0) {
                priority.remove(0);
                priority.addAll(explored);
                Update();
                delay();
            } else {
                priority.remove(0);
            }
            sortQue(priority);	
        }
    }

    public ArrayList<Node> sortQue(ArrayList<Node> sort) {	
        int c = 0;
        while(c < sort.size()) {
            int sm = c;
            for(int i = c+1; i < sort.size(); i++) {
                if(sort.get(i).getEuclidDist()+sort.get(i).getHops() < sort.get(sm).getEuclidDist()+sort.get(sm).getHops())
                    sm = i;
            }
            if(c != sm) {
                Node temp = sort.get(c);
                sort.set(c, sort.get(sm));
                sort.set(sm, temp);
            }
            c++;
        }
        return sort;
    }

    public ArrayList<Node> exploreNeighbors(Node current, int hops) {	
        ArrayList<Node> explored = new ArrayList<Node>();	
        for(int a = -1; a <= 1; a++) {
            for(int b = -1; b <= 1; b++) {
                int xbound = current.getX()+a;
                int ybound = current.getY()+b;
                if((xbound > -1 && xbound < cells) && (ybound > -1 && ybound < cells)) {	
                    Node neighbor = map[xbound][ybound];
                    if((neighbor.getHops()==-1 || neighbor.getHops() > hops) && neighbor.getType()!=2) {	
                        explore(neighbor, current.getX(), current.getY(), hops);	
                        explored.add(neighbor);
                }
            }
        }
        
    }
    return explored;
}

    public void explore(Node current, int lastx, int lasty, int hops) {	
        if(current.getType()!=0 && current.getType() != 1)
            current.setType(4);	
        current.setLastNode(lastx, lasty);	
        current.setHops(hops);	
        checks++;
        if(current.getType() == 1) {	
            backtrack(current.getLastX(), current.getLastY(),hops);
        }
    }


    public void backtrack(int lx, int ly, int hops) {	
        length = hops;
        while(hops > 1) {	
            Node current = map[lx][ly];
            current.setType(5);
            lx = current.getLastX();
            ly = current.getLastY();
            hops--;
        }
        solving = false;
    }
}
}
