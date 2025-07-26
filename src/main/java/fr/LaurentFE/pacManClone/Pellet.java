package fr.LaurentFE.pacManClone;

public class Pellet {
    private final TileIndex tileIndex;
    private final int score;
    private final boolean isPowerPellet;

    public Pellet(int x, int y, boolean isPowerPellet) {
        tileIndex = new TileIndex(x, y);
        score = (isPowerPellet)?50:10;
        this.isPowerPellet = isPowerPellet;
    }

    public TileIndex getTileIndex() {
        return tileIndex;
    }

    public int getScore() {
        return score;
    }

    public boolean isPowerPellet() {
        return isPowerPellet;
    }
}