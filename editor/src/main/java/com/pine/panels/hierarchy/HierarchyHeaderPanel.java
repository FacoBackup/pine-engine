package com.pine.panels.hierarchy;

import com.pine.PInject;
import com.pine.component.InstancedSceneComponent;
import com.pine.service.RequestProcessingService;
import com.pine.service.request.AddEntityRequest;
import com.pine.theme.Icons;
import com.pine.view.AbstractView;
import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.ImString;

import java.util.List;

public class HierarchyHeaderPanel extends AbstractView {

    @PInject
    public RequestProcessingService requestProcessingService;

    private final ImString search = new ImString();

    @Override
    public void renderInternal() {
        if(ImGui.inputText("##hierarchySearch", search, ImGuiInputTextFlags.EnterReturnsTrue)){
            // TODO
        }
        ImGui.sameLine();
        if(ImGui.button(Icons.add, 25, 25)){
            requestProcessingService.addRequest(new AddEntityRequest(List.of(InstancedSceneComponent.class)));
        }
    }
}
