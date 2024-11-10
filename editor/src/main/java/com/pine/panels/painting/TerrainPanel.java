package com.pine.panels.painting;

import com.pine.injection.PInject;
import com.pine.service.grid.HashGridService;

public class TerrainPanel extends AbstractMaskPanel {
    @PInject
    public HashGridService hashGridService;

    @Override
    protected String getTextureId() {
        if(!hashGridService.getCurrentTile().isTerrainPresent){
            return null;
        }
        return hashGridService.getCurrentTile().terrainHeightMapId;
    }
}
