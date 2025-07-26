package fr.LaurentFE.pacManClone;

public class Position {
    public int x;
    public int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public TileIndex toTileIndex() {
        return new TileIndex(x/GamePanel.TILE_SIZE, y/GamePanel.TILE_SIZE);
    }

    public String toString() {
        return "Position["+x+", "+y+"]";
    }

    public Position add(Position p) {
        x += p.x;
        y += p.y;
        return this;
    }
}
