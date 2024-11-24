package com.pine.editor.panels.inspector;

import com.pine.engine.component.AbstractComponent;
import com.pine.engine.component.ComponentType;
import com.pine.engine.component.Entity;
import com.pine.editor.core.AbstractView;
import com.pine.editor.core.UIUtil;
import com.pine.editor.core.dock.AbstractDockPanel;
import com.pine.common.injection.PInject;
import com.pine.common.inspection.Inspectable;
import com.pine.editor.panels.component.FormPanel;
import com.pine.editor.repository.EditorRepository;
import com.pine.engine.repository.WorldRepository;
import com.pine.engine.repository.terrain.TerrainRepository;
import com.pine.engine.service.world.WorldService;
import com.pine.engine.service.rendering.RequestProcessingService;
import com.pine.engine.service.request.AddComponentRequest;
import com.pine.engine.service.request.UpdateFieldRequest;
import com.pine.common.Icons;
import imgui.ImGui;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

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

    private final List<Inspectable> additionalInspection = new ArrayList<>();

    private Inspectable currentInspection;
    private Entity selected;
    private String selectedId;
    private final List<String> types = new ArrayList<>();
    private FormPanel formPanel;
    private AbstractView foliagePanel;
    private AbstractView materialPanel;

    @Override
    public void onInitialize() {
        types.add(Icons.add + " Add component");
        types.addAll(Stream.of(ComponentType.values()).map(a -> a.getIcon() + a.getTitle()).toList());
        formPanel = appendChild(new FormPanel((dto, newValue) -> requestProcessingService.addRequest(new UpdateFieldRequest(dto, newValue))));
        foliagePanel = appendChild(new FoliagePanel());
        materialPanel = appendChild(new MaterialPanel());
        currentInspection = repositories.getFirst();
    }

    @Override
    public void render() {
        tick();
        ImGui.columns(2, "##inspectorColumns" + imguiId, false);
        ImGui.setColumnWidth(0, 30);
        for (var repo : repositories) {
            if (UIUtil.renderOption(repo.getIcon(), Objects.equals(currentInspection, repo), true, editorRepository.accent)) {
                currentInspection = repo;
            }
        }

        ImGui.spacing();
        ImGui.spacing();
        for (var additional : additionalInspection) {
            if (additional != null) {
                if (UIUtil.renderOption(additional.getIcon(), Objects.equals(currentInspection, additional), true, editorRepository.accent)) {
                    currentInspection = additional;
                }
            }
        }

        ImGui.spacing();
        ImGui.spacing();

        ImGui.nextColumn();
        if (ImGui.beginChild(imguiId + "form")) {
            if (selected != null && additionalInspection.contains(currentInspection)) {
                if (ImGui.beginCombo(imguiId, types.getFirst())) {
                    for (int i = 1; i < types.size(); i++) {
                        String type = types.get(i);
                        if (ImGui.selectable(type)) {
                            ComponentType entityComponent = ComponentType.values()[i - 1];
                            requestProcessingService.addRequest(new AddComponentRequest(entityComponent, selected));
                            selected = null;
                            selectedId = null;
                        }
                    }
                    ImGui.endCombo();
                }
            }


            if (formPanel.getInspectable() != currentInspection) {
                formPanel.setInspection(currentInspection);
            }

            formPanel.render();

            if (Objects.equals(currentInspection, terrainRepository)) {
                foliagePanel.render();
                materialPanel.render();
            }
        }
        ImGui.endChild();
        ImGui.columns(1);
    }

    private void tick() {
        if (!Objects.equals(editorRepository.mainSelection, selectedId)) {
            additionalInspection.clear();
            selectedId = editorRepository.mainSelection;

            if (selectedId != null) {
                for (var tile : worldService.getLoadedTiles()) {
                    if (tile != null) {
                        selected = selectedId != null ? world.entityMap.get(selectedId) : null;
                        if (selected != null) {
                            additionalInspection.add(selected);
                            world.runByComponent(this::addComponent, selectedId);
                            currentInspection = additionalInspection.getFirst();
                            break;
                        }
                    }
                }
            } else {
                selected = null;
            }

            if (selectedId != null && selected == null) {
                currentInspection = repositories.getFirst();
            }
        }
    }

    private void addComponent(AbstractComponent abstractComponent) {
        additionalInspection.add(abstractComponent);
    }
}
