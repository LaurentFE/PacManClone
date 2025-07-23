package fr.LaurentFE.pacManClone.ghost.personality;

import fr.LaurentFE.pacManClone.GamePanel;
import fr.LaurentFE.pacManClone.Orientation;

public class Inky implements GhostPersonality {

    public Inky() {}

    @Override
    public Orientation getNextMovementOrientation() {
        return switch (GamePanel.BLINKY.getState()) {
            case CHASE -> getNextChaseMovementOrientation();
            case SCATTER -> getNextScatterMovementOrientation();
            case FRIGHTENED -> getNextFrightenedMovementOrientation();
            case EATEN -> getNextEatenMovementOrientation();
        };
    }

    private Orientation getNextChaseMovementOrientation() {
        return null;
    }

    private Orientation getNextScatterMovementOrientation() {
        return null;
    }

    private Orientation getNextFrightenedMovementOrientation() {
        return null;
    }

    private Orientation getNextEatenMovementOrientation() {
        return null;
    }
}
