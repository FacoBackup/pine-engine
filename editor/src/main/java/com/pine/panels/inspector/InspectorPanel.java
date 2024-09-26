package com.pine.panels.inspector;

import com.pine.Icon;
import com.pine.PInject;
import com.pine.component.EntityComponent;
import com.pine.component.FormPanel;
import com.pine.inspection.InspectableRepository;
import com.pine.inspection.WithMutableData;
import com.pine.repository.EntitySelectionRepository;
import com.pine.service.RequestProcessingService;
import com.pine.service.world.WorldService;
import com.pine.service.world.request.AddComponentRequest;
import com.pine.service.world.request.UpdateFieldRequest;
import com.pine.ui.panel.AbstractWindowPanel;
import imgui.ImGui;
import imgui.ImVec4;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class InspectorPanel extends AbstractWindowPanel {
    private static final ImVec4 HOVERED_COLOR = new ImVec4(0.3f, 0.5f, 0.7f, 1.0f);

    @PInject
    public EntitySelectionRepository selectionRepository;

    @PInject
    public RequestProcessingService requestProcessingService;

    @PInject
    public List<EntityComponent> components;

    @PInject
    public List<InspectableRepository> repositories;

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
                        requestProcessingService.addRequest(new UpdateFieldRequest(dto, newValue));
                    }));
                    appendChild(formPanel);
                }
            }
        }
        super.tick();
    }

    @Override
    public void renderInternal() {
        ImGui.columns(2, "columns", false);
        ImGui.setColumnWidth(0, 35);

        ImGui.button(Icon.ANGLEDOUBLELEFT.codePoint, 27, 27);
        ImGui.button(Icon.EDIT.codePoint, 27, 27);
        ImGui.button(Icon.EGG.codePoint, 27, 27);
        ImGui.button(Icon.AD.codePoint, 27, 27);

        ImGui.nextColumn();
        if (selected != null) {
            if (ImGui.beginCombo(internalId, types.getFirst())) {
                for (int i = 1; i < types.size(); i++) {
                    String type = types.get(i);
                    if (ImGui.selectable(type)) {
                        EntityComponent entityComponent = components.get(i - 1);
                        requestProcessingService.addRequest(new AddComponentRequest(entityComponent.getClass(), selected));
                        selected = null;
                    }
                }
                ImGui.endCombo();
            }
        }
        super.renderInternal();
        ImGui.columns(1);
    }
}
