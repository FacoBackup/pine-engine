package com.pine.panels.painting;

import com.pine.injection.PInject;
import com.pine.repository.TerrainRepository;

public class TerrainPanel extends AbstractMaskPanel {
    @PInject
    public TerrainRepository terrainRepository;

    @Override
    protected String getTextureId() {
        return terrainRepository.heightMapTexture;
    }
}
