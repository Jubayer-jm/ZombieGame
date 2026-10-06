import java.awt.Graphics2D;
import java.awt.Rectangle;

public abstract class Zombie extends Entity {
    protected int damage;

    public Zombie(double x, double y, int health, double speed, int damage) {
        super(x, y, health, speed);
        this.damage = damage;
    }

    public void moveToward(double targetX, double targetY) {
        double dx = targetX - x;
        double dy = targetY - y;
        double dist = Math.hypot(dx, dy);
        if (dist > 1) {
            x += speed * dx / dist;
            y += speed * dy / dist;
        }
    }

    public void setPosition(double newX, double newY) {
        this.x = newX;
        this.y = newY;
    }

    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, getSize(), getSize());
    }

    public int getDamage() {
        return damage;
    }

    public int getSize() {
        return 22;
    }

    public abstract void draw(Graphics2D g);
}