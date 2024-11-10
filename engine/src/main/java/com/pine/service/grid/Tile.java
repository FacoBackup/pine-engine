package com.pine.service.grid;

import com.pine.service.voxelization.svo.SparseVoxelOctree;
import org.jetbrains.annotations.NotNull;

public class Tile {
    public static final String FOLIAGE_MASK = "foliage-mask";
    private final String[] adjacentTiles = new String[4]; // LEFT UP RIGHT DOWN
    private final int x;
    private final int z;
    private final String id;
    public boolean isLoaded = false;
    public boolean isTerrainPresent = false;
    public String terrainFoliageId;
    public String terrainHeightMapId;
    private SparseVoxelOctree octree;
    private final TileWorld world;


    public Tile(int x, int z, String id) {
        this.x = x;
        this.z = z;
        this.id = id;
        world = new TileWorld(x, z);
    }

    public static @NotNull String getId(int x, int z) {
        return x + "_" + z;
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

    public SparseVoxelOctree getOctree() {
        return octree;
    }

    public void setOctree(SparseVoxelOctree octree) {
        this.octree = octree;
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

    private void updateTiles(Tile adjacentTile, String key) {
        if (adjacentTile.x > x && adjacentTile.z == z) { // UP
            adjacentTiles[1] = key;
        } else if(adjacentTile.x < x && adjacentTile.z == z){ // DOWN
            adjacentTiles[3] = key;
        }

        if (adjacentTile.z > z && adjacentTile.x == x) { // RIGHT
            adjacentTiles[2] = key;
        } else if(adjacentTile.z < z && adjacentTile.x == x){ // LEFT
            adjacentTiles[0] = key;
        }
    }
}
