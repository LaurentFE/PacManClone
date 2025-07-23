package fr.LaurentFE.pacManClone;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class GameMap {
    private static GameMap INSTANCE;
    private final int mapHeightTile;
    private final int mapWidthTile;
    private final TileType[][] map;
    private boolean usable;
    private Point ghostHouse;

    private GameMap() {
        mapHeightTile = 36;
        mapWidthTile = 28;
        map = new TileType[mapHeightTile][mapWidthTile];
        usable = false;
    }

    public static GameMap getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GameMap();
        }
        return INSTANCE;
    }

    public void loadMap(String filePath) {
        try {
            usable=loadTileMap(filePath);
        } catch (IOException e) {
            System.err.println("Couldn't read file '"+filePath+"'");
        }
    }

    private boolean loadTileMap(String filePath) throws IOException {
        Path tileMapFilePath = Path.of(filePath);
        List<String> tileMap = Files.readAllLines(tileMapFilePath);
        return convertTileMap(tileMap);
    }

    private boolean convertTileMap(List<String> tileMap) {
        for (int y = 0; y < mapHeightTile; y++) {
            for (int x = 0; x < mapWidthTile; x++) {
                if (y >= tileMap.size() || x >= tileMap.get(y).length()) {
                    System.err.println("Provided tile map size ("+tileMap.get(y).length()+"*"+tileMap.size()+" " +
                            "doesn't coincide with mapWidthTile("+mapWidthTile+") " +
                            "or mapHeightTile("+mapHeightTile+") requirements on line "+y);
                    return false;
                }
                char tile = tileMap.get(y).charAt(x);
                map[y][x] = switch(tile) {
                    case '.' -> TileType.PATH;
                    case '=' -> TileType.DOUBLEHORIZONTALWALL;
                    case 'V' -> TileType.DOUBLEVERTICALWALL;
                    case 'A' -> TileType.DOUBLEOUTERDOWNRIGHTCORNER;
                    case 'B' -> TileType.DOUBLEOUTERDOWNLEFTCORNER;
                    case 'C' -> TileType.DOUBLEOUTERUPLEFTCORNER;
                    case 'D' -> TileType.DOUBLEOUTERUPRIGHTCORNER;
                    case 'E' -> TileType.DOUBLEINNERDOWNRIGHTCORNER;
                    case 'F' -> TileType.DOUBLEINNERDOWNLEFTCORNER;
                    case 'G' -> TileType.DOUBLEINNERUPLEFTCORNER;
                    case 'H' -> TileType.DOUBLEINNERUPRIGHTCORNER;
                    case '-' -> TileType.SIMPLEHORIZONTALWALL;
                    case '|' -> TileType.SIMPLEVERTICALWALL;
                    case 'a' -> TileType.SIMPLEOUTERDOWNRIGHTCORNER;
                    case 'b' -> TileType.SIMPLEOUTERDOWNLEFTCORNER;
                    case 'c' -> TileType.SIMPLEOUTERUPLEFTCORNER;
                    case 'd' -> TileType.SIMPLEOUTERUPRIGHTCORNER;
                    case 'e' -> TileType.SIMPLEINNERDOWNRIGHTCORNER;
                    case 'f' -> TileType.SIMPLEINNERDOWNLEFTCORNER;
                    case 'g' -> TileType.SIMPLEINNERUPLEFTCORNER;
                    case 'h' -> TileType.SIMPLEINNERUPRIGHTCORNER;
                    case '0' -> TileType.OUTOFBOUNDS;
                    case 'w' -> TileType.DOOR;
                    case 'X' -> TileType.GHOSTHOUSE;
                    default -> TileType.UNDEFINED;
                };
                if (map[y][x] == TileType.GHOSTHOUSE) {
                    ghostHouse = new Point(x, y);
                } else if (map[y][x] == TileType.UNDEFINED) {
                    System.err.println("Undefined tile type in tile map : "+tile);
                    return false;
                }
            }
        }
        if (ghostHouse != null)
            return true;
        System.err.println("No GHOSTHOUSE tile defined in tile map");
        return false;
    }

    public int getMapHeightTile() {
        return mapHeightTile;
    }

    public int getMapWidthTile() {
        return mapWidthTile;
    }

    public Point getGhostHouse() {
        return ghostHouse;
    }

    public boolean isUsable() {
        return usable;
    }

    public TileType getTile(Point position) {
        if (position.x >= 0
                && position.x < mapWidthTile
                && position.y >= 0
                && position.y < mapHeightTile) {
            return map[position.y][position.x];
        }
        return TileType.UNDEFINED;
    }
}
