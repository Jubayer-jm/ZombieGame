public class Wave {
    private final int walkers;
    private final int runners;

    public Wave(int walkers, int runners) {
        this.walkers = walkers;
        this.runners = runners;
    }

    public int getWalkers() { return walkers; }
    public int getRunners() { return runners; }

    public int getTotal() {
        return walkers + runners;
    }

    public static Wave forNumber(int waveNumber) {
        int walkers = 3 + waveNumber * 2;
        int runners = waveNumber - 1;
        return new Wave(walkers, runners);
    }
}