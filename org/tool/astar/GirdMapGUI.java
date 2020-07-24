package org.tool.astar;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.List;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * 可视化操作窗口
 */
public class GirdMapGUI {

    private static final Color COLOR_PATH = new Color(0, 0, 255);
    private static final Color COLOR_POINT = new Color(255, 0, 0);

    private static final Color COLOR_BLOCK = new Color(0, 0, 0);
    private static final Color COLOR_NORMAL = new Color(117, 149, 25);

    private byte[][] map = new byte[64][40];
    private Random random = new Random();

    private JFrame frame;
    private JLabel lblMouse;
    private TileCanvas canvas;
    private JTextField txtX1, txtY1, txtX2, txtY2;

    private JTextField setX, setY;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                GirdMapGUI window = new GirdMapGUI();
                window.frame.setLocationRelativeTo(null);
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public GirdMapGUI() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setBounds(10, 10, 1370, 880);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        frame.setResizable(false);

        int x = 100, y = 5;
        JLabel lblX1 = new JLabel("起点");
        lblX1.setBounds(x, y, 30, 25);
        frame.getContentPane().add(lblX1);
        txtX1 = new JTextField("0");
        x += 35;
        txtX1.setBounds(x, y, 50, 25);
        frame.getContentPane().add(txtX1);
        x += 50;
        JLabel lblP1 = new JLabel("，");
        lblP1.setBounds(x, y, 20, 25);
        frame.getContentPane().add(lblP1);
        x += 20;
        txtY1 = new JTextField("0");
        txtY1.setBounds(x, y, 50, 25);
        frame.getContentPane().add(txtY1);

        x += 80;
        JLabel lblX2 = new JLabel("终点");
        lblX2.setBounds(x, y, 30, 25);
        frame.getContentPane().add(lblX2);
        txtX2 = new JTextField(String.valueOf(map.length - 1));
        x += 35;
        txtX2.setBounds(x, y, 50, 25);
        frame.getContentPane().add(txtX2);
        x += 50;
        JLabel lblP2 = new JLabel("，");
        lblP2.setBounds(x, y, 20, 25);
        frame.getContentPane().add(lblP2);
        x += 20;
        txtY2 = new JTextField(String.valueOf(map[0].length - 1));
        txtY2.setBounds(x, y, 50, 25);
        frame.getContentPane().add(txtY2);

        Runnable onP1 = () -> {
            lblX1.setForeground(Color.RED);
            lblX2.setForeground(Color.BLACK);
            setX = txtX1;
            setY = txtY1;
        };

