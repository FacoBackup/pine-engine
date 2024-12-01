package com.pine.engine.repository.terrain;

public class TerrainChunk {
    public final float locationX;
    public final float locationZ;
    public final float normalizedX;
    public final float normalizedZ;
    public final float x;
    public final float z;
    private boolean isCulled = false;
    private float divider = 1;
    private float tiles = 1;
    private int triangles;

    public TerrainChunk(float locationX, float locationZ, float normalizedX, float normalizedZ, float x, float z) {
        this.locationX = locationX;
        this.locationZ = locationZ;
        this.normalizedX = normalizedX;
        this.normalizedZ = normalizedZ;
        this.x = x;
        this.z = z;
    }

    public void setCulled(boolean culled) {
        isCulled = culled;
    }

    public boolean isCulled() {
        return isCulled;
    }

    public void setDivider(float divider) {
        this.divider = divider;
    }

    public float getDivider() {
        return divider;
    }

    public void setTiles(float tiles) {
        this.tiles = tiles;
    }

    public float getTiles() {
        return tiles;
    }

    public void setTriangles(int triangles) {
        this.triangles = triangles;
    }

    public int getTriangles() {
        return triangles;
    }
}
