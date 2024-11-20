package com.pine.panels.inspector;

import com.pine.core.AbstractView;
import com.pine.injection.PInject;
import com.pine.repository.*;
import com.pine.repository.terrain.TerrainRepository;
import com.pine.theme.Icons;
import imgui.ImGui;
import imgui.flag.ImGuiTableColumnFlags;
import imgui.flag.ImGuiTableFlags;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.pine.theme.Icons.ONLY_ICON_BUTTON_SIZE;

public class FoliagePanel extends AbstractView {
    public static final int TABLE_FLAGS = ImGuiTableFlags.Resizable | ImGuiTableFlags.RowBg | ImGuiTableFlags.NoBordersInBody;

    @PInject
    public EditorRepository editorRepository;

    @PInject
    public TerrainRepository terrainRepository;

    private final Map<String, Boolean> toRemove = new HashMap<>();
    private AbstractResourceField materialField;
    private AbstractResourceField meshField;

    @Override
    public void onInitialize() {
        materialField = appendChild(new MaterialResourceField());
        meshField = appendChild(new MeshResourceField());
    }

    @Override
    public void render() {
        if (ImGui.button(Icons.add + "Add foliage" + imguiId)) {
            var instance = new FoliageInstance(terrainRepository.foliage.size() + 1);
            terrainRepository.foliage.put(instance.id, instance);
        }
        ImGui.dummy(0, 8);
        renderSelected();
    }

    private void renderSelected() {
        if (ImGui.beginTable("##foliage" + imguiId, 3, TABLE_FLAGS)) {
            ImGui.tableSetupColumn("Actions", ImGuiTableColumnFlags.WidthStretch);
            ImGui.tableSetupColumn("Material", ImGuiTableColumnFlags.WidthStretch);
            ImGui.tableSetupColumn("Mesh", ImGuiTableColumnFlags.WidthStretch);
            ImGui.tableHeadersRow();

            for (FoliageInstance m : terrainRepository.foliage.values()) {
                ImGui.tableNextRow();
                ImGui.tableNextColumn();
                boolean isSelected = Objects.equals(editorRepository.foliageForPainting, m.id);
                if (ImGui.button((!isSelected ? Icons.check_box_outline_blank : Icons.check_box) + "##" + m.id, ONLY_ICON_BUTTON_SIZE, ONLY_ICON_BUTTON_SIZE)) {
                    editorRepository.foliageForPainting = isSelected ? null : m.id;
                }
                ImGui.sameLine();
                if (ImGui.button(Icons.remove + "##" + m.id, ONLY_ICON_BUTTON_SIZE, ONLY_ICON_BUTTON_SIZE)) {
                    toRemove.put(m.id, true);
                    if (Objects.equals(editorRepository.foliageForPainting, m.id)) {
                        editorRepository.foliageForPainting = null;
                    }
                }

                ImGui.tableNextColumn();
                materialField.setFoliage(m);
                materialField.render();

                ImGui.tableNextColumn();
                meshField.setFoliage(m);
                meshField.render();
            }
            remove();
            ImGui.endTable();
        }
    }

    private void remove() {
        if (!toRemove.isEmpty()) {
            for (String e : toRemove.keySet()) {
                if (Objects.equals(e, editorRepository.foliageForPainting)) {
                    editorRepository.foliageForPainting = null;
                }
                terrainRepository.foliage.remove(e);
            }
            toRemove.clear();
        }
    }
}
