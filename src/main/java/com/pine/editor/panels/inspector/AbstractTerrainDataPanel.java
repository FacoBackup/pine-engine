package com.pine.editor.panels.inspector;

import com.pine.common.Icons;
import com.pine.common.injection.PInject;
import com.pine.editor.core.AbstractView;
import com.pine.editor.panels.component.FormPanel;
import com.pine.engine.repository.terrain.AbstractDataInstance;
import com.pine.engine.service.rendering.RequestProcessingService;
import com.pine.engine.service.request.UpdateFieldRequest;
import imgui.ImGui;
import imgui.flag.ImGuiTableColumnFlags;
import imgui.flag.ImGuiTableFlags;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.pine.common.Icons.ONLY_ICON_BUTTON_SIZE;

public abstract class AbstractTerrainDataPanel extends AbstractView {
    public static final int TABLE_FLAGS = ImGuiTableFlags.Resizable | ImGuiTableFlags.RowBg | ImGuiTableFlags.NoBordersInBody;

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

    protected abstract Map<String, ? extends AbstractDataInstance> getDataMap();

    protected abstract String getSelectedId();

    protected abstract void setSelectedId(String id);

    protected abstract void addNewInstance(int index);

    protected abstract String getTitle();

    @Override
    public void render() {
        if(ImGui.collapsingHeader(getTitle())){
            ImGui.dummy(0, 8);
            if (ImGui.button(getTitle() + imguiId)) {
                addNewInstance(getDataMap().size() + 1);
            }
            ImGui.dummy(0, 8);
            renderSelected();
            ImGui.dummy(0, 8);
            if (getSelectedId() != null) {
                form.setInspection(getDataMap().get(getSelectedId()));
            } else {
                form.setInspection(null);
            }
            form.render();
        }
    }

    private void renderSelected() {
        if (ImGui.beginTable("##data" + imguiId, 2, TABLE_FLAGS)) {
            ImGui.tableSetupColumn("Name", ImGuiTableColumnFlags.WidthStretch);
            ImGui.tableSetupColumn("Actions", ImGuiTableColumnFlags.WidthStretch);
            ImGui.tableHeadersRow();

            for (AbstractDataInstance m : getDataMap().values()) {
                ImGui.tableNextRow();
                ImGui.tableNextColumn();
                ImGui.text(m.name);
                ImGui.tableNextColumn();
                boolean isSelected = Objects.equals(getSelectedId(), m.id);
                if (ImGui.button((!isSelected ? Icons.check_box_outline_blank : Icons.check_box) + "##" + m.id, ONLY_ICON_BUTTON_SIZE, ONLY_ICON_BUTTON_SIZE)) {
                    setSelectedId(isSelected ? null : m.id);
                }
                ImGui.sameLine();
                if (ImGui.button(Icons.remove + "##" + m.id, ONLY_ICON_BUTTON_SIZE, ONLY_ICON_BUTTON_SIZE)) {
                    toRemove.put(m.id, true);
                    if (Objects.equals(getSelectedId(), m.id)) {
                        setSelectedId(null);
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
                if (Objects.equals(e, getSelectedId())) {
                    setSelectedId(null);
                }
                getDataMap().get(e).dispose();
                getDataMap().remove(e);
            }
            toRemove.clear();
        }
    }
}
