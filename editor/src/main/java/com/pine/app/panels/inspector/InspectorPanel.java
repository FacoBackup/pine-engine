package com.pine.app.panels.inspector;

import com.pine.Icon;
import com.pine.PInject;
import com.pine.app.component.FormPanel;
import com.pine.app.repository.EntitySelectionRepository;
import com.pine.component.EntityComponent;
import com.pine.inspection.WithMutableData;
import com.pine.service.world.WorldService;
import com.pine.service.world.request.AddComponentRequest;
import com.pine.service.world.request.UpdateFieldRequest;
import com.pine.tasks.RequestProcessingTask;
import com.pine.ui.panel.AbstractWindowPanel;
import imgui.ImGui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class InspectorPanel extends AbstractWindowPanel {
    @PInject
    public EntitySelectionRepository selectionRepository;

    @PInject
    public RequestProcessingTask requestProcessingTask;

    @PInject
    public List<EntityComponent> components;

    @PInject
    public WorldService worldService;

    private Integer selected;
    private final List<FormPanel> formPanels = new LinkedList<>();
    private final List<String> types = new ArrayList<>();

    @Override
    protected String getTitle() {
        return "Inspector";
    }

    @Override
    public void onInitialize() {
        types.add(Icon.PLUS.codePoint + " Add component");
        types.addAll(components.stream().map(EntityComponent::getLabel).toList());
    }

    @Override
    public void tick() {
        Integer first = selectionRepository.getMainSelection();
        if (!Objects.equals(first, selected)) {
            selected = first;
            formPanels.forEach(this::removeChild);
            if (selected != null) {
                for (var component : worldService.getComponents(selected).values()) {
                    FormPanel formPanel;
                    formPanels.add(formPanel = new FormPanel((WithMutableData) component, (dto, newValue) -> {
                        requestProcessingTask.addRequest(new UpdateFieldRequest(dto, newValue));
                    }));
                    appendChild(formPanel);
                }
            }
        }
        super.tick();
    }

    @Override
    public void renderInternal() {
        if (selected != null) {
            if (ImGui.beginCombo(internalId, types.getFirst())) {
                for (int i = 1; i < types.size(); i++) {
                    String type = types.get(i);
                    if (ImGui.selectable(type)) {
                        EntityComponent entityComponent = components.get(i - 1);
                        requestProcessingTask.addRequest(new AddComponentRequest(entityComponent.getClass(), selected));
                        selected = null;
                    }
                }
                ImGui.endCombo();
            }
        }
        super.renderInternal();
    }
}
