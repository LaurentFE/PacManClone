package fr.LaurentFE.pacManClone;

import java.awt.*;

public class Pellet {
    private final Point tileIndex;
    private final int score;
    private final boolean isPowerPellet;

    public Pellet(int x, int y, boolean isPowerPellet) {
        tileIndex = new Point(x, y);
        score = (isPowerPellet)?50:10;
        this.isPowerPellet = isPowerPellet;
    }

    public Point getTileIndex() {
        return tileIndex;
    }

    public int getScore() {
        return score;
    }

    public boolean isPowerPellet() {
        return isPowerPellet;
    }
}