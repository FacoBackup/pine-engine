package com.pine.editor.panels.viewport.header;

import com.pine.common.injection.PInject;
import com.pine.editor.core.UIUtil;
import com.pine.editor.repository.BrushMode;
import com.pine.editor.repository.EditorMode;
import com.pine.engine.repository.core.CoreBufferRepository;
import com.pine.engine.repository.streaming.StreamableResourceType;
import com.pine.engine.repository.terrain.TerrainRepository;
import com.pine.engine.service.streaming.ref.TextureResourceRef;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;

public class PaintingSettingsPanel extends AbstractViewportSettingsPanel {
    private final float[] brushRadius = new float[]{1};
    private final float[] brushDensity = new float[]{1};
    private final ImVec2 maskRes = new ImVec2();

    @PInject
    public TerrainRepository terrainRepository;

    @PInject
    public CoreBufferRepository bufferRepository;

    @Override
    public void render() {
        for (BrushMode mode : BrushMode.values()) {
            if (UIUtil.renderOption(mode.label, editorRepository.brushMode == mode, ImGui.calcTextSizeX(mode.label) + 8, 25, editorRepository.accent)) {
                editorRepository.brushMode = mode;
            }
            UIUtil.spacing();
        }
        UIUtil.largeSpacing();

        brushDensity[0] = editorRepository.brushDensity;
        brushRadius[0] = editorRepository.brushRadius;
        if (ImGui.checkbox("Show mask" + imguiId, editorRepository.showPaintingMask)) {
            editorRepository.showPaintingMask = !editorRepository.showPaintingMask;
        }
        UIUtil.spacing();
        ImGui.setNextItemWidth(150);
        if (ImGui.dragFloat("Brush radius", brushRadius, .1f, 0, 250)) {
            editorRepository.brushRadius = brushRadius[0];
        }
        UIUtil.spacing();
        ImGui.setNextItemWidth(150);
        if (ImGui.dragFloat("Brush density", brushDensity, .001f, 0, 1)) {
            editorRepository.brushDensity = brushDensity[0];
        }
    }

    @Override
    public void renderOutside() {
        if (editorRepository.editorMode != EditorMode.TRANSFORM && editorRepository.showPaintingMask) {
            var targetTexture = (TextureResourceRef) streamingService.streamIn(getTextureId(), StreamableResourceType.TEXTURE);
            if (targetTexture != null) {
                targetTexture.lastUse = System.currentTimeMillis();
                float maskSize = ImGui.getWindowSizeX() * .15f;
                ImGui.setNextWindowSize(maskSize, maskSize);
                ImGui.setNextWindowPos(ImGui.getWindowPosX() + ImGui.getWindowSizeX() - maskSize, ImGui.getWindowPosY() + ImGui.getWindowSizeY() - maskSize);
                if (ImGui.begin(imguiId, UIUtil.OPEN, ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoCollapse)) {
                    maskRes.x = ImGui.getWindowSizeX();
                    maskRes.y = ImGui.getWindowSizeX();
                    ImGui.image(targetTexture.texture, maskRes);
                }
                ImGui.end();
            }
        }
    }

    private String getTextureId() {
        switch (editorRepository.editorMode) {
            case TERRAIN -> {
                return terrainRepository.heightMapTexture;
            }
            case FOLIAGE -> {
                return terrainRepository.foliageMask;
            }
            case MATERIAL -> {
                return terrainRepository.materialMask;
            }
        }
        return null;
    }
}
