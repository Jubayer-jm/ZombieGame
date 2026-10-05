import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private final List<Item> items = new ArrayList<>();
    private int currentWeaponIndex = 0;

    public void add(Item item) {
        items.add(item);
    }

    public List<Weapon> getWeapons() {
        List<Weapon> weapons = new ArrayList<>();
        for (Item item : items) {
            if (item instanceof Weapon) {
                weapons.add((Weapon) item);
            }
        }
        return weapons;
    }

    public Weapon getCurrentWeapon() {
        List<Weapon> weapons = getWeapons();
        if (weapons.isEmpty()) return null;
        return weapons.get(currentWeaponIndex % weapons.size());
    }

    public void nextWeapon() {
        List<Weapon> weapons = getWeapons();
        if (!weapons.isEmpty()) {
            currentWeaponIndex = (currentWeaponIndex + 1) % weapons.size();
        }
    }

    public int getMedKitCount() {
        int count = 0;
        for (Item item : items) {
            if (item instanceof MedKit) count++;
        }
        return count;
    }

    public MedKit takeMedKit() {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i) instanceof MedKit) {
                return (MedKit) items.remove(i);
            }
        }
        return null;
    }
}