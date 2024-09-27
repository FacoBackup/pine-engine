package com.pine.panels.inspector;

import com.pine.PInject;
import com.pine.component.AbstractComponent;
import com.pine.component.EntityComponent;
import com.pine.component.FormPanel;
import com.pine.inspection.Inspectable;
import com.pine.repository.EditorSettingsRepository;
import com.pine.repository.EntitySelectionRepository;
import com.pine.service.RequestProcessingService;
import com.pine.service.world.WorldService;
import com.pine.service.world.request.AddComponentRequest;
import com.pine.service.world.request.UpdateFieldRequest;
import com.pine.dock.AbstractDockPanel;
import com.pine.theme.Icons;
import imgui.ImGui;
import imgui.flag.ImGuiCol;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.pine.theme.Icons.ONLY_ICON_BUTTON_SIZE;

public class InspectorPanel extends AbstractDockPanel {

    @PInject
    public EntitySelectionRepository selectionRepository;

    @PInject
    public RequestProcessingService requestProcessingService;

    @PInject
    public EditorSettingsRepository settingsRepository;

    @PInject
    public List<EntityComponent> components;

    @PInject
    public List<Inspectable> repositories;

    private final List<Inspectable> additionalInspectable = new ArrayList<>();

    @PInject
    public WorldService worldService;

    private Inspectable currentInspection;
    private Integer selected;
    private final List<String> types = new ArrayList<>();
    private FormPanel formPanel;
    private boolean isComponentInspected;

    @Override
    public void onInitialize() {
        types.add(Icons.add + " Add component");
        types.addAll(components.stream().map(EntityComponent::getTitle).toList());
        formPanel = appendChild(new FormPanel((dto, newValue) -> {
            requestProcessingService.addRequest(new UpdateFieldRequest(dto, newValue));
        }));
        repositories = repositories.stream().filter(a -> !(a instanceof AbstractComponent<?>)).toList();
    }

    @Override
    public void tick() {
        Integer first = selectionRepository.getMainSelection();
        if (!Objects.equals(first, selected)) {
            formPanel.setInspectable(repositories.getFirst());
            isComponentInspected = false;
            additionalInspectable.clear();
            selected = first;
            if (selected != null) {
                for (var component : worldService.getComponents(selected).values()) {
                    additionalInspectable.add((AbstractComponent<?>) component);
                }
            }
        }

        if (formPanel.getInspectable() != currentInspection) {
            formPanel.setInspectable(currentInspection);
        }
    }

    @Override
    public void renderInternal() {
        ImGui.columns(2, "##inspectorColumns", false);
        ImGui.setColumnWidth(0, 35);
        for (var repo : repositories) {
            renderOption(repo);
        }

        ImGui.spacing();
        ImGui.spacing();
        for (var additional : additionalInspectable) {
            renderOption(additional);
        }

        ImGui.nextColumn();
        if (selected != null && isComponentInspected) {
            if (ImGui.beginCombo(imguiId, types.getFirst())) {
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

    private void renderOption(Inspectable repo) {
        int popStyle = 0;
        if (Objects.equals(currentInspection, repo)) {
            ImGui.pushStyleColor(ImGuiCol.Button, settingsRepository.accentColor);
            popStyle++;
        }

        if (ImGui.button(repo.getIcon(), ONLY_ICON_BUTTON_SIZE, ONLY_ICON_BUTTON_SIZE)) {
            currentInspection = repo;
            isComponentInspected = false;
        }
        ImGui.popStyleColor(popStyle);
    }
}
