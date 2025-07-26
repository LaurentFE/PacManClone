package fr.LaurentFE.pacManClone.map;

import fr.LaurentFE.pacManClone.GamePanel;

public class TileIndex {
    public int x;
    public int y;

    public TileIndex(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Position toPosition() {
        return new Position(x* GamePanel.TILE_SIZE, y*GamePanel.TILE_SIZE);
    }

    public String toString() {
        return "TileIndex["+x+", "+y+"]";
    }

    public TileIndex add(TileIndex ti) {
        x += ti.x;
        y += ti.y;
        return this;
    }
}
