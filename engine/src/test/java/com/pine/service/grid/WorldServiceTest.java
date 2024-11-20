package com.pine.service.grid;

import com.pine.repository.CameraRepository;
import com.pine.repository.WorldRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static com.pine.service.grid.WorldGrid.TILE_SIZE;

class WorldServiceTest {
    private final WorldService worldService = new WorldService();

    @BeforeEach
    void setUp() {
        worldService.repo = new WorldRepository();
        worldService.cameraRepository = new CameraRepository();
    }

    @Test
    void currentTile() {
        worldService.cameraRepository.currentCamera.position.set(0, 0, 0);
        Assertions.assertEquals(WorldTile.getId(0, 0), worldService.getCurrentTile().getId());

        worldService.cameraRepository.currentCamera.position.set(TILE_SIZE - 1, 0, TILE_SIZE - 1);
        Assertions.assertEquals(WorldTile.getId(0, 0), worldService.getCurrentTile().getId());

        worldService.cameraRepository.currentCamera.position.set(-1, 0, -1);
        Assertions.assertEquals(WorldTile.getId(0, 0), worldService.getCurrentTile().getId());

        worldService.cameraRepository.currentCamera.position.set(TILE_SIZE, 0, TILE_SIZE);
        Assertions.assertEquals(WorldTile.getId(1, 1), worldService.getCurrentTile().getId());

        worldService.cameraRepository.currentCamera.position.set(TILE_SIZE, 0, -TILE_SIZE);
        Assertions.assertEquals(WorldTile.getId(1, -1), worldService.getCurrentTile().getId());

        worldService.cameraRepository.currentCamera.position.set(-TILE_SIZE, 0, -TILE_SIZE);
        Assertions.assertEquals(WorldTile.getId(-1, -1), worldService.getCurrentTile().getId());

        worldService.cameraRepository.currentCamera.position.set(-TILE_SIZE, 0, 0);
        Assertions.assertEquals(WorldTile.getId(-1, 0), worldService.getCurrentTile().getId());

        worldService.cameraRepository.currentCamera.position.set(0, 0, -TILE_SIZE);
        Assertions.assertEquals(WorldTile.getId(0, -1), worldService.getCurrentTile().getId());

        worldService.cameraRepository.currentCamera.position.set(0, 0, TILE_SIZE);
        Assertions.assertEquals(WorldTile.getId(0, 1), worldService.getCurrentTile().getId());

        worldService.cameraRepository.currentCamera.position.set(TILE_SIZE, 0, 0);
        Assertions.assertEquals(WorldTile.getId(1, 0), worldService.getCurrentTile().getId());

        getAdjacentTiles();
    }

    void getAdjacentTiles() {
        worldService.cameraRepository.currentCamera.position.set(0, 0, 0);
        var tiles = worldService.getLoadedTiles();
        var topId = WorldTile.getId(0, 1);
        var leftId = WorldTile.getId(1, 0);
        var rightId = WorldTile.getId(-1, 0);
        var bottomId = WorldTile.getId(0, -1);
        for (var tile : tiles) {
            if(tile == worldService.getCurrentTile()) {
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

}