package com.pine.app;

import com.pine.Engine;
import com.pine.PInject;
import com.pine.app.panels.console.ConsolePanel;
import com.pine.app.panels.files.FilesPanel;
import com.pine.app.panels.hierarchy.HierarchyPanel;
import com.pine.app.panels.inspector.InspectorPanel;
import com.pine.app.panels.viewport.ViewportPanel;
import com.pine.app.repository.EntitySelectionRepository;
import com.pine.app.service.ProjectDTO;
import com.pine.app.service.ProjectService;
import com.pine.common.messages.Message;
import com.pine.common.messages.MessageCollector;
import com.pine.common.messages.MessageSeverity;
import com.pine.component.AtmosphereComponent;
import com.pine.service.world.request.AddEntityRequest;
import com.pine.tools.ToolsConfigurationModule;
import com.pine.tools.ToolsModule;
import com.pine.ui.panel.DockDTO;
import com.pine.window.AbstractWindow;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiDir;
import imgui.flag.ImGuiWindowFlags;

import java.util.List;

import static com.pine.Engine.GLSL_VERSION;
import static com.pine.common.messages.MessageCollector.MESSAGE_DURATION;

public class EditorWindow extends AbstractWindow {

    @PInject
    public ProjectService projectService;

    @PInject
    public EntitySelectionRepository selectionRepository;

    @PInject
    public Engine engine;

    @Override
    public void onInitialization() {
        engine.prepare(displayW, displayH, (String message, Boolean isError) -> {
            MessageCollector.pushMessage(message, isError ? MessageSeverity.ERROR : MessageSeverity.SUCCESS);
        });
        engine.addModules(List.of(new ToolsModule(), new ToolsConfigurationModule(selectionRepository.getSelected())));
        engine.requestTask.addRequest(new AddEntityRequest(List.of(AtmosphereComponent.class)));
    }

    @Override
    protected List<DockDTO> getDockSpaces() {
        DockDTO dockCenter = new DockDTO("Viewport", ViewportPanel.class);
        DockDTO dockRightUp = new DockDTO("Hierarchy", HierarchyPanel.class);
        DockDTO dockRightDown = new DockDTO("Inspector", InspectorPanel.class);
        DockDTO dockDown = new DockDTO("Console", ConsolePanel.class);
        DockDTO dockDownRight = new DockDTO("Files", FilesPanel.class);

        dockCenter.setOrigin(null);
        dockCenter.setSplitDir(ImGuiDir.Right);
        dockCenter.setSizeRatioForNodeAtDir(0.17f);
        dockCenter.setOutAtOppositeDir(null);

        dockRightUp.setOrigin(dockCenter);
        dockRightUp.setSplitDir(ImGuiDir.Down);
        dockRightUp.setSizeRatioForNodeAtDir(0.4f);
        dockRightUp.setOutAtOppositeDir(dockCenter);

        dockRightDown.setOrigin(dockRightUp);
        dockRightDown.setSplitDir(ImGuiDir.Down);
        dockRightDown.setSizeRatioForNodeAtDir(0.6f);
        dockRightDown.setOutAtOppositeDir(dockRightUp);

        dockDown.setOrigin(null);
        dockDown.setSplitDir(ImGuiDir.Down);
        dockDown.setSizeRatioForNodeAtDir(0.22f);
        dockDown.setOutAtOppositeDir(null);

        dockDownRight.setOrigin(dockDown);
        dockDownRight.setSplitDir(ImGuiDir.Right);
        dockDownRight.setSizeRatioForNodeAtDir(0.5f);
        dockDownRight.setOutAtOppositeDir(dockDown);

        return List.of(
                dockCenter,
                dockRightUp,
                dockRightDown,
                dockDown,
                dockDownRight
        );
    }

    @Override
    protected void renderInternal() {
        Message[] messages = MessageCollector.getMessages();
        for (int i = 0; i < MessageCollector.MAX_MESSAGES; i++) {
            var message = messages[i];
            if (message == null) {
                continue;
            }
            if (System.currentTimeMillis() - message.getDisplayStartTime() > MESSAGE_DURATION) {
                messages[i] = null;
                continue;
            }
            ImVec2 viewportDimensions = getViewDocument().getViewportDimensions();
            ImGui.setNextWindowPos(5, viewportDimensions.y - 40 * (i + 1));
            ImGui.setNextWindowSize(viewportDimensions.x * .35F, 35);
            ImGui.pushStyleColor(ImGuiCol.Border, message.severity().getColor());
            ImGui.pushStyleColor(ImGuiCol.WindowBg, message.severity().getColor());
            ImGui.begin("##toaster" + i, ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoSavedSettings);
            ImGui.popStyleColor();
            ImGui.popStyleColor();
            ImGui.text(message.message());
            ImGui.end();
        }
    }

    @Override
    protected String getGlslVersion(){
        return GLSL_VERSION;
    }

    public String getWindowName() {
        ProjectDTO currentProject = projectService.getCurrentProject();
        if(currentProject != null) {
            return currentProject.getName();
        }
        return "New Project - Pine Engine";
    }

    public Engine getEngine() {
        return engine;
    }
}