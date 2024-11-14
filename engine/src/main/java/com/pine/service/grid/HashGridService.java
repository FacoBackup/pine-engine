package com.pine.service.grid;

import com.pine.component.EnvironmentProbeComponent;
import com.pine.component.MeshComponent;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.messaging.Loggable;
import com.pine.repository.CameraRepository;
import com.pine.repository.EngineRepository;
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
import java.util.Objects;

import static com.pine.service.grid.HashGrid.TILE_SIZE;
import static java.lang.Math.sin;
import static org.joml.Math.cos;

@PBean
public class HashGridService extends AbstractTask implements SyncTask, Loggable {

    @PInject
    public EngineRepository repo;

    @PInject
    public CameraRepository cameraRepository;

    @PInject
    public StreamingService streamingService;

    @PInject
    public TransformationService transformationService;

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

    public HashGrid getHashGrid() {
        return repo.hashGrid;
    }

    @Override
    protected void tickInternal() {
        var currentTile = getHashGrid().getOrCreateTile(cameraRepository.currentCamera.position);
        for(Tile tile : getTiles().values()) {
            updateTile(tile, currentTile);
        }

    }

    private void updateTile(Tile tile, Tile currentTile) {
        tile.setCulled(transformationService.isCulled(tile.getBoundingBox().center, repo.tileCullingMaxDistance, (float) Math.sqrt(TILE_SIZE)));
        if(tile == currentTile) {
            tile.setLoaded(true);
            return;
        }
        for(var adjacent : currentTile.getAdjacentTiles()){
            if(Objects.equals(adjacent, tile.getId())){
                tile.setLoaded(true);
                return;
            }
        }
        tile.setLoaded(false);
    }

    @Override
    public String getTitle() {
        return "Grid processing";
    }
}
