public abstract class Entity {
    protected double x, y, speed;
    private int health;

    public Entity(double x, double y, int health, double speed) {
        this.x = x;
        this.y = y;
        this.health = health;
        this.speed = speed;
    }

    public void takeDamage(int dmg) {
        health = Math.max(0, health - dmg);
    }

    public boolean isDead() {
        return health <= 0;
    }

    public int getHealth() {
        return health;
    }

    public double getX() { return x; }
    public double getY() { return y; }

    public abstract void update();
}