package com.pine.editor.panels.inspector;

import com.pine.common.Icons;
import com.pine.common.injection.PInject;
import com.pine.common.inspection.FieldDTO;
import com.pine.common.inspection.Inspectable;
import com.pine.editor.core.dock.AbstractDockPanel;
import com.pine.editor.panels.component.FormPanel;
import com.pine.editor.repository.EditorRepository;
import com.pine.engine.component.Entity;
import com.pine.engine.repository.WorldRepository;
import com.pine.engine.repository.terrain.TerrainRepository;
import com.pine.engine.service.rendering.RequestProcessingService;
import com.pine.engine.service.request.UpdateFieldRequest;
import com.pine.engine.service.world.WorldService;
import imgui.ImGui;
import imgui.type.ImString;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InspectorPanel extends AbstractDockPanel {
    @PInject
    public RequestProcessingService requestProcessingService;
    @PInject
    public EditorRepository editorRepository;
    @PInject
    public WorldService worldService;
    @PInject
    public WorldRepository world;
    @PInject
    public TerrainRepository terrainRepository;
    @PInject
    public List<Inspectable> repositories;

    private String selectedId;
    private final ImString fieldSearch = new ImString();
    private final List<FormPanel> dynamicForms = new ArrayList<>();
    private final List<FormPanel> staticForms = new ArrayList<>();

    @Override
    public void onInitialize() {
        for (Inspectable repo : repositories) {
            var form = appendChild(new FormPanel(this::handleChange));
            form.setInspection(repo);
            staticForms.add(form);
            form.setCompactMode(true);
        }
        appendChild(new FoliagePanel());
        appendChild(new MaterialPanel());
    }

    private void handleChange(FieldDTO dto, Object newValue) {
        requestProcessingService.addRequest(new UpdateFieldRequest(dto, newValue));
    }

    @Override
    public void render() {
        tick();
        ImGui.text(Icons.search);
        ImGui.sameLine();
        ImGui.inputText(imguiId + "search", fieldSearch);
        String search = fieldSearch.get().toLowerCase();
        if (ImGui.beginChild(imguiId + "cont")) {
            for (var form : dynamicForms) {
                form.setSearch(search);
                form.render();
            }
            if (!dynamicForms.isEmpty()) {
                ImGui.separator();
            }
            for (var form : staticForms) {
                form.setSearch(search);
                form.render();
            }
        }
        ImGui.endChild();
    }

    private void tick() {
        if (!Objects.equals(editorRepository.mainSelection, selectedId)) {
            dynamicForms.clear();
            selectedId = editorRepository.mainSelection;
            if (selectedId != null) {
                var selected = world.entityMap.get(selectedId);
                if (selected != null) {
                    addDynamicInspection(selected);
                    world.runByComponent(this::addDynamicInspection, selectedId);
                }
            }

            if (selectedId == null && !dynamicForms.isEmpty()) {
                dynamicForms.clear();
            }
        }
    }

    private void addDynamicInspection(Inspectable comp) {
        var form = appendChild(new FormPanel(this::handleChange));
        form.setInspection(comp);
        removeChild(form);
        form.setCompactMode(true);
        dynamicForms.add(form);
    }
}
