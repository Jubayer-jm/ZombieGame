public class Weapon extends Item {
    private final int damage;
    private final int cooldownTicks;
    private int ammo;

    public Weapon(String name, int damage, int cooldownTicks, int ammo) {
        super(name);
        this.damage = damage;
        this.cooldownTicks = cooldownTicks;
        this.ammo = ammo;
    }

    public boolean hasAmmo() {
        return ammo > 0;
    }

    public void useAmmo() {
        if (ammo > 0) {
            ammo--;
        }
    }

    public void addAmmo(int amount) {
        ammo += amount;
    }

    public int getDamage() { return damage; }
    public int getCooldownTicks() { return cooldownTicks; }
    public int getAmmo() { return ammo; }
}