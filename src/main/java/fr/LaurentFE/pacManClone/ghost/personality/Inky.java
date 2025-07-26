package fr.LaurentFE.pacManClone.ghost.personality;

import fr.LaurentFE.pacManClone.GamePanel;
import fr.LaurentFE.pacManClone.Orientation;
import fr.LaurentFE.pacManClone.TileIndex;

public class Inky implements GhostPersonality {

    public Inky() {}

    @Override
    public Orientation getNextMovementOrientation() {
        TileIndex pacManTile = GamePanel.PAC_MAN.getPosition().toTileIndex();
        switch (GamePanel.PAC_MAN.getOrientation()) {
            case UP -> pacManTile.y -= 2;
            case LEFT -> pacManTile.x -= 2;
            case DOWN -> pacManTile.y += 2;
            case RIGHT -> pacManTile.x += 2;
        }
        TileIndex blinkyTile = GamePanel.BLINKY.getPosition().toTileIndex();
        TileIndex targetTile = new TileIndex(
                blinkyTile.x - 2*(blinkyTile.x- pacManTile.x),
                blinkyTile.y - 2*(blinkyTile.y- pacManTile.y));

        TileIndex nextMoveTile = GamePanel.INKY.getNextMoveTile(new TileIndex(targetTile.x, targetTile.y));

        return GamePanel.INKY.getOrientationToGoToTile(nextMoveTile);
    }
}
