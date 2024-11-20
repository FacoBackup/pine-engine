package com.pine.service.grid;

import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.messaging.Loggable;
import com.pine.repository.CameraRepository;
import com.pine.repository.EngineRepository;
import com.pine.repository.WorldRepository;
import com.pine.service.rendering.TransformationService;
import com.pine.service.streaming.StreamingService;
import com.pine.tasks.AbstractTask;
import com.pine.tasks.SyncTask;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import static com.pine.service.grid.WorldGrid.TILE_SIZE;

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

    @PInject
    public TransformationService transformationService;

    private WorldTile[] loadedWorldTiles;
    private WorldTile currentWorldTile;

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
    public void onInitialize() {
        try {
            for (WorldTile worldTile : getTiles().values()) {
                if (worldTile.getEntities().isEmpty()) {
                    getHashGrid().removeTile(worldTile.getId());
                }
            }
        } catch (Exception e) {
            getLogger().error("Could not clean up tiles", e);
        }
    }

    @Override
    public void sync() {
        updateLoadedTiles();
        updateCurrentTile();
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

        worldTile.setCulled(transformationService.isCulled(worldTile.getBoundingBox().center, engineRepository.tileCullingMaxDistance, TILE_SIZE * 2));
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
}
