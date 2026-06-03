import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class SnakeGame extends JFrame {
    public SnakeGame() {
        setTitle("贪吃蛇游戏");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        add(new GamePanel());
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SnakeGame());
    }
}

class GamePanel extends JPanel {
    private static final int WIDTH = 400;
    private static final int HEIGHT = 400;
    private static final int UNIT_SIZE = 20;
    private static final int GAME_UNITS = (WIDTH * HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    private static final int DELAY = 75;

    private int[] snakeX = new int[GAME_UNITS];
    private int[] snakeY = new int[GAME_UNITS];
    private int snakeLength = 3;
    private int foodX;
    private int foodY;
    private int score = 0;
    private char direction = 'R';
    private char nextDirection = 'R';
    private boolean running = true;
    private Random random;
    private Timer timer;

    public GamePanel() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        if (direction != 'R') nextDirection = 'L';
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (direction != 'L') nextDirection = 'R';
                        break;
                    case KeyEvent.VK_UP:
                        if (direction != 'D') nextDirection = 'U';
                        break;
                    case KeyEvent.VK_DOWN:
                        if (direction != 'U') nextDirection = 'D';
                        break;
                }
            }
        });

        random = new Random();
        initGame();
        timer = new Timer(DELAY, e -> update());
        timer.start();
    }

    private void initGame() {
        // 初始化蛇的位置
        snakeX[0] = 100;
        snakeY[0] = 100;
        snakeX[1] = 80;
        snakeY[1] = 100;
        snakeX[2] = 60;
        snakeY[2] = 100;
        generateFood();
    }

    private void generateFood() {
        foodX = random.nextInt(WIDTH / UNIT_SIZE) * UNIT_SIZE;
        foodY = random.nextInt(HEIGHT / UNIT_SIZE) * UNIT_SIZE;
    }

    private void update() {
        if (!running) {
            timer.stop();
            return;
        }

        direction = nextDirection;

        // 移动蛇身
        for (int i = snakeLength; i > 0; i--) {
            snakeX[i] = snakeX[i - 1];
            snakeY[i] = snakeY[i - 1];
        }

        // 根据方向更新蛇头位置
        switch (direction) {
            case 'L':
                snakeX[0] -= UNIT_SIZE;
                break;
            case 'R':
                snakeX[0] += UNIT_SIZE;
                break;
            case 'U':
                snakeY[0] -= UNIT_SIZE;
                break;
            case 'D':
                snakeY[0] += UNIT_SIZE;
                break;
        }

        // 检查碰撞（墙壁）
        if (snakeX[0] < 0 || snakeX[0] >= WIDTH || snakeY[0] < 0 || snakeY[0] >= HEIGHT) {
            running = false;
        }

        // 检查碰撞（自己）
        for (int i = 1; i < snakeLength; i++) {
            if (snakeX[0] == snakeX[i] && snakeY[0] == snakeY[i]) {
                running = false;
            }
        }

        // 检查是否吃到食物
        if (snakeX[0] == foodX && snakeY[0] == foodY) {
            snakeLength++;
            score += 10;
            generateFood();
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 绘制蛇
        g2d.setColor(Color.GREEN);
        for (int i = 0; i < snakeLength; i++) {
            g2d.fillRect(snakeX[i], snakeY[i], UNIT_SIZE, UNIT_SIZE);
        }

        // 绘制食物
        g2d.setColor(Color.RED);
        g2d.fillOval(foodX, foodY, UNIT_SIZE, UNIT_SIZE);

        // 绘制分数
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString("Score: " + score, 10, 20);

        // 如果游戏结束，显示提示
        if (!running) {
            g2d.setColor(new Color(0, 0, 0, 200));
            g2d.fillRect(0, 0, WIDTH, HEIGHT);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 40));
            g2d.drawString("Game Over", 80, 180);
            g2d.setFont(new Font("Arial", Font.BOLD, 20));
            g2d.drawString("Final Score: " + score, 130, 220);
        }
    }
}
