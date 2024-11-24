package com.pine.editor.panels.component;

import com.pine.editor.core.AbstractView;
import imgui.ImGui;
import imgui.ImVec4;
import imgui.flag.ImGuiCol;
import imgui.type.ImString;

import java.util.ArrayList;
import java.util.List;

public class AccordionPanel extends AbstractView {
    public String title;
    private final List<AbstractFieldView> views = new ArrayList<>();

    public List<AbstractFieldView> getViews() {
        return views;
    }

    public void append(AbstractFieldView view) {
        appendChild(view);
        views.add(view);
    }

    @Override
    public void render() {
        if (title.isEmpty()) {
            renderInternal();
            return;
        }

        if (ImGui.collapsingHeader(title + imguiId)) {
            renderInternal();
        }
    }

    private void renderInternal() {
        for (var view : views) {
            view.render();
        }
    }
}
