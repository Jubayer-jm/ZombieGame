public class MedKit extends Item {
    private final int healAmount;

    public MedKit(int healAmount) {
        super("MedKit");
        this.healAmount = healAmount;
    }

    public int getHealAmount() {
        return healAmount;
    }
}