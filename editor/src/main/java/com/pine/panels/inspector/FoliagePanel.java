package com.pine.panels.inspector;

import com.pine.core.AbstractView;
import com.pine.injection.PInject;
import com.pine.panels.component.FormPanel;
import com.pine.repository.EditorRepository;
import com.pine.repository.FoliageInstance;
import com.pine.repository.terrain.TerrainRepository;
import com.pine.service.rendering.RequestProcessingService;
import com.pine.service.request.UpdateFieldRequest;
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

    @PInject
    public RequestProcessingService requestProcessingService;

    private final Map<String, Boolean> toRemove = new HashMap<>();
    private FormPanel form;

    @Override
    public void onInitialize() {
        form = appendChild(new FormPanel((field, value) -> {
            requestProcessingService.addRequest(new UpdateFieldRequest(field, value));
        }));
    }

    @Override
    public void render() {
        ImGui.dummy(0, 8);
        if (ImGui.button(Icons.add + "Add foliage" + imguiId)) {
            var instance = new FoliageInstance(terrainRepository.foliage.size() + 1);
            terrainRepository.foliage.put(instance.id, instance);
        }
        ImGui.dummy(0, 8);
        renderSelected();
        ImGui.dummy(0, 8);
        if (editorRepository.foliageForPainting != null) {
            form.setInspection(terrainRepository.foliage.get(editorRepository.foliageForPainting));
        } else {
            form.setInspection(null);
        }
        form.render();
    }

    private void renderSelected() {
        if (ImGui.beginTable("##foliage" + imguiId, 2, TABLE_FLAGS)) {
            ImGui.tableSetupColumn("Name", ImGuiTableColumnFlags.WidthStretch);
            ImGui.tableSetupColumn("Actions", ImGuiTableColumnFlags.WidthStretch);
            ImGui.tableHeadersRow();

            for (FoliageInstance m : terrainRepository.foliage.values()) {
                ImGui.tableNextRow();
                ImGui.tableNextColumn();
                ImGui.text(Icons.forest + m.name);
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
