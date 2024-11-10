package com.pine.service.grid;

import com.pine.component.Entity;
import com.pine.repository.CameraRepository;
import com.pine.service.camera.Camera;
import org.joml.Vector3f;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static com.pine.service.grid.HashGrid.TILE_SIZE;
import static org.junit.jupiter.api.Assertions.*;

class HashGridServiceTest {
    private HashGridService hashGridService = new HashGridService();

    @BeforeEach
    void setUp() {
        hashGridService.repo = new HashGridRepository();
        hashGridService.cameraRepository = new CameraRepository();
    }

    @Test
    void currentTile() {
        hashGridService.cameraRepository.currentCamera.position.set(0, 0, 0);
        Assertions.assertEquals(Tile.getId(0, 0), hashGridService.getCurrentTile().getId());

        hashGridService.cameraRepository.currentCamera.position.set(TILE_SIZE - 1, 0, TILE_SIZE - 1);
        Assertions.assertEquals(Tile.getId(0, 0), hashGridService.getCurrentTile().getId());

        hashGridService.cameraRepository.currentCamera.position.set(-1, 0, -1);
        Assertions.assertEquals(Tile.getId(0, 0), hashGridService.getCurrentTile().getId());

        hashGridService.cameraRepository.currentCamera.position.set(TILE_SIZE, 0, TILE_SIZE);
        Assertions.assertEquals(Tile.getId(1, 1), hashGridService.getCurrentTile().getId());

        hashGridService.cameraRepository.currentCamera.position.set(TILE_SIZE, 0, -TILE_SIZE);
        Assertions.assertEquals(Tile.getId(1, -1), hashGridService.getCurrentTile().getId());

        hashGridService.cameraRepository.currentCamera.position.set(-TILE_SIZE, 0, -TILE_SIZE);
        Assertions.assertEquals(Tile.getId(-1, -1), hashGridService.getCurrentTile().getId());

        hashGridService.cameraRepository.currentCamera.position.set(-TILE_SIZE, 0, 0);
        Assertions.assertEquals(Tile.getId(-1, 0), hashGridService.getCurrentTile().getId());

        hashGridService.cameraRepository.currentCamera.position.set(0, 0, -TILE_SIZE);
        Assertions.assertEquals(Tile.getId(0, -1), hashGridService.getCurrentTile().getId());

        hashGridService.cameraRepository.currentCamera.position.set(0, 0, TILE_SIZE);
        Assertions.assertEquals(Tile.getId(0, 1), hashGridService.getCurrentTile().getId());

        hashGridService.cameraRepository.currentCamera.position.set(TILE_SIZE, 0, 0);
        Assertions.assertEquals(Tile.getId(1, 0), hashGridService.getCurrentTile().getId());

        getAdjacentTiles();
    }

    void getAdjacentTiles() {
        hashGridService.cameraRepository.currentCamera.position.set(0, 0, 0);
        var tiles = hashGridService.getLoadedTiles();
        var topId = Tile.getId(0, 1);
        var leftId = Tile.getId(1, 0);
        var rightId = Tile.getId(-1, 0);
        var bottomId = Tile.getId(0, -1);
        for (var tile : tiles) {
            if(tile == hashGridService.getCurrentTile()) {
                continue;
            }
            boolean isIn = false;
            Assertions.assertNotNull(tile);
            if (Objects.equals(tile.getId(), topId)) {
                isIn = true;
            }
            if (Objects.equals(tile.getId(), bottomId)) {
                isIn = true;
            }
            if (Objects.equals(tile.getId(), leftId)) {
                isIn = true;
            }
            if (Objects.equals(tile.getId(), rightId)) {
                isIn = true;
            }

            Assertions.assertTrue(isIn);
        }
    }

    @Test
    void moveEntityBetweenTiles() {
        hashGridService.cameraRepository.currentCamera.position.set(0, 0, 0);
        var current = hashGridService.getCurrentTile();
        var entity = new Entity();
        current.getWorld().entityMap.put(entity.id, entity);

        hashGridService.moveEntityBetweenTiles(hashGridService.getCurrentTile(), new Vector3f(-1), entity.id);
        Assertions.assertFalse(current.getWorld().entityMap.containsKey(entity.id));

        var newTile = hashGridService.getTiles().get(Tile.getId(-1, -1));
        Assertions.assertTrue(newTile.getWorld().entityMap.containsKey(entity.id));
    }
}