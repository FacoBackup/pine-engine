package com.pine.panels.painting;

import com.pine.core.view.AbstractView;
import com.pine.injection.PInject;
import com.pine.repository.EditorRepository;
import com.pine.repository.FilesRepository;
import com.pine.repository.TerrainRepository;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.streaming.StreamingService;
import com.pine.service.streaming.ref.TextureResourceRef;
import imgui.ImGui;
import imgui.ImVec2;

import static com.pine.panels.viewport.ViewportPanel.INV_X;
import static com.pine.panels.viewport.ViewportPanel.INV_Y;

public class TerrainPanel extends AbstractMaskPanel {
    @PInject
    public TerrainRepository terrainRepository;

    @Override
    protected String getTextureId() {
        return terrainRepository.heightMapTexture;
    }
}
