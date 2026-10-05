import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class GamePanel extends JPanel {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    private final Set<Integer> keysDown = new HashSet<>();
    private final Timer timer;
    private final Random random = new Random();

    private double playerX = WIDTH / 2.0;
    private double playerY = HEIGHT / 2.0;
    private final int playerSize = 24;
    private final double playerSpeed = 4;

    private final List<Zombie> zombies = new ArrayList<>();
    private int tickCount = 0;

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
        movePlayer();

        tickCount++;
        if (tickCount % 120 == 0 && zombies.size() < 10) {
            spawnZombie();
        }

        for (Zombie z : zombies) {
            z.update();
            z.moveToward(playerX, playerY);
        }
    }

    private void movePlayer() {
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

    private void spawnZombie() {
        double x, y;
        switch (random.nextInt(4)) {
            case 0:  x = random.nextInt(WIDTH); y = 0; break;
            case 1:  x = random.nextInt(WIDTH); y = HEIGHT; break;
            case 2:  x = 0; y = random.nextInt(HEIGHT); break;
            default: x = WIDTH; y = random.nextInt(HEIGHT); break;
        }
        zombies.add(new Walker(x, y));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        for (Zombie z : zombies) {
            z.draw(g2);
        }

        g2.setColor(new Color(60, 140, 255));
        g2.fillRect((int) playerX, (int) playerY, playerSize, playerSize);

        g2.setColor(Color.WHITE);
        g2.drawString("Move: W A S D", 10, 20);
        g2.drawString("Zombies: " + zombies.size(), 10, 38);
    }
}