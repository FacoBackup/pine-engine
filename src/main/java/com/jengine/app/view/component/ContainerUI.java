package com.jengine.app.view.component;

import com.jengine.app.view.core.state.ConstStringState;
import com.jengine.app.view.core.state.State;
import com.jengine.app.view.core.state.StringState;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;

public class ContainerUI extends AbstractUI<State<?>> {
    private final boolean autoResize;

    public ContainerUI(String state, boolean autoResize, AbstractUI<?>... children) {
        super(new ConstStringState(state), children);
        this.autoResize = autoResize;
    }

    public ContainerUI(StringState state, boolean autoResize, AbstractUI<?>... children) {
        super(state, children);
        this.autoResize = autoResize;
    }

    @Override
    public void render() {
        if (autoResize && ImGui.begin(state.toString(), ImGuiWindowFlags.AlwaysAutoResize) || ImGui.begin(state.toString())) {
            for(var child : children){
                child.render();
            }
        }
        ImGui.end();
    }
}
