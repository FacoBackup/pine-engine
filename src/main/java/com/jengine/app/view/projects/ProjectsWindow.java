package com.jengine.app.view.projects;

import com.jengine.app.view.component.AbstractUI;
import com.jengine.app.view.component.ButtonUI;
import com.jengine.app.view.component.ContainerUI;
import com.jengine.app.view.component.ListUI;
import com.jengine.app.view.core.RuntimeWindow;
import com.jengine.app.view.core.state.ConstStringState;
import com.jengine.app.view.core.state.FloatState;
import com.jengine.app.view.core.state.State;
import com.jengine.app.view.core.state.StringState;
import com.jengine.app.view.core.window.Icons;
import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;

import java.util.ArrayList;
import java.util.List;

public class ProjectsWindow extends RuntimeWindow {
    private final List<ProjectDTO> rows = new ArrayList<>();

    public ProjectsWindow() {
        super("Projects");
    }

    @Override
    public AbstractUI<?> setupUI() {
        return new ContainerUI("Projects", false,
                new ListUI<>(rows) {
                    @Override
                    public void renderRow(ProjectDTO row, int i) {
                        StringState nameState = row.getName();
                        StringState pathState = row.getPath();

                        if (row.isEditing()) {
                            ImGui.setNextItemWidth(150);
                            if (ImGui.inputText("##edit" + i, nameState.getState(), ImGuiInputTextFlags.EnterReturnsTrue)) {
                                row.setEditing(false);
                            }
                            ImGui.sameLine();
                            if (ImGui.button("Save##save" + i)) {
                                row.setEditing(false);
                            }
                        } else {
                            ImGui.text(nameState.toString());
                            ImGui.sameLine();
                            if (ImGui.button("Edit##edit" + i)) {
                                row.setEditing(true);
                            }

                            if (ImGui.button("Open##open" + i)) {
                                row.setEditing(true);
                            }
                        }

                        ImGui.sameLine();
                        if (ImGui.button("Remove##remove" + i)) {
                            rows.remove(i);
                            i--;
                        }
                    }
                },
                new ButtonUI(ConstStringState.of("New project")) {
                    @Override
                    public void onClick() {
                        rows.add(new ProjectDTO());
                    }
                });
    }


    public int getWindowWidth() {
        return 960;
    }

    public String getWindowName() {
        return "Projects";
    }

    public int getWindowHeight() {
        return 540;
    }

    public boolean isFullScreen() {
        return false;
    }
}