import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Barricade {
    private static final int MAX_HEALTH = 100;

    private final int x, y;
    private final int width = 56;
    private final int height = 16;
    private int health = MAX_HEALTH;

    public Barricade(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void takeDamage(int dmg) {
        health = Math.max(0, health - dmg);
    }

    public boolean isDestroyed() {
        return health <= 0;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public void draw(Graphics2D g) {
        g.setColor(new Color(139, 90, 43));
        g.fillRect(x, y, width, height);

        g.setColor(new Color(90, 55, 25));
        g.drawRect(x, y, width, height);

        // health bar above the barricade
        g.setColor(Color.DARK_GRAY);
        g.fillRect(x, y - 6, width, 4);
        g.setColor(new Color(240, 200, 60));
        g.fillRect(x, y - 6, width * health / MAX_HEALTH, 4);
    }
}