package com.pine.panels.inspector;

import com.pine.component.AbstractComponent;
import com.pine.component.ComponentType;
import com.pine.component.Entity;
import com.pine.core.dock.AbstractDockPanel;
import com.pine.injection.PInject;
import com.pine.inspection.Inspectable;
import com.pine.panels.component.FormPanel;
import com.pine.repository.EditorRepository;
import com.pine.service.grid.HashGridService;
import com.pine.service.rendering.RequestProcessingService;
import com.pine.service.request.AddComponentRequest;
import com.pine.service.request.UpdateFieldRequest;
import com.pine.theme.Icons;
import imgui.ImGui;
import imgui.flag.ImGuiCol;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static com.pine.theme.Icons.ONLY_ICON_BUTTON_SIZE;

public class InspectorPanel extends AbstractDockPanel {
    @PInject
    public RequestProcessingService requestProcessingService;
    @PInject
    public EditorRepository editorRepository;
    @PInject
    public HashGridService hashGridService;
    @PInject
    public List<Inspectable> repositories;

    private final List<Inspectable> additionalInspection = new ArrayList<>();

    private Inspectable currentInspection;
    private Entity selected;
    private String selectedId;
    private final List<String> types = new ArrayList<>();
    private FormPanel formPanel;

    @Override
    public void onInitialize() {
        types.add(Icons.add + " Add component");
        types.addAll(Stream.of(ComponentType.values()).map(a -> a.getIcon() + a.getTitle()).toList());
        formPanel = appendChild(new FormPanel((dto, newValue) -> requestProcessingService.addRequest(new UpdateFieldRequest(dto, newValue))));
        currentInspection = repositories.getFirst();
    }

    @Override
    public void render() {
        tick();
        ImGui.columns(2, "##inspectorColumns" + imguiId, false);
        ImGui.setColumnWidth(0, 30);
        for (var repo : repositories) {
            renderOption(repo);
        }

        ImGui.spacing();
        ImGui.spacing();
        for (var additional : additionalInspection) {
            if (additional != null) {
                renderOption(additional);
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
            super.render();
        }
        ImGui.endChild();
        ImGui.columns(1);
    }

    private void tick() {
        if (!Objects.equals(editorRepository.mainSelection, selectedId)) {
            additionalInspection.clear();
            selectedId = editorRepository.mainSelection;

            if(selectedId != null) {
                for (var tile : hashGridService.getLoadedTiles()) {
                    if (tile != null) {
                        selected = selectedId != null ? tile.getWorld().entityMap.get(selectedId) : null;
                        if (selected != null) {
                            additionalInspection.add(selected);
                            tile.getWorld().runByComponent(this::addComponent, selectedId);
                            currentInspection = additionalInspection.getFirst();
                            break;
                        }
                    }
                }
            }else{
                selected = null;
            }

            if(selectedId != null && selected == null) {
                currentInspection = repositories.getFirst();
            }
        }
    }

    private void renderOption(Inspectable repo) {
        int popStyle = 0;
        if (Objects.equals(currentInspection, repo)) {
            ImGui.pushStyleColor(ImGuiCol.Button, editorRepository.accent);
            popStyle++;
        }

        if (ImGui.button(repo.getIcon(), ONLY_ICON_BUTTON_SIZE, ONLY_ICON_BUTTON_SIZE)) {
            currentInspection = repo;
        }
        ImGui.popStyleColor(popStyle);
    }

    private void addComponent(AbstractComponent abstractComponent) {
        additionalInspection.add(abstractComponent);
    }
}
