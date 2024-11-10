package com.pine.service.grid;

import com.pine.component.EnvironmentProbeComponent;
import com.pine.component.MeshComponent;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.messaging.Loggable;
import com.pine.repository.CameraRepository;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.camera.Camera;
import com.pine.service.rendering.TransformationService;
import com.pine.service.request.DeleteEntityRequest;
import com.pine.service.streaming.StreamingService;
import com.pine.tasks.AbstractTask;
import com.pine.tasks.SyncTask;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.Map;

import static com.pine.service.grid.HashGrid.TILE_SIZE;
import static java.lang.Math.sin;
import static org.joml.Math.cos;

@PBean
public class HashGridService implements SyncTask, Loggable {

    @PInject
    public HashGridRepository repo;

    @PInject
    public CameraRepository cameraRepository;

    @PInject
    public StreamingService streamingService;

    private Tile[] loadedTiles;
    private Tile currentTile;

    public Tile[] getLoadedTiles() {
        if(loadedTiles == null){
            updateLoadedTiles();
        }
        return loadedTiles;
    }

    public Tile getCurrentTile() {
        if(currentTile == null){
            updateCurrentTile();
        }
        return currentTile;
    }

    private void updateLoadedTiles() {
        loadedTiles = repo.hashGrid.getLoadedTiles(cameraRepository.currentCamera.position);
    }

    private void updateCurrentTile() {
        currentTile = repo.hashGrid.getOrCreateTile(cameraRepository.currentCamera.position);
    }

    @Override
    public void sync() {
        updateLoadedTiles();
        updateCurrentTile();
    }

    public Map<String, Tile> getTiles() {
        return repo.hashGrid.getTiles();
    }

    public void moveEntityBetweenTiles(Tile tile, Vector3f newLocation, String entityId) {
        var newTile = repo.hashGrid.getOrCreateTile(newLocation);
        if (newTile != tile) {
            newTile.getWorld().entityMap.put(entityId, tile.getWorld().entityMap.get(entityId));
            tile.getWorld().runByComponent(comp -> {
                newTile.getWorld().registerComponent(comp);
            }, entityId);

            getLogger().warn("Entity moved to tile {} from tile {}", newTile.getId(), tile.getId());

            DeleteEntityRequest.removeEntity(entityId, tile);
        }
    }

    public HashGrid getHashGrid() {
        return repo.hashGrid;
    }
}
