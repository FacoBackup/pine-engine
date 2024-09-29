package com.pine;

import com.pine.dock.DockDTO;
import com.pine.dock.DockService;
import com.pine.panels.EditorHeaderPanel;
import com.pine.panels.ToasterPanel;
import com.pine.repository.EditorStateRepository;
import com.pine.repository.ThemeService;
import com.pine.service.ProjectService;
import com.pine.tools.ToolsModule;
import com.pine.view.View;
import imgui.ImVec4;
import imgui.flag.ImGuiDir;

import java.util.List;

import static com.pine.Engine.GLSL_VERSION;

public class EditorWindow extends AbstractWindow {
    @PInject
    public ProjectService projectService;

    @PInject
    public Engine engine;

    @PInject
    public DockService dockService;

    @PInject
    public ThemeService themeService;

    @PInject
    public EditorStateRepository settingsRepository;

    @Override
    public void onInitializeInternal() {
        themeService.tick();
        engine.prepare(displayW, displayH);
        engine.addModules(List.of(new ToolsModule()));
        appendChild(new ToasterPanel());

        DockDTO dockCenter = new DockDTO(EditorDock.Viewport);
        DockDTO rightUp = new DockDTO(EditorDock.Hierarchy);
        DockDTO rightDown = new DockDTO(EditorDock.Inspector);
        DockDTO downLeft = new DockDTO(EditorDock.Console);
        DockDTO downRight = new DockDTO(EditorDock.Files);

        dockCenter.setOrigin(null);
        dockCenter.setSplitDir(ImGuiDir.Right);
        dockCenter.setSizeRatioForNodeAtDir(0.17f);
        dockCenter.setOutAtOppositeDir(null);

        rightUp.setOrigin(dockCenter);
        rightUp.setSplitDir(ImGuiDir.Down);
        rightUp.setSizeRatioForNodeAtDir(0.4f);
        rightUp.setOutAtOppositeDir(dockCenter);

        rightDown.setOrigin(rightUp);
        rightDown.setSplitDir(ImGuiDir.Down);
        rightDown.setSizeRatioForNodeAtDir(0.6f);
        rightDown.setOutAtOppositeDir(rightUp);

        downLeft.setOrigin(null);
        downLeft.setSplitDir(ImGuiDir.Down);
        downLeft.setSizeRatioForNodeAtDir(0.22f);
        downLeft.setOutAtOppositeDir(null);

        downRight.setOrigin(downLeft);
        downRight.setSplitDir(ImGuiDir.Right);
        downRight.setSizeRatioForNodeAtDir(0.5f);
        downRight.setOutAtOppositeDir(downLeft);

        dockService.getCurrentDockGroup().docks.addAll(List.of(dockCenter, rightUp, rightDown, downLeft, downRight));
        dockService.setDockGroupTemplate(dockService.getCurrentDockGroup());

        projectService.loadProject();
    }

    @Override
    protected float[] getBackgroundColor() {
        return themeService.BACKGROUND_COLOR;
    }

    @Override
    protected ImVec4 getNeutralPalette() {
        return themeService.neutralPalette;
    }

    @Override
    protected ImVec4 getAccentColor() {
        return settingsRepository.getAccentColor();
    }

    @Override
    protected View getHeader() {
        return new EditorHeaderPanel();
    }

    @Override
    protected String getGlslVersion() {
        return GLSL_VERSION;
    }

    public String getWindowName() {
        return "Pine Engine";
    }

    @Override
    public void tick() {
        themeService.tick();
    }
}