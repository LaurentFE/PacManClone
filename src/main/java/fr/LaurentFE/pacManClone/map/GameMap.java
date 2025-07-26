package fr.LaurentFE.pacManClone.map;

import fr.LaurentFE.pacManClone.entities.Pellet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameMap {
    private static GameMap INSTANCE;
    private final int mapHeightTile;
    private final int mapWidthTile;
    private final TileType[][] map;
    private boolean usable;
    private TileIndex ghostHouse;

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
                    ghostHouse = new TileIndex(x, y);
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

    public TileIndex getGhostHouse() {
        return ghostHouse;
    }

    public boolean isUsable() {
        return usable;
    }

    public TileType getTile(TileIndex tileIndex) {
        if (tileIndex.x >= 0
                && tileIndex.x < mapWidthTile
                && tileIndex.y >= 0
                && tileIndex.y < mapHeightTile) {
            return map[tileIndex.y][tileIndex.x];
        }
        return TileType.UNDEFINED;
    }

    public Set<Pellet> loadPellets(String filePath) {
        try {
            return loadPelletMap(filePath);
        } catch (IOException e) {
            System.err.println("Couldn't read file '"+filePath+"'");
            return null;
        }
    }

    private Set<Pellet> loadPelletMap(String filePath) throws IOException {
        Path pelletMapFilePath = Path.of(filePath);
        List<String> pelletMap = Files.readAllLines(pelletMapFilePath);
        return convertPelletMap(pelletMap);
    }

    private Set<Pellet> convertPelletMap(List<String> pelletMap) {
        Set<Pellet> pellets = new HashSet<>();
        for (int y = 0; y < mapHeightTile; y++) {
            for (int x = 0; x < mapWidthTile; x++) {
                if (y >= pelletMap.size() || x >= pelletMap.get(y).length()) {
                    System.err.println("Provided pellet map size ("+pelletMap.get(y).length()+"*"+pelletMap.size()+" " +
                            "doesn't coincide with mapWidthTile("+mapWidthTile+") " +
                            "or mapHeightTile("+mapHeightTile+") requirements on line "+y);
                    return null;
                }
                char pellet = pelletMap.get(y).charAt(x);
                switch(pellet) {
                    case '.' : pellets.add(new Pellet(x, y, false)); break;
                    case 'X' : pellets.add(new Pellet(x, y, true)); break ;
                    default : if (pellet != '0') System.err.println("IGNORED - Undefined tile type in pellet map : "+pellet); break;
                }
            }
        }
        return pellets;
    }
}
