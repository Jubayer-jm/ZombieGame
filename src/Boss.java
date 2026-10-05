import java.awt.Color;
import java.awt.Graphics2D;

public class Boss extends Zombie {
    private final int size = 50;

    public Boss(double x, double y) {
        super(x, y, 300, 0.7, 20);
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public void update() {
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(new Color(170, 40, 40));
        g.fillRect((int) x, (int) y, size, size);

        g.setColor(Color.WHITE);
        g.fillRect((int) x + 10, (int) y + 14, 8, 8);
        g.fillRect((int) x + 32, (int) y + 14, 8, 8);
    }
}