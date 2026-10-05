import java.awt.Graphics2D;

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

    public int getDamage() {
        return damage;
    }

    public abstract void draw(Graphics2D g);
}