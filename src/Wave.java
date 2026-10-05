public class Wave {
    public static final int FINAL_WAVE = 5;

    private final int walkers;
    private final int runners;
    private final int bosses;

    public Wave(int walkers, int runners, int bosses) {
        this.walkers = walkers;
        this.runners = runners;
        this.bosses = bosses;
    }

    public int getWalkers() { return walkers; }
    public int getRunners() { return runners; }
    public int getBosses() { return bosses; }

    public int getTotal() {
        return walkers + runners + bosses;
    }

    public static Wave forNumber(int waveNumber) {
        if (waveNumber >= FINAL_WAVE) {
            return new Wave(6, 3, 1);
        }
        int walkers = 3 + waveNumber * 2;
        int runners = waveNumber - 1;
        return new Wave(walkers, runners, 0);
    }
}