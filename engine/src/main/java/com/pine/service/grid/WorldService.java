package com.pine.service.grid;

import com.pine.component.MeshComponent;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.messaging.Loggable;
import com.pine.repository.CameraRepository;
import com.pine.repository.EngineRepository;
import com.pine.repository.WorldRepository;
import com.pine.service.streaming.StreamingService;
import com.pine.tasks.AbstractTask;
import com.pine.tasks.SyncTask;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

@PBean
public class WorldService extends AbstractTask implements SyncTask, Loggable {

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
            for (var tile : getTiles().values()) {
                getHashGrid().updateAdjacentTiles(tile);
            }
            getLogger().warn("Tile layout update: Before {} | After {} | Total processing {}ms", before, getTiles().size(), System.currentTimeMillis() - start);
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
        int min = -engineRepository.numberOfTiles / 2;
        int max = engineRepository.numberOfTiles / 2;
        var tiles = new ArrayList<>(getTiles().values());
        for (var tile : tiles) {
            if (tile.getZ() > max || tile.getZ() < min || tile.getX() > max || tile.getX() < min) {
                if (currentWorldTile != tile) {
                    getHashGrid().removeTile(tile.getId());
                }
            }
        }
    }

    @Override
    protected void tickInternal() {
        var currentTile = repo.worldGrid.getOrCreateTile(cameraRepository.currentCamera.position);
        Vector2f origin = new Vector2f(currentTile.getX(), currentTile.getZ());
        Vector2f aux = new Vector2f();
        try {
            for (var tile : repo.worldGrid.getTiles().values()) {
                processTile(currentTile, tile, aux, origin);
            }
        } catch (Exception e) {
            getLogger().error("Error while processing tile", e);
        }
    }

    private void processTile(WorldTile currentWorldTile, WorldTile worldTile, Vector2f aux, Vector2f origin) {
        if (worldTile == currentWorldTile) {
            worldTile.setLoaded(true);
            worldTile.setNormalizedDistance(0);
            return;
        }

        aux.set(worldTile.getX(), worldTile.getZ());
        worldTile.setNormalizedDistance((int) aux.sub(origin).length());
        for (var adjacent : currentWorldTile.getAdjacentTiles()) {
            if (Objects.equals(adjacent, worldTile.getId())) {
                worldTile.setLoaded(true);
                return;
            }
        }
        worldTile.setLoaded(false);
    }

    @Override
    public String getTitle() {
        return "Grid processing";
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
