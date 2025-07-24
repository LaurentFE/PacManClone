package fr.LaurentFE.pacManClone.ghost.personality;

import fr.LaurentFE.pacManClone.GamePanel;
import fr.LaurentFE.pacManClone.Orientation;

import java.awt.*;

public class Inky implements GhostPersonality {

    public Inky() {}

    @Override
    public Orientation getNextMovementOrientation() {
        Point pacManTile = GamePanel.PAC_MAN.getPosition();
        pacManTile.x = pacManTile.x / GamePanel.TILE_SIZE;
        pacManTile.y = pacManTile.y / GamePanel.TILE_SIZE;
        switch (GamePanel.PAC_MAN.getOrientation()) {
            case UP -> pacManTile.y -= 2;
            case LEFT -> pacManTile.x -= 2;
            case DOWN -> pacManTile.y += 2;
            case RIGHT -> pacManTile.x += 2;
        }
        Point blinkyTile = GamePanel.BLINKY.getPosition();
        blinkyTile.x = pacManTile.x / GamePanel.TILE_SIZE;
        blinkyTile.y = pacManTile.y / GamePanel.TILE_SIZE;
        Point targetTile = new Point(
                -(blinkyTile.x - pacManTile.x),
                -(blinkyTile.y - pacManTile.y));


        Point nextMoveTile = GamePanel.INKY.getNextMoveTile(targetTile);


        return GamePanel.INKY.getOrientationToGoToTile(nextMoveTile);
    }
}
