package com.pine.engine.service.world;

import com.pine.common.messaging.Loggable;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

public class WorldGrid implements Loggable {
    public static final int TILE_SIZE = 64;
    private final Map<String, WorldTile> tiles = new HashMap<>();
    private transient WorldTile[] loadedWorldTiles = new WorldTile[9];

    public static int getTileLocation(float v) {
        return (int) Math.floor(v / TILE_SIZE);
    }

    /**
     * Returns current tile and its adjacent ones
     * @param point Approximate location of the current tile
     * @return May contain null elements
     */
    public WorldTile[] getLoadedTiles(Vector3f point) {
        if (loadedWorldTiles == null) {
            loadedWorldTiles = new WorldTile[9];
        }
        var center = getOrCreateTile(point);
        loadedWorldTiles[0] = center;
        var adjacentIds = center.getAdjacentTiles();
        for (int i = 0; i < 8; i++) {
            loadedWorldTiles[i + 1] = tiles.get(adjacentIds[i]);
        }
        return loadedWorldTiles;
    }

    public WorldTile getOrCreateTile(Vector3f point) {
        int tileX = getTileLocation(point.x);
        int tileZ = getTileLocation(point.z);
        String id = WorldTile.getId(tileX, tileZ);
        if (!tiles.containsKey(id)) {
            addTile(point);
        }
        return tiles.get(id);
    }

    public void createIfAbsent(int x, int z) {
        String id = WorldTile.getId(x, z);
        if (!tiles.containsKey(id)) {
            var newTile = new WorldTile(x, z, id);
            tiles.put(newTile.getId(), newTile);
        }
    }

    /**
     * Adds tile that includes point if none is present
     *
     * @param point tile that includes this point
     */
    public void addTile(Vector3f point) {
        int x = getTileLocation(point.x);
        int z = getTileLocation(point.z);
        String id = WorldTile.getId(x, z);
        if (tiles.containsKey(id)) {
            tiles.get(id);
            return;
        }
        var newTile = new WorldTile(x, z, id);
        tiles.put(newTile.getId(), newTile);

        updateAdjacentTiles(newTile);
    }

    public void updateAdjacentTiles(WorldTile newTile) {
        int x = newTile.getX();
        int z = newTile.getZ();
        int[] westTile = new int[]{x, z - 1};
        int[] eastTile = new int[]{x, z + 1};
        int[] northTile = new int[]{x + 1, z};
        int[] southTile = new int[]{x - 1, z};
        int[] northEastTile = new int[]{x + 1, z + 1};
        int[] northWestTile = new int[]{x + 1, z - 1};
        int[] southEastTile = new int[]{x - 1, z + 1};
        int[] southWestTile = new int[]{x - 1, z - 1};

        putAdjacentTile(westTile, newTile);
        putAdjacentTile(eastTile, newTile);
        putAdjacentTile(northTile, newTile);
        putAdjacentTile(southTile, newTile);
        putAdjacentTile(northEastTile, newTile);
        putAdjacentTile(northWestTile, newTile);
        putAdjacentTile(southEastTile, newTile);
        putAdjacentTile(southWestTile, newTile);
    }

    private void putAdjacentTile(int[] tileLocation, WorldTile newTile) {
        String tileId = WorldTile.getId(tileLocation[0], tileLocation[1]);
        if(tiles.containsKey(tileId)) {
            var tile = tiles.get(tileId);
            tile.putAdjacentTile(newTile);
            newTile.putAdjacentTile(tile);
        }
    }

    public Map<String, WorldTile> getTiles() {
        return tiles;
    }

    public void removeTile(String id) {
        var tileToRemove = tiles.get(id);
        tiles.remove(id);
        for (var tile : tiles.values()) {
            tile.removeAdjacentTile(tileToRemove);
        }
    }

    public void moveBetweenTiles(String entityId, WorldTile previousWorldTile, WorldTile newWorldTile) {
        if (!previousWorldTile.getEntitiesMap().containsKey(entityId) || newWorldTile != previousWorldTile) {
            var prevCopy = new HashMap<>(previousWorldTile.getEntitiesMap());
            prevCopy.remove(entityId);
            previousWorldTile.setEntities(prevCopy);

            var newCopy = new HashMap<>(newWorldTile.getEntitiesMap());
            newCopy.put(entityId, true);
            newWorldTile.setEntities(newCopy);
        }
    }
}
