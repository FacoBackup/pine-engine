package com.pine.app.view.component.view;

import com.pine.app.view.component.View;
import com.pine.app.view.component.panel.AbstractPanel;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;

public class BlockView extends AbstractView {
    private boolean autoResize = false;
    private String label = "";

    public BlockView(View parent, String id, AbstractPanel panel) {
        super(parent, id, panel);
    }

    @Override
    public void render(long index) {
        if(!visible){
            return;
        }
        final String tempLabel = label + "##" + index;
        if (autoResize && ImGui.begin(tempLabel, ImGuiWindowFlags.AlwaysAutoResize) || ImGui.begin(tempLabel)) {
            for(var child : children){
                child.render(index);
                index++;
            }
        }
        ImGui.end();
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isAutoResize() {
        return autoResize;
    }

    public void setAutoResize(boolean autoResize) {
        this.autoResize = autoResize;
    }
}
