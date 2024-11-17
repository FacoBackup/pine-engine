package com.pine.panels.inspector;

import com.pine.core.AbstractView;
import com.pine.injection.PInject;
import com.pine.repository.*;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.SelectionService;
import com.pine.service.grid.WorldService;
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
    public FilesRepository filesRepository;

    @PInject
    public TerrainRepository terrainRepository;

    private final Map<String, Boolean> toRemove = new HashMap<>();
    private MaterialField materialField;

    @Override
    public void onInitialize() {
        materialField = appendChild(new MaterialField());
    }

    @Override
    public void render() {
            if (ImGui.beginChild(imguiId, ImGui.getWindowSizeX(), 50, true)) {
                for (String m : filesRepository.byType.get(StreamableResourceType.MESH)) {
                    if (terrainRepository.foliage.containsKey(m)) {
                        continue;
                    }

                    FSEntry entry = filesRepository.entry.get(m);

                    if(ImGui.getContentRegionAvailX() > 35){
                        ImGui.sameLine();
                    }
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
        if (ImGui.beginTable("##foliage" + imguiId, 2, TABLE_FLAGS)) {
            ImGui.tableSetupColumn("Name", ImGuiTableColumnFlags.WidthStretch);
            ImGui.tableSetupColumn("Material", ImGuiTableColumnFlags.WidthStretch);
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
                ImGui.sameLine();
                boolean isSelected = Objects.equals(editorRepository.foliageForPainting, m.id);
                if (ImGui.button((!isSelected ? Icons.check_box_outline_blank : Icons.check_box) + "##" + m.id, ONLY_ICON_BUTTON_SIZE, ONLY_ICON_BUTTON_SIZE)) {
                    editorRepository.foliageForPainting = isSelected ? null : m.id;
                }
                ImGui.sameLine();
                if (ImGui.button(Icons.remove + "##" + m.id, ONLY_ICON_BUTTON_SIZE, ONLY_ICON_BUTTON_SIZE)) {
                    toRemove.put(m.id, true);
                }
                ImGui.tableNextColumn();
                materialField.setFoliage(m);
                materialField.render();
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
