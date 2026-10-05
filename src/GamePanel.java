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
    private static final int MAX_HEALTH = 100;

    private final Set<Integer> keysDown = new HashSet<>();
    private final Timer timer;
    private final Random random = new Random();

    private double playerX = WIDTH / 2.0;
    private double playerY = HEIGHT / 2.0;
    private final int playerSize = 24;
    private final double playerSpeed = 4;

    private int playerHealth = MAX_HEALTH;
    private int damageCooldown = 0;
    private int shootCooldown = 0;
    private boolean gameOver = false;
    private boolean gameWon = false;

    private Inventory inventory = new Inventory();

    private final List<Zombie> zombies = new ArrayList<>();
    private final List<Bullet> bullets = new ArrayList<>();
    private int score = 0;

    private int waveNumber = 1;
    private int walkersLeft;
    private int runnersLeft;
    private int bossesLeft;
    private int spawnTimer = 0;
    private int breakTimer = 0;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(30, 30, 30));
        setFocusable(true);

        setupInventory();

        addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                boolean firstPress = keysDown.add(code);
                if (firstPress && !gameOver && !gameWon) {
                    if (code == KeyEvent.VK_E) inventory.nextWeapon();
                    if (code == KeyEvent.VK_H) useMedKit();
                }
                if (firstPress && (gameOver || gameWon) && code == KeyEvent.VK_R) {
                    restartGame();
                }
            }
            @Override public void keyReleased(KeyEvent e) { keysDown.remove(e.getKeyCode()); }
        });

        addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                if (!gameOver && !gameWon) {
                    shoot(e.getX(), e.getY());
                }
            }
        });

        startWave();

        timer = new Timer(16, e -> {
            update();
            repaint();
        });
    }

    private void setupInventory() {
        inventory = new Inventory();
        inventory.add(new Weapon("Pistol", 25, 12, 80));
        inventory.add(new Weapon("Rifle", 50, 30, 40));
        inventory.add(new MedKit(40));
        inventory.add(new MedKit(40));
    }

    private void restartGame() {
        playerX = WIDTH / 2.0;
        playerY = HEIGHT / 2.0;
        playerHealth = MAX_HEALTH;
        damageCooldown = 0;
        shootCooldown = 0;
        gameOver = false;
        gameWon = false;
        zombies.clear();
        bullets.clear();
        score = 0;
        waveNumber = 1;
        breakTimer = 0;
        setupInventory();
        startWave();
    }

    public void start() {
        timer.start();
    }

    private void startWave() {
        Wave wave = Wave.forNumber(waveNumber);
        walkersLeft = wave.getWalkers();
        runnersLeft = wave.getRunners();
        bossesLeft = wave.getBosses();
        spawnTimer = 0;
    }

    private void shoot(double targetX, double targetY) {
        Weapon weapon = inventory.getCurrentWeapon();
        if (weapon == null || !weapon.hasAmmo() || shootCooldown > 0) {
            return;
        }
        double startX = playerX + playerSize / 2.0;
        double startY = playerY + playerSize / 2.0;
        bullets.add(new Bullet(startX, startY, targetX, targetY, weapon.getDamage()));
        weapon.useAmmo();
        shootCooldown = weapon.getCooldownTicks();
    }

    private void useMedKit() {
        if (playerHealth < MAX_HEALTH && inventory.getMedKitCount() > 0) {
            MedKit kit = inventory.takeMedKit();
            if (kit != null) {
                playerHealth = Math.min(MAX_HEALTH, playerHealth + kit.getHealAmount());
            }
        }
    }

    private void giveWaveReward() {
        for (Weapon w : inventory.getWeapons()) {
            w.addAmmo(15);
        }
        inventory.add(new MedKit(40));
    }

    private void update() {
        if (gameOver || gameWon) return;

        movePlayer();
        updateWave();

        if (damageCooldown > 0) damageCooldown--;
        if (shootCooldown > 0) shootCooldown--;

        for (Zombie z : zombies) {
            z.update();
            z.moveToward(playerX, playerY);

            double zx = z.getX() + z.getSize() / 2.0;
            double zy = z.getY() + z.getSize() / 2.0;
            double px = playerX + playerSize / 2.0;
            double py = playerY + playerSize / 2.0;
            double touchDist = (z.getSize() + playerSize) / 2.0;

            if (Math.hypot(zx - px, zy - py) < touchDist && damageCooldown == 0) {
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

    private void updateWave() {
        if (breakTimer > 0) {
            breakTimer--;
            if (breakTimer == 0) {
                waveNumber++;
                startWave();
            }
            return;
        }

        int left = walkersLeft + runnersLeft + bossesLeft;

        spawnTimer++;
        if (spawnTimer >= 60 && left > 0) {
            spawnZombie();
            spawnTimer = 0;
        }

        if (left == 0 && zombies.isEmpty()) {
            if (waveNumber >= Wave.FINAL_WAVE) {
                gameWon = true;
            } else {
                giveWaveReward();
                breakTimer = 180;
            }
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
                Rectangle zombieBox = new Rectangle(
                        (int) z.getX(), (int) z.getY(), z.getSize(), z.getSize());
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
        do {
            switch (random.nextInt(4)) {
                case 0:  x = random.nextInt(WIDTH - 50); y = 0; break;
                case 1:  x = random.nextInt(WIDTH - 50); y = HEIGHT - 50; break;
                case 2:  x = 0; y = random.nextInt(HEIGHT - 50); break;
                default: x = WIDTH - 50; y = random.nextInt(HEIGHT - 50); break;
            }
        } while (Math.hypot(x - playerX, y - playerY) < 150);

        int normalLeft = walkersLeft + runnersLeft;

        if (normalLeft > 0) {
            boolean makeRunner = runnersLeft > 0
                    && (walkersLeft == 0 || random.nextInt(normalLeft) < runnersLeft);
            if (makeRunner) {
                zombies.add(new Runner(x, y));
                runnersLeft--;
            } else {
                zombies.add(new Walker(x, y));
                walkersLeft--;
            }
        } else {
            zombies.add(new Boss(x, y));
            bossesLeft--;
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

        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g2.setColor(Color.WHITE);
        g2.drawString("Move: WASD   Shoot: click   Switch weapon: E   Heal: H", 10, 20);

        String waveText = "Wave: " + waveNumber;
        if (waveNumber >= Wave.FINAL_WAVE) waveText += " (BOSS WAVE)";
        g2.drawString(waveText + "   Zombies: " + zombies.size()
                + "   Score: " + score, 10, 38);

        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(10, 50, 150, 14);
        g2.setColor(new Color(220, 50, 50));
        g2.fillRect(10, 50, playerHealth * 150 / MAX_HEALTH, 14);
        g2.setColor(Color.WHITE);
        g2.drawString("HP: " + playerHealth, 170, 62);

        Weapon weapon = inventory.getCurrentWeapon();
        if (weapon != null) {
            String ammoText = weapon.getName() + "   Ammo: " + weapon.getAmmo();
            if (!weapon.hasAmmo()) ammoText += "  (EMPTY - press E)";
            g2.drawString(ammoText, 10, 82);
        }
        g2.drawString("MedKits: " + inventory.getMedKitCount(), 10, 100);

        if (breakTimer > 0) {
            g2.setFont(new Font("SansSerif", Font.BOLD, 30));
            g2.setColor(new Color(120, 220, 120));
            g2.drawString("Wave " + waveNumber + " cleared!", WIDTH / 2 - 130, HEIGHT / 2 - 10);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 18));
            g2.drawString("+15 ammo, +1 MedKit", WIDTH / 2 - 85, HEIGHT / 2 + 20);
        }

        if (gameWon) {
            g2.setFont(new Font("SansSerif", Font.BOLD, 56));
            g2.setColor(new Color(255, 215, 0));
            g2.drawString("YOU WIN!", WIDTH / 2 - 120, HEIGHT / 2);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 22));
            g2.setColor(Color.WHITE);
            g2.drawString("Final score: " + score, WIDTH / 2 - 70, HEIGHT / 2 + 40);
            g2.drawString("Press R to restart", WIDTH / 2 - 85, HEIGHT / 2 + 70);
        }

        if (gameOver) {
            g2.setFont(new Font("SansSerif", Font.BOLD, 48));
            g2.setColor(Color.RED);
            g2.drawString("GAME OVER", WIDTH / 2 - 130, HEIGHT / 2);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 22));
            g2.setColor(Color.WHITE);
            g2.drawString("Press R to restart", WIDTH / 2 - 85, HEIGHT / 2 + 40);
        }
    }
}