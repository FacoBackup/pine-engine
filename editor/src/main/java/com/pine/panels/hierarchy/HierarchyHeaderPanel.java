package com.pine.panels.hierarchy;

import com.pine.Engine;
import com.pine.Icon;
import com.pine.PInject;
import com.pine.component.InstancedSceneComponent;
import com.pine.service.world.request.AddEntityRequest;
import com.pine.ui.view.AbstractView;
import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.ImString;

import java.util.List;

public class HierarchyHeaderPanel extends AbstractView {

    @PInject
    public Engine engine;

    private final ImString search = new ImString();

    @Override
    public void renderInternal() {
        if(ImGui.inputText("##hierarchySearch", search, ImGuiInputTextFlags.EnterReturnsTrue)){
            // TODO
        }
        ImGui.sameLine();
        if(ImGui.button(Icon.PLUS.codePoint, 35, 35)){
            engine.addRequest(new AddEntityRequest(List.of(InstancedSceneComponent.class)));
        }
    }
}
