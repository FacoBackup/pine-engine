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
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.Map;

import static com.pine.service.grid.HashGrid.TILE_SIZE;

@PBean
public class HashGridService extends AbstractTask implements Loggable {

    @PInject
    public HashGridRepository repo;

    @PInject
    public CameraRepository cameraRepository;

    @PInject
    public StreamingService streamingService;

    @PInject
    public TransformationService transformationService;

    private Tile[] visibleTiles = new Tile[3];

    public Tile[] getLoadedTiles() {
        return repo.hashGrid.getLoadedTiles(cameraRepository.currentCamera.position);
    }

    public Tile getCurrentTile() {
        return repo.hashGrid.getOrCreateTile(cameraRepository.currentCamera.position);
    }

    @Override
    protected void tickInternal() {
        var cameraLocation = cameraRepository.currentCamera.position;
        var hashGrid = repo.hashGrid;
        var previousTile = hashGrid.getOrCreateTile(repo.previousCameraLocation);
        var currentTile = hashGrid.getOrCreateTile(cameraLocation);

        if (previousTile != currentTile) {
            var previousLoadedTiles = hashGrid.getLoadedTiles(repo.previousCameraLocation);
            var currentLoadedTiles = hashGrid.getLoadedTiles(cameraLocation);
            for (Tile localTile : previousLoadedTiles) {
                if (localTile == null) {
                    continue;
                }
                boolean isIncluded = false;
                for (Tile currentLoadedTile : currentLoadedTiles) {
                    if (localTile == currentLoadedTile) {
                        isIncluded = true;
                        break;
                    }
                }
                if (!isIncluded) {
                    localTile.isLoaded = false;
                    unloadTile(localTile);
                }
            }

            for (Tile currentLoadedTile : currentLoadedTiles) {
                if (currentLoadedTile != null && !currentLoadedTile.isLoaded) {
                    loadTile(currentLoadedTile);
                }
            }
        }


        findVisibleTiles();

        repo.previousCameraLocation.set(cameraLocation);
    }

    int count = 0;
    private void findVisibleTiles() {
        Vector2f direction = new Vector2f(
                -cameraRepository.viewMatrix.m02(),
                -cameraRepository.viewMatrix.m22()
        ).normalize();

        int currentTileIndex = 0;
        for (Tile currentLoadedTile : getLoadedTiles()) {
            if (currentLoadedTile != null && currentTileIndex < 3 && isInFrustum(currentLoadedTile.getX() * TILE_SIZE, currentLoadedTile.getZ() * TILE_SIZE, cameraRepository.currentCamera, direction)) {
                visibleTiles[currentTileIndex] = currentLoadedTile;
                currentTileIndex++;
            }
        }

        if(count >= 50){
            count = 0;
            getLogger().warn("Tiles visible: {} {} {}",
                    visibleTiles[0] != null ? visibleTiles[0].getId() : null,
                    visibleTiles[1] != null ? visibleTiles[1].getId() : null,
                    visibleTiles[2] != null ? visibleTiles[2].getId() : null);
        }
        count++;
    }

    public static boolean isInFrustum(float x, float z, Camera camera, Vector2f direction) {
        float halfAngle = camera.fov / 2;

        Vector2f leftBoundary = rotateVector(direction, halfAngle);
        Vector2f rightBoundary = rotateVector(direction, -halfAngle);

        Vector2f toPoint = new Vector2f(x, z).sub(camera.position.x, camera.position.z);
        float distToPoint = toPoint.length();

        if (distToPoint < TILE_SIZE / 2f) {
            return true;
        }
        toPoint.normalize();

        boolean withinLeft = toPoint.dot(leftBoundary) >= 0;
        boolean withinRight = toPoint.dot(rightBoundary) <= 0;
        return withinLeft && withinRight;
    }

    private static Vector2f rotateVector(Vector2f vector, float angle) {
        float cosTheta = (float) Math.cos(angle);
        float sinTheta = (float) Math.sin(angle);
        return new Vector2f(
                vector.x * cosTheta - vector.y * sinTheta,
                vector.x * sinTheta + vector.y * cosTheta
        );
    }

    private void unloadTile(Tile tile) {
        getLogger().warn("Unloading tile {}", tile.getId());
        for (MeshComponent mesh : tile.getWorld().bagMeshComponent.values()) {
            streamingService.streamOut(mesh.lod0, StreamableResourceType.MESH);
            streamingService.streamOut(mesh.lod1, StreamableResourceType.MESH);
            streamingService.streamOut(mesh.lod2, StreamableResourceType.MESH);
            streamingService.streamOut(mesh.lod3, StreamableResourceType.MESH);
            streamingService.streamOut(mesh.lod4, StreamableResourceType.MESH);
            streamingService.streamOut(mesh.material, StreamableResourceType.MATERIAL);
        }

        for (EnvironmentProbeComponent probe : tile.getWorld().bagEnvironmentProbeComponent.values()) {
            streamingService.streamOut(probe.getEntityId(), StreamableResourceType.ENVIRONMENT_MAP);
        }
    }

    private void loadTile(Tile tile) {
        getLogger().warn("Loading tile {}", tile.getId());
        for (MeshComponent mesh : tile.getWorld().bagMeshComponent.values()) {
            streamingService.streamIn(mesh.lod0, StreamableResourceType.MESH);
            streamingService.streamIn(mesh.lod1, StreamableResourceType.MESH);
            streamingService.streamIn(mesh.lod2, StreamableResourceType.MESH);
            streamingService.streamIn(mesh.lod3, StreamableResourceType.MESH);
            streamingService.streamIn(mesh.lod4, StreamableResourceType.MESH);
            streamingService.streamIn(mesh.material, StreamableResourceType.MATERIAL);
        }

        for (EnvironmentProbeComponent probe : tile.getWorld().bagEnvironmentProbeComponent.values()) {
            streamingService.streamIn(probe.getEntityId(), StreamableResourceType.ENVIRONMENT_MAP);
        }
    }

    @Override
    public String getTitle() {
        return "Grid streaming";
    }

    public Map<String, Tile> getTiles() {
        return repo.hashGrid.getTiles();
    }

    public Tile[] getVisibleTiles() {
        return visibleTiles;
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
}
