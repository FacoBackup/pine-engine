package com.pine.panels.painting;

import com.pine.core.dock.AbstractDockPanel;
import com.pine.injection.PInject;
import com.pine.inspection.Inspectable;
import com.pine.repository.BrushMode;
import com.pine.repository.EditorRepository;
import com.pine.repository.PaintingType;
import com.pine.theme.Icons;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiTabBarFlags;

public class PaintingPanel extends AbstractDockPanel {
    @PInject
    public EditorRepository editorRepository;

    private final float[] brushRadius = new float[]{1};
    private final float[] brushDensity = new float[]{1};
    private TerrainPanel terrainPanel;
    private FoliagePanel foliagePanel;
    private MaterialPanel materialPanel;

    @Override
    public void onInitialize() {
        foliagePanel = appendChild(new FoliagePanel());
        terrainPanel = appendChild(new TerrainPanel());
        materialPanel = appendChild(new MaterialPanel());
    }

    @Override
    public void render() {

        PaintingType[] values = PaintingType.values();
        for (int i = 0, valuesLength = values.length; i < valuesLength; i++) {
            var type = values[i];
            int popStyle = 0;
            if (editorRepository.paintingType == type) {
                ImGui.pushStyleColor(ImGuiCol.Button, editorRepository.accent);
                popStyle++;
            }
            if (ImGui.button(type.label + imguiId, ImGui.calcTextSizeX(type.label) + 8, 25)) {
                editorRepository.paintingType = type;
            }
            ImGui.popStyleColor(popStyle);

            if (i < valuesLength - 1) {
                ImGui.sameLine();
            }
        }
        ImGui.separator();

        renderBaseOptions();
        switch (editorRepository.paintingType) {
            case FOLIAGE -> foliagePanel.render();
            case TERRAIN -> terrainPanel.render();
            case MATERIAL -> materialPanel.render();
        }
    }

    private void renderBaseOptions() {
        BrushMode[] values = BrushMode.values();
        for (int i = 0, valuesLength = values.length; i < valuesLength; i++) {
            var mode = values[i];
            int popStyle = 0;
            if (editorRepository.brushMode == mode) {
                ImGui.pushStyleColor(ImGuiCol.Button, editorRepository.accent);
                popStyle++;
            }

            if (ImGui.button(mode.label, ImGui.calcTextSizeX(mode.label) + 8, 25)) {
                editorRepository.brushMode = mode;
            }
            ImGui.popStyleColor(popStyle);
            if (i < valuesLength - 1) {
                ImGui.sameLine();
            }
        }
        ImGui.separator();
        brushDensity[0] = editorRepository.brushDensity;
        brushRadius[0] = editorRepository.brushRadius;

        if (ImGui.dragFloat("Brush radius", brushRadius, .1f, .1f, .1f)) {
            editorRepository.brushRadius = brushRadius[0];
        }
        if (ImGui.dragFloat("Brush density", brushDensity, .001f, 0, 1)) {
            editorRepository.brushDensity = brushDensity[0];
        }
    }
}
