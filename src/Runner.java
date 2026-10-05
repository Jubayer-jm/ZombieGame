import java.awt.Color;
import java.awt.Graphics2D;

public class Runner extends Zombie {
    private final int size = 18;

    public Runner(double x, double y) {
        super(x, y, 25, 2.2, 8);
    }

    @Override
    public void update() {
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(new Color(220, 130, 40));
        g.fillRect((int) x, (int) y, size, size);
    }
}