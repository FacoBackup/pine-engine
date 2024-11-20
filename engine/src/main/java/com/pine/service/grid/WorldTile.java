package com.pine.service.grid;

import com.pine.service.voxelization.svo.SparseVoxelOctree;
import com.pine.service.voxelization.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.*;

import static com.pine.service.grid.WorldGrid.TILE_SIZE;

public class WorldTile {
    private final String[] adjacentTiles = new String[8];
    private final int x;
    private final int z;
    private final String id;
    private boolean loaded;
    private boolean culled;
    private SparseVoxelOctree svo;
    private Map<String, Boolean> entities = new HashMap<>();
    private final BoundingBox boundingBox = new BoundingBox();
    private int normalizedDistance = 0;

    public WorldTile(int x, int z, String id) {
        this.x = x;
        this.z = z;
        this.id = id;

        boundingBox.center.x = x * TILE_SIZE;
        boundingBox.center.z = z * TILE_SIZE;
        boundingBox.max.set(boundingBox.center).add(TILE_SIZE / 2f, TILE_SIZE / 2f, TILE_SIZE / 2f);
        boundingBox.min.set(boundingBox.center).sub(TILE_SIZE / 2f, TILE_SIZE / 2f, TILE_SIZE / 2f);
    }

    public Set<String> getEntities() {
        return entities.keySet();
    }

    public Map<String, Boolean> getEntitiesMap() {
        return entities;
    }

    public void setEntities(Map<String, Boolean> entities) {
        this.entities = entities;
    }

    public static @NotNull String getId(int x, int z) {
        return x + "_" + z;
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public int getNormalizedDistance() {
        return normalizedDistance;
    }

    public void setNormalizedDistance(int normalizedDistance) {
        this.normalizedDistance = normalizedDistance;
    }

    public String getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public SparseVoxelOctree getSvo() {
        return svo;
    }

    public void setSvo(SparseVoxelOctree svo) {
        this.svo = svo;
    }

    public String[] getAdjacentTiles() {
        return adjacentTiles;
    }

    public void putAdjacentTile(WorldTile adjacentWorldTile) {
        updateTiles(adjacentWorldTile, adjacentWorldTile.id);
    }

    public void removeAdjacentTile(WorldTile adjacentWorldTile) {
        updateTiles(adjacentWorldTile, null);
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public void setCulled(boolean culled) {
        this.culled = culled;
    }

    public boolean isCulled() {
        return culled;
    }

    private void updateTiles(WorldTile adjacentWorldTile, String key) {
        boolean isWest = adjacentWorldTile.z < z && adjacentWorldTile.x == x;
        boolean isEast = adjacentWorldTile.z > z && adjacentWorldTile.x == x;
        boolean isNorth = adjacentWorldTile.x > x && adjacentWorldTile.z == z;
        boolean isSouth = adjacentWorldTile.x < x && adjacentWorldTile.z == z;

        boolean isSouthEast = adjacentWorldTile.x < x && adjacentWorldTile.z > z;
        boolean isSouthWest = adjacentWorldTile.x < x && adjacentWorldTile.z < z;
        boolean isNorthEast = adjacentWorldTile.x > x && adjacentWorldTile.z > z;
        boolean isNorthWest = adjacentWorldTile.x > x && adjacentWorldTile.z < z;

        if (isWest) {
            adjacentTiles[0] = key;
        }

        if (isNorth) {
            adjacentTiles[1] = key;
        }

        if (isEast) {
            adjacentTiles[2] = key;
        }

        if (isSouth) {
            adjacentTiles[3] = key;
        }

        if (isSouthWest) {
            adjacentTiles[4] = key;
        }

        if (isSouthEast) {
            adjacentTiles[5] = key;
        }

        if (isNorthWest) {
            adjacentTiles[6] = key;
        }

        if (isNorthEast) {
            adjacentTiles[7] = key;
        }
    }
}
