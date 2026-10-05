import java.awt.Color;
import java.awt.Graphics2D;

public class Bullet {
    private double x, y;
    private final double vx, vy;
    private final int damage;

    public Bullet(double x, double y, double targetX, double targetY, int damage) {
        this.x = x;
        this.y = y;
        this.damage = damage;
        double dx = targetX - x;
        double dy = targetY - y;
        double dist = Math.hypot(dx, dy);
        double speed = 10;
        if (dist == 0) dist = 1;
        this.vx = speed * dx / dist;
        this.vy = speed * dy / dist;
    }

    public void update() {
        x += vx;
        y += vy;
    }

    public boolean isOffscreen() {
        return x < 0 || x > GamePanel.WIDTH || y < 0 || y > GamePanel.HEIGHT;
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.YELLOW);
        g.fillOval((int) x - 3, (int) y - 3, 6, 6);
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public int getDamage() { return damage; }
}