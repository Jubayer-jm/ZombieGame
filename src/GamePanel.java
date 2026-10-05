import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
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

    private int playerHealth = 100;
    private int damageCooldown = 0;
    private boolean gameOver = false;

    private final List<Zombie> zombies = new ArrayList<>();
    private final List<Bullet> bullets = new ArrayList<>();
    private int tickCount = 0;
    private int score = 0;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(30, 30, 30));
        setFocusable(true);

        addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e)  { keysDown.add(e.getKeyCode()); }
            @Override public void keyReleased(KeyEvent e) { keysDown.remove(e.getKeyCode()); }
        });

        addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                if (!gameOver) {
                    shoot(e.getX(), e.getY());
                }
            }
        });

        timer = new Timer(16, e -> {
            update();
            repaint();
        });
    }

    public void start() {
        timer.start();
    }

    private void shoot(double targetX, double targetY) {
        double startX = playerX + playerSize / 2.0;
        double startY = playerY + playerSize / 2.0;
        bullets.add(new Bullet(startX, startY, targetX, targetY));
    }

    private void update() {
        if (gameOver) return;

        movePlayer();

        tickCount++;
        if (tickCount % 120 == 0 && zombies.size() < 10) {
            spawnZombie();
        }

        if (damageCooldown > 0) damageCooldown--;

        for (Zombie z : zombies) {
            z.update();
            z.moveToward(playerX, playerY);

            double dist = Math.hypot(z.getX() - playerX, z.getY() - playerY);
            if (dist < 22 && damageCooldown == 0) {
                playerHealth = Math.max(0, playerHealth - z.getDamage());
                damageCooldown = 30;
            }
        }

        updateBullets();

        int before = zombies.size();
        zombies.removeIf(Zombie::isDead);
        score += (before - zombies.size()) * 10;

        if (playerHealth <= 0) {
            gameOver = true;
        }
    }

    private void updateBullets() {
        Iterator<Bullet> it = bullets.iterator();
        while (it.hasNext()) {
            Bullet b = it.next();
            b.update();

            if (b.isOffscreen()) {
                it.remove();
                continue;
            }

            for (Zombie z : zombies) {
                Rectangle zombieBox = new Rectangle((int) z.getX(), (int) z.getY(), 22, 22);
                if (zombieBox.contains(b.getX(), b.getY())) {
                    z.takeDamage(b.getDamage());
                    it.remove();
                    break;
                }
            }
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

        if (random.nextInt(100) < 30) {
            zombies.add(new Runner(x, y));
        } else {
            zombies.add(new Walker(x, y));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        for (Zombie z : zombies) {
            z.draw(g2);
        }

        for (Bullet b : bullets) {
            b.draw(g2);
        }

        g2.setColor(new Color(60, 140, 255));
        g2.fillRect((int) playerX, (int) playerY, playerSize, playerSize);

        g2.setColor(Color.WHITE);
        g2.drawString("Move: W A S D   Shoot: mouse click", 10, 20);
        g2.drawString("Zombies: " + zombies.size() + "   Score: " + score, 10, 38);

        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(10, 50, 150, 14);
        g2.setColor(new Color(220, 50, 50));
        g2.fillRect(10, 50, playerHealth * 150 / 100, 14);
        g2.setColor(Color.WHITE);
        g2.drawString("HP: " + playerHealth, 170, 62);

        if (gameOver) {
            g2.setFont(new Font("SansSerif", Font.BOLD, 48));
            g2.setColor(Color.RED);
            g2.drawString("GAME OVER", WIDTH / 2 - 130, HEIGHT / 2);
        }
    }
}