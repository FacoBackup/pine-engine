package com.pine.service.grid;

import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

public class HashGrid {
    public static final int TILE_SIZE = 150;
    private final Map<String, Tile> tiles = new HashMap<>();
    private transient Tile[] loadedTiles = new Tile[5];

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
            loadedTiles = new Tile[5];
        }
        var center = getOrCreateTile(point);
        loadedTiles[0] = center;
        var adjacentIds = center.getAdjacentTiles();
        for (int i = 0; i < 4; i++) {
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
        String leftTileId = Tile.getId(x, z - 1);
        String rightTileId = Tile.getId(x, z + 1);
        String topTileId = Tile.getId(x + 1, z);
        String bottomTileId = Tile.getId(x - 1, z);

        var leftTile = tiles.get(leftTileId);
        var rightTile = tiles.get(rightTileId);
        var topTile = tiles.get(topTileId);
        var bottomTile = tiles.get(bottomTileId);

        if (leftTile != null) {
            leftTile.putAdjacentTile(newTile);
            newTile.putAdjacentTile(leftTile);
        }

        if (rightTile != null) {
            rightTile.putAdjacentTile(newTile);
            newTile.putAdjacentTile(rightTile);
        }

        if (topTile != null) {
            topTile.putAdjacentTile(newTile);
            newTile.putAdjacentTile(topTile);
        }

        if (bottomTile != null) {
            bottomTile.putAdjacentTile(newTile);
            newTile.putAdjacentTile(bottomTile);
        }
        return newTile;
    }

    public Map<String, Tile> getTiles() {
        return tiles;
    }
}
