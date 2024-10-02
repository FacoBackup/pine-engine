package com.pine;

import com.pine.dock.DockDTO;
import com.pine.dock.DockGroup;
import com.pine.dock.DockService;
import com.pine.panels.EditorHeaderPanel;
import com.pine.panels.ToasterPanel;
import com.pine.repository.EditorStateRepository;
import com.pine.repository.ThemeService;
import com.pine.service.ProjectService;
import com.pine.tools.ToolsModule;
import com.pine.view.View;
import imgui.ImVec4;

import java.util.Collections;
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