package com.pine.panels.painting;

import com.pine.core.view.AbstractView;
import com.pine.injection.PInject;
import com.pine.repository.*;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.streaming.StreamingService;
import com.pine.service.streaming.ref.TextureResourceRef;
import com.pine.theme.Icons;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiTableColumnFlags;
import imgui.flag.ImGuiTableFlags;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.pine.panels.viewport.ViewportPanel.INV_X;
import static com.pine.panels.viewport.ViewportPanel.INV_Y;
import static com.pine.theme.Icons.ONLY_ICON_BUTTON_SIZE;

public class FoliagePanel extends AbstractMaskPanel {
    public static final int TABLE_FLAGS = ImGuiTableFlags.Resizable | ImGuiTableFlags.RowBg | ImGuiTableFlags.NoBordersInBody;

    @PInject
    public EditorRepository editorRepository;

    @PInject
    public FilesRepository filesRepository;

    @PInject
    public StreamingService streamingService;

    @PInject
    public TerrainRepository terrainRepository;

    private final ImVec2 maskRes = new ImVec2();
    private boolean showMask = false;
    private final Map<String, Boolean> toRemove = new HashMap<>();

    @Override
    protected String getTextureId() {
        return terrainRepository.instanceMaskMap;
    }

    @Override
    public void renderInternal() {
        ImGui.text(Icons.add + "Foliage");
        if (ImGui.beginChild(imguiId, ImGui.getWindowSizeX(), 50, true)) {
            for (String m : filesRepository.byType.get(StreamableResourceType.MESH)) {
                if (terrainRepository.foliage.containsKey(m)) {
                    continue;
                }

                FSEntry entry = filesRepository.entry.get(m);
                if (ImGui.button(entry.name)) {
                    var instance = new FoliageInstance(m, terrainRepository.foliage.size() + 1);
                    terrainRepository.foliage.put(m, instance);
                }
            }
        }
        ImGui.endChild();

        ImGui.dummy(0, 8);
        renderSelected();
    }

    private void renderSelected() {
        if (ImGui.beginTable("##foliage" + imguiId, 3, TABLE_FLAGS)) {
            ImGui.tableSetupColumn("Name", ImGuiTableColumnFlags.WidthStretch);
            ImGui.tableSetupColumn("Select for painting", ImGuiTableColumnFlags.WidthFixed, ONLY_ICON_BUTTON_SIZE);
            ImGui.tableSetupColumn("Remove", ImGuiTableColumnFlags.WidthFixed, ONLY_ICON_BUTTON_SIZE);
            ImGui.tableHeadersRow();

            for (FoliageInstance m : terrainRepository.foliage.values()) {
                FSEntry entry = filesRepository.entry.get(m.id);

                if (entry == null) {
                    toRemove.put(m.id, true);
                    continue;
                }

                ImGui.tableNextRow();
                ImGui.tableNextColumn();
                ImGui.text(Icons.forest + entry.name);
                ImGui.tableNextColumn();
                boolean isSelected = Objects.equals(editorRepository.foliageForPainting, m.id);
                if (ImGui.button((!isSelected ? Icons.check_box_outline_blank : Icons.check_box) + "##" + m.id, ONLY_ICON_BUTTON_SIZE, ONLY_ICON_BUTTON_SIZE)) {
                    editorRepository.foliageForPainting = isSelected ? null : m.id;
                }
                ImGui.tableNextColumn();
                if (ImGui.button(Icons.remove + "##" + m.id, ONLY_ICON_BUTTON_SIZE, ONLY_ICON_BUTTON_SIZE)) {
                    toRemove.put(m.id, true);
                }
            }
            remove();
            ImGui.endTable();
        }
    }

    private void remove() {
        if (!toRemove.isEmpty()) {
            for (String e : toRemove.keySet()) {
                if(Objects.equals(e, editorRepository.foliageForPainting)){
                    editorRepository.foliageForPainting = null;
                }
                terrainRepository.foliage.remove(e);
            }
            toRemove.clear();
            terrainRepository.registerChange();
        }
    }
}
