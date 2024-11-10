package com.pine.service.grid;

import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

public class HashGrid {
    public static final int TILE_SIZE = 150;
    private final Map<String, Tile> tiles = new HashMap<>();
    private transient Tile[] loadedTiles = new Tile[9];

    public Tile getOrCreateTile(Vector3f point) {
        int tileX = getTileLocation(point.x);
        int tileZ = getTileLocation(point.z);
        String id = Tile.getId(tileX, tileZ);
        if (!tiles.containsKey(id)) {
            addTile(point);
        }
        return tiles.get(id);
    }

    public static int getTileLocation(float v) {
        return (int) Math.floor(v / TILE_SIZE);
    }

    /**
     * Returns current tile and its adjacent ones
     * @param point Approximate location of the current tile
     * @return May contain null elements
     */
    public Tile[] getLoadedTiles(Vector3f point) {
        if (loadedTiles == null) {
            loadedTiles = new Tile[9];
        }
        var center = getOrCreateTile(point);
        loadedTiles[0] = center;
        var adjacentIds = center.getAdjacentTiles();
        for (int i = 0; i < 8; i++) {
            loadedTiles[i + 1] = tiles.get(adjacentIds[i]);
        }
        return loadedTiles;
    }

    /**
     * Adds tile that includes point if none is present
     * @param point tile that includes this point
     * @return Created tile
     */
    public Tile addTile(Vector3f point) {
        int x = getTileLocation(point.x);
        int z = getTileLocation(point.z);
        String id = Tile.getId(x, z);
        if (tiles.containsKey(id)) {
            return tiles.get(id);
        }
        var newTile = new Tile(x, z, id);
        tiles.put(newTile.getId(), newTile);

        String westTileId = Tile.getId(x, z - 1);
        String eastTileId = Tile.getId(x, z + 1);
        String northTileId = Tile.getId(x + 1, z);
        String southTileId = Tile.getId(x - 1, z);
        String northEastTileId = Tile.getId(x + 1, z + 1);
        String northWestTileId = Tile.getId(x + 1, z - 1);
        String southEastTileId = Tile.getId(x - 1, z + 1);
        String southWestTileId = Tile.getId(x - 1, z - 1);

        putTile(westTileId, newTile);
        putTile(eastTileId, newTile);
        putTile(northTileId, newTile);
        putTile(southTileId, newTile);
        putTile(northEastTileId, newTile);
        putTile(northWestTileId, newTile);
        putTile(southEastTileId, newTile);
        putTile(southWestTileId, newTile);

        return newTile;
    }

    private void putTile(String tileId, Tile newTile) {
        var tile = tiles.get(tileId);
        if (tile != null) {
            tile.putAdjacentTile(newTile);
            newTile.putAdjacentTile(tile);
        }
    }

    public Map<String, Tile> getTiles() {
        return tiles;
    }
}