        lblX1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onP1.run();
            }
        });
        Runnable onP2 = () -> {
            lblX2.setForeground(Color.RED);
            lblX1.setForeground(Color.BLACK);
            setX = txtX2;
            setY = txtY2;
        };
        lblX2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onP2.run();
            }
        });
        onP2.run();

        canvas = new TileCanvas();
        canvas.setBounds(40, 35, 1281, 801);
        canvas.setBackground(new Color(244, 244, 244));
        frame.getContentPane().add(canvas);

        x += 80;
        JButton btnFast = new JButton("最短路径");
        btnFast.addActionListener(e -> {
            canvas.findFastPath();
            canvas.repaint();
        });
        btnFast.setBounds(x, y, 100, 25);
        frame.getContentPane().add(btnFast);

        x += 110;
        JButton btnFind = new JButton("开始寻路");
        btnFind.addActionListener(e -> {
            canvas.findPath();
            canvas.repaint();
        });
        btnFind.setBounds(x, y, 100, 25);
        frame.getContentPane().add(btnFind);

        x += 110;
        JButton btnRandom = new JButton("随机寻路");
        btnRandom.addActionListener(e ->{
            txtY2.setText(String.valueOf(random.nextInt(map[0].length)));
            txtX2.setText(String.valueOf(random.nextInt(map.length)));
            canvas.findPath();
            canvas.repaint();
        });
        btnRandom.setBounds(x, y, 100, 25);
        frame.getContentPane().add(btnRandom);

        x += 110;
        JButton btnRefresh = new JButton("刷新地图");
        btnRefresh.addActionListener(e ->{
            canvas.buildMap();
            canvas.repaint();
        });
        btnRefresh.setBounds(x, y, 100, 25);
        frame.getContentPane().add(btnRefresh);

        x += 150;
        lblMouse = new JLabel("");
        lblMouse.setBounds(x, y, 150, 25);
        frame.getContentPane().add(lblMouse);

        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                onMouseEvent(e);
            }
        });
        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                onMouseEvent(e);
            }
            @Override
            public void mouseMoved(MouseEvent e) {
                onMouseLoc(e);
            }
        });
    }

    private void onMouseLoc(MouseEvent e) {
        Tile node = canvas.getNode(e.getX(), e.getY());
        if (node == null) {
            node = new Tile(0, 0);
        }
        lblMouse.setText(String.format("(%s, %s) (%s, %s)", e.getX(), e.getY(), node.x, node.y));
    }

    private void onMouseEvent(MouseEvent e) {
        onMouseLoc(e);
        Tile node = canvas.getNode(e.getX(), e.getY());
        if (node != null) {
            setX.setText(String.valueOf(node.x));
            setY.setText(String.valueOf(node.y));
        }
        canvas.findPath();
        canvas.repaint();
    }

    public class TileCanvas extends JPanel {

        int R = 20;

        AStarPath hPath = new AStarPath(map);
        public TileCanvas() {
            buildMap();
        }

        void buildMap() {
            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[i].length; j++) {
                    map[i][j] = 0;
                }
            }
            // 障碍点
            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[i].length; j++) {
                    if (random.nextFloat() < 0.2F) {
                        map[i][j] = 1;
                    }
                }
            }
            findPath();
        }

        List<Tile> path;
        int x1, y1, x2, y2;

        private void build() {
            x1 = Integer.valueOf(txtX1.getText());
            y1 = Integer.valueOf(txtY1.getText());
            x2 = Integer.valueOf(txtX2.getText());
            y2 = Integer.valueOf(txtY2.getText());
        }

        public void findPath() {
            build();
            long startTime = System.currentTimeMillis();
            path = hPath.findPath(x1, y1, x2, y2);
            System.out.println("find path use " + (System.currentTimeMillis() - startTime) + "ms");
            if (path == null) {
                System.out.println("无法通过！");
            }
        }

        public void findFastPath() {
            build();
            long startTime = System.currentTimeMillis();
            path = GirdLine.getGirdRoad(x1, y1, x2, y2);
            System.out.println("find fast path use " + (System.currentTimeMillis() - startTime) + "ms");
        }

        public void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(COLOR_NORMAL);
            g2d.fillRect(0, 0, this.getWidth(), this.getHeight());

            int maxx = Math.min (map.length, (this.getWidth() / R));
            int maxy =  Math.min (map[0].length, (this.getHeight() / R));
            // 画地图
            g2d.setColor(COLOR_PATH);
            for (int i = 0; i <= maxy; i++) {
                g2d.drawLine(0, i * R, maxx * R, i * R);
            }
            for (int j = 0; j <= maxx; j++) {
                g2d.drawLine(j * R, 0, j * R, maxy * R);
            }

            // 画障碍
            for (int i = 0; i < maxx; i++) {
                for (int j = 0; j < maxy; j++) {
                    if(map[i][j] != 0) {
                        fillNode(g2d, i, j, COLOR_BLOCK);
                    }
                }
            }

            System.out.println("地图画好啦~");

            if (path != null) {
                for (Tile node : path) {
                    fillNode(g2d, node.x, node.y, COLOR_PATH);
                }
                System.out.println("路径画好啦~");
            }


            fillNode(g2d, x1, y1, COLOR_POINT);
            fillNode(g2d, x2, y2, COLOR_POINT);
        }

        void fillNode(Graphics2D g2d, int x, int y, Color color) {
            if(x > this.getWidth() / R || y > this.getHeight() / R) {
                return;
            }
            g2d.setColor(color);
            g2d.fillRect(x * R, y * R, R, R);
        }

        Tile getNode(double x, double y) {
            int tx = (int) ((x) / R);
            int ty = (int) ((y) / R);
            if (tx < 0 || ty < 0 || tx >= map.length || ty >= map[0].length) {
                return null;
            }
            return new Tile(tx, ty);
        }

    }

}