package com.pine.panels.hierarchy;

import com.pine.core.view.AbstractView;
import com.pine.injection.PInject;
import com.pine.repository.EditorRepository;
import com.pine.service.rendering.RequestProcessingService;
import com.pine.service.request.AddEntityRequest;
import com.pine.theme.Icons;
import imgui.ImGui;
import imgui.type.ImString;

import java.util.List;

import static com.pine.theme.Icons.ONLY_ICON_BUTTON_SIZE;

public class HierarchyHeaderPanel extends AbstractView {
    private static final String FILTER_OFF_LABEL = Icons.filter_list_off + "##hierarchyFilter";
    private static final String FILTER_ON_LABEL = Icons.filter_list + "##hierarchyFilter";
    private static final String ADD_LABEL = Icons.add + "##hierarchyAdd";

    @PInject
    public RequestProcessingService requestProcessingService;

    @PInject
    public EditorRepository editorRepository;

    private final ImString search;

    public HierarchyHeaderPanel(ImString search) {
        this.search = search;
    }

    @Override
    public void render() {
        ImGui.inputText("##hierarchySearch", search);
        ImGui.sameLine();
        if (ImGui.button(ADD_LABEL, ONLY_ICON_BUTTON_SIZE, ONLY_ICON_BUTTON_SIZE)) {
            requestProcessingService.addRequest(new AddEntityRequest());
        }
        ImGui.sameLine();
        boolean show = editorRepository.showOnlyEntitiesHierarchy;
        if (ImGui.button(show ? FILTER_OFF_LABEL : FILTER_ON_LABEL, ONLY_ICON_BUTTON_SIZE, ONLY_ICON_BUTTON_SIZE)) {
            editorRepository.showOnlyEntitiesHierarchy = !show;
        }
    }
}
