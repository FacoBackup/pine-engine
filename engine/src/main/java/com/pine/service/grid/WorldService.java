package com.pine.service.grid;

import com.pine.component.MeshComponent;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.messaging.Loggable;
import com.pine.repository.CameraRepository;
import com.pine.repository.EngineRepository;
import com.pine.repository.WorldRepository;
import com.pine.service.streaming.StreamingService;
import com.pine.tasks.SyncTask;

import java.util.ArrayList;
import java.util.Map;

@PBean
public class WorldService implements SyncTask, Loggable {

    @PInject
    public WorldRepository repo;

    @PInject
    public EngineRepository engineRepository;

    @PInject
    public CameraRepository cameraRepository;

    @PInject
    public StreamingService streamingService;

    private WorldTile[] loadedWorldTiles;
    private WorldTile currentWorldTile;
    private WorldTile prevCurrentTile;

    public WorldTile[] getLoadedTiles() {
        if (loadedWorldTiles == null) {
            updateLoadedTiles();
        }
        return loadedWorldTiles;
    }

    public WorldTile getCurrentTile() {
        if (currentWorldTile == null) {
            updateCurrentTile();
        }
        return currentWorldTile;
    }

    private void updateLoadedTiles() {
        loadedWorldTiles = repo.worldGrid.getLoadedTiles(cameraRepository.currentCamera.position);
    }

    private void updateCurrentTile() {
        currentWorldTile = repo.worldGrid.getOrCreateTile(cameraRepository.currentCamera.position);
    }

    public Map<String, WorldTile> getTiles() {
        return repo.worldGrid.getTiles();
    }

    public WorldGrid getHashGrid() {
        return repo.worldGrid;
    }

    @Override
    public void sync() {
        updateTileLayout();
        updateCurrentTile();
        if (prevCurrentTile != currentWorldTile) {
            long start = System.currentTimeMillis();
            updateLoadedTiles();
            prevCurrentTile = currentWorldTile;
            getLogger().warn("Loaded tiles query took {}ms", System.currentTimeMillis() - start);
        }
    }

    private void updateTileLayout() {
        int squared = engineRepository.numberOfTiles * engineRepository.numberOfTiles;
        boolean isSizeSmaller = getTiles().size() < squared;
        boolean isSizeBigger = getTiles().size() > squared;
        if (isSizeSmaller || isSizeBigger) {
            int before = getTiles().size();
            long start = System.currentTimeMillis();
            if (isSizeSmaller) { // Number of tiles increased
                addMissingTiles();
            } else { // Number of tiles reduced
                removeExtraTiles();
            }
            if (before != getTiles().size()) {
                for (var tile : getTiles().values()) {
                    getHashGrid().updateAdjacentTiles(tile);
                }
                getLogger().warn("Tile layout update: Before {} | After {} | Expected {} | Total processing {}ms", before, getTiles().size(), squared, System.currentTimeMillis() - start);
            }
        }
    }

    private void addMissingTiles() {
        int half = engineRepository.numberOfTiles / 2;
        for (int x = -half; x < half; x++) {
            for (int z = -half; z < half; z++) {
                repo.worldGrid.createIfAbsent(x, z);
            }
        }
    }

    private void removeExtraTiles() {
        int half = engineRepository.numberOfTiles / 2;
        int min = -half;
        var tiles = new ArrayList<>(getTiles().values());
        if (currentWorldTile == null || isTileOutsideBounds(currentWorldTile, half, min)) {
            return;
        }
        for (var tile : tiles) {
            if (isTileOutsideBounds(tile, half, min)) {
                getLogger().warn("Removing tile {}", tile.getId());
                getHashGrid().removeTile(tile.getId());
            }
        }
    }

    private static boolean isTileOutsideBounds(WorldTile tile, int half, int min) {
        return tile.getZ() >= half || tile.getZ() <= min || tile.getX() >= half || tile.getX() <= min;
    }

    public boolean isEntityVisible(String entityId) {
        return (!repo.culled.containsKey(entityId) || engineRepository.disableCullingGlobally) && !repo.hiddenEntities.containsKey(entityId);
    }

    public boolean isMeshReady(MeshComponent mesh) {
        if (mesh != null && isEntityVisible(mesh.getEntityId())) {
            return mesh.renderRequest != null && mesh.renderRequest.modelMatrix != null && mesh.renderRequest.mesh != null;
        }
        return false;
    }
}
