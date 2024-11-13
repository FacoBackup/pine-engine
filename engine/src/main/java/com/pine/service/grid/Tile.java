package com.pine.service.grid;

import com.pine.service.voxelization.svo.SparseVoxelOctree;
import com.pine.service.voxelization.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import static com.pine.service.grid.HashGrid.TILE_SIZE;

public class Tile {
    public static final String FOLIAGE_MASK = "foliage-mask";
    private final String[] adjacentTiles = new String[8];
    private final int x;
    private final int z;
    private final String id;
    public boolean isTerrainPresent = false;
    public String terrainFoliageId;
    public String terrainHeightMapId;
    private boolean loaded;
    private boolean culled;
    private SparseVoxelOctree svo;
    private final TileWorld world;
    private final BoundingBox boundingBox = new BoundingBox();

    public Tile(int x, int z, String id) {
        this.x = x;
        this.z = z;
        this.id = id;
        world = new TileWorld(x, z);

        boundingBox.center.x = x * TILE_SIZE;
        boundingBox.center.z = z * TILE_SIZE;
        boundingBox.max.set(boundingBox.center).add(TILE_SIZE / 2f, TILE_SIZE / 2f, TILE_SIZE / 2f);
        boundingBox.min.set(boundingBox.center).sub(TILE_SIZE / 2f, TILE_SIZE / 2f, TILE_SIZE / 2f);
    }

    public static @NotNull String getId(int x, int z) {
        return x + "_" + z;
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
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

    public TileWorld getWorld() {
        return world;
    }

    public String[] getAdjacentTiles() {
        return adjacentTiles;
    }

    public void putAdjacentTile(Tile adjacentTile) {
        updateTiles(adjacentTile, adjacentTile.id);
    }

    public void removeAdjacentTile(Tile adjacentTile) {
        updateTiles(adjacentTile, null);
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

    private void updateTiles(Tile adjacentTile, String key) {
        boolean isWest = adjacentTile.z < z && adjacentTile.x == x;
        boolean isEast = adjacentTile.z > z && adjacentTile.x == x;
        boolean isNorth = adjacentTile.x > x && adjacentTile.z == z;
        boolean isSouth = adjacentTile.x < x && adjacentTile.z == z;

        boolean isSouthEast = adjacentTile.x < x && adjacentTile.z > z;
        boolean isSouthWest = adjacentTile.x < x && adjacentTile.z < z;
        boolean isNorthEast = adjacentTile.x > x && adjacentTile.z > z;
        boolean isNorthWest = adjacentTile.x > x && adjacentTile.z < z;

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
