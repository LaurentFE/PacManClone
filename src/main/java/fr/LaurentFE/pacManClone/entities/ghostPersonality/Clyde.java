package fr.LaurentFE.pacManClone.entities.ghostPersonality;

import fr.LaurentFE.pacManClone.GamePanel;
import fr.LaurentFE.pacManClone.entities.Orientation;
import fr.LaurentFE.pacManClone.map.TileIndex;


public class Clyde implements GhostPersonality {

    public Clyde() {}

    @Override
    public Orientation getNextMovementOrientation() {
        if (getSquareDistanceFromPacMan() >= 64) {
            TileIndex targetTile = GamePanel.PAC_MAN.getPosition().toTileIndex();
            TileIndex nextMoveTile = GamePanel.CLYDE.getNextMoveTile(targetTile);

            return GamePanel.CLYDE.getOrientationToGoToTile(nextMoveTile);
        } else {
            return GamePanel.CLYDE.getNextScatterMovementOrientation();
        }
    }

    private int getSquareDistanceFromPacMan() {
        TileIndex pacManTile = GamePanel.PAC_MAN.getPosition().toTileIndex();
        TileIndex clydeTile = GamePanel.CLYDE.getPosition().toTileIndex();

        return (pacManTile.x - clydeTile.x)*(pacManTile.x - clydeTile.x)
                + (pacManTile.y - clydeTile.y)*(pacManTile.y - clydeTile.y);
    }
}
