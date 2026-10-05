import java.awt.Color;
import java.awt.Graphics2D;

public class Walker extends Zombie {
    private final int size = 22;

    public Walker(double x, double y) {
        super(x, y, 50, 1.0, 5);
    }

    @Override
    public void update() {
        // movement is handled by the game panel using moveToward()
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(new Color(80, 180, 80));
        g.fillRect((int) x, (int) y, size, size);
    }
}