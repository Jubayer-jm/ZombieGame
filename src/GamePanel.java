import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

public class GamePanel extends JPanel {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    private final Set<Integer> keysDown = new HashSet<>();
    private final Timer timer;

    private double playerX = WIDTH / 2.0;
    private double playerY = HEIGHT / 2.0;
    private final int playerSize = 24;
    private final double playerSpeed = 4;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(30, 30, 30));
        setFocusable(true);

        addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e)  { keysDown.add(e.getKeyCode()); }
            @Override public void keyReleased(KeyEvent e) { keysDown.remove(e.getKeyCode()); }
        });

        timer = new Timer(16, e -> {
            update();
            repaint();
        });
    }

    public void start() {
        timer.start();
    }

    private void update() {
        double dx = 0, dy = 0;
        if (keysDown.contains(KeyEvent.VK_W)) dy -= 1;
        if (keysDown.contains(KeyEvent.VK_S)) dy += 1;
        if (keysDown.contains(KeyEvent.VK_A)) dx -= 1;
        if (keysDown.contains(KeyEvent.VK_D)) dx += 1;

        if (dx != 0 && dy != 0) {
            dx *= 0.7071;
            dy *= 0.7071;
        }

        playerX += dx * playerSpeed;
        playerY += dy * playerSpeed;

        playerX = Math.max(0, Math.min(WIDTH - playerSize, playerX));
        playerY = Math.max(0, Math.min(HEIGHT - playerSize, playerY));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(new Color(60, 140, 255));
        g2.fillRect((int) playerX, (int) playerY, playerSize, playerSize);

        g2.setColor(Color.WHITE);
        g2.drawString("Move: W A S D", 10, 20);
    }
}