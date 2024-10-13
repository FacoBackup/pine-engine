package com.pine.panels.inspector;

import com.pine.component.AbstractComponent;
import com.pine.component.ComponentType;
import com.pine.component.Entity;
import com.pine.core.dock.AbstractDockPanel;
import com.pine.injection.PInject;
import com.pine.inspection.Inspectable;
import com.pine.panels.component.FormPanel;
import com.pine.repository.EditorRepository;
import com.pine.repository.WorldRepository;
import com.pine.repository.fs.ResourceEntry;
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
    public List<Inspectable> repositories;

    public Inspectable selectedFile;

    private final List<Inspectable> additionalInspectable = new ArrayList<>();

    private Inspectable currentInspection;
    private Entity selected;
    private final List<String> types = new ArrayList<>();
    private FormPanel formPanel;
    private ResourceEntry previousSelectedEntry;

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
        ImGui.columns(2, "##inspectorColumns", false);
        ImGui.setColumnWidth(0, 35);
        for (var repo : repositories) {
            renderOption(repo);
        }

        ImGui.spacing();
        ImGui.spacing();
        for (var additional : additionalInspectable) {
            if (additional != null) {
                renderOption(additional);
            }
        }

        ImGui.spacing();
        ImGui.spacing();
        if (selectedFile != null) {
            renderOption(selectedFile);
        }

        ImGui.nextColumn();
        if (ImGui.beginChild(imguiId + "form")) {
            if (selected != null && additionalInspectable.contains(currentInspection)) {
                if (ImGui.beginCombo(imguiId, types.getFirst())) {
                    for (int i = 1; i < types.size(); i++) {
                        String type = types.get(i);
                        if (ImGui.selectable(type)) {
                            ComponentType entityComponent = ComponentType.values()[i - 1];
                            requestProcessingService.addRequest(new AddComponentRequest(entityComponent, selected));
                            selected = null;
                        }
                    }
                    ImGui.endCombo();
                }
            }
            super.render();
            ImGui.endChild();
        }
        ImGui.columns(1);
    }

    private void tick() {
        if (editorRepository.mainSelection != selected) {
            additionalInspectable.clear();
            selected = editorRepository.mainSelection;
            additionalInspectable.add(selected);
            if (selected != null) {
                additionalInspectable.addAll(selected.components.values());
                currentInspection = additionalInspectable.getFirst();
            } else {
                currentInspection = repositories.getFirst();
            }
        }

        if (formPanel.getInspectable() != currentInspection) {
            formPanel.setInspectable(currentInspection);
        }

        if (previousSelectedEntry != editorRepository.inspectFile) {
            previousSelectedEntry = editorRepository.inspectFile;
            if (previousSelectedEntry != null) {
                selectedFile = currentInspection = previousSelectedEntry.streamableResource;
            } else {
                selectedFile = null;
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
}
