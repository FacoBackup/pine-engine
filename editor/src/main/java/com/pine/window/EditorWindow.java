package com.pine.window;

import com.pine.*;
import com.pine.dock.DockDTO;
import com.pine.dock.DockGroup;
import com.pine.dock.DockService;
import com.pine.panels.EditorHeaderPanel;
import com.pine.panels.ToasterPanel;
import com.pine.repository.SettingsRepository;
import com.pine.service.ThemeService;
import com.pine.service.ProjectService;
import com.pine.tools.ToolsModule;
import com.pine.view.View;
import imgui.ImVec4;

import java.util.Collections;
import java.util.List;


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
    public SettingsRepository settingsRepository;

    @PInject
    public WindowService windowService;

    @Override
    public void onInitializeInternal() {
        engine.prepare(windowService.getDisplayW(), windowService.getDisplayH());
        engine.addModules(List.of(new ToolsModule()));
        appendChild(new ToasterPanel());
        try {
            DockDTO dockCenter = new DockDTO(EditorDock.Viewport);
            DockDTO rightUp = new DockDTO(EditorDock.Hierarchy);
            DockDTO rightDown = new DockDTO(EditorDock.Inspector);
            DockDTO downLeft = new DockDTO(EditorDock.Console);
            DockDTO downRight = new DockDTO(EditorDock.Files);

            dockCenter.setSizeRatioForNodeAtDir(0.17f);
            rightUp.setSizeRatioForNodeAtDir(0.4f);
            rightDown.setSizeRatioForNodeAtDir(0.6f);
            downLeft.setSizeRatioForNodeAtDir(0.22f);
            downRight.setSizeRatioForNodeAtDir(0.5f);

            dockService.setDockGroupTemplate(new DockGroup("Viewport", dockCenter, List.of(downLeft, downRight), Collections.emptyList(), List.of(rightUp, rightDown)));
            dockService.createDockGroup();
        } catch (Exception e) {
            getLogger().error(e.getMessage(), e);
        }
        projectService.loadProject();
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

    public String getWindowName() {
        return "Pine Engine";
    }

    @Override
    public void tick() {
        themeService.tick();
    }
}