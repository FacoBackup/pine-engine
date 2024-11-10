package com.pine.window;

import com.pine.Engine;
import com.pine.core.AbstractWindow;
import com.pine.core.WindowService;
import com.pine.core.view.AbstractView;
import com.pine.injection.PInject;
import com.pine.panels.ToasterPanel;
import com.pine.panels.header.EditorHeaderPanel;
import com.pine.panels.viewport.FullScreenViewportPanel;
import com.pine.repository.EditorRepository;
import com.pine.service.ProjectService;
import com.pine.service.ThemeService;
import com.pine.service.serialization.SerializationService;
import com.pine.tools.ToolsModule;
import imgui.ImGui;
import imgui.ImVec4;
import imgui.flag.ImGuiKey;

import java.util.List;

import static com.pine.core.dock.DockPanel.FLAGS;
import static com.pine.core.dock.DockPanel.OPEN;


public class EditorWindow extends AbstractWindow {
    @PInject
    public ProjectService projectService;

    @PInject
    public Engine engine;

    @PInject
    public ThemeService themeService;

    @PInject
    public EditorRepository editorRepository;

    @PInject
    public WindowService windowService;

    @PInject
    public SerializationService serializationRepository;

    private boolean isInitialized = false;
    private FullScreenViewportPanel fullscreen;

    @Override
    public void onInitialize() {
        super.onInitialize();
        projectService.loadProject();
        appendChild(new ToasterPanel());
    }

    @Override
    public ImVec4 getNeutralPalette() {
        return themeService.neutralPalette;
    }

    @Override
    public ImVec4 getAccentColor() {
        return editorRepository.accent;
    }

    @Override
    public AbstractView getHeader() {
        return new EditorHeaderPanel();
    }

    @Override
    public void render() {
        startTracking();
        themeService.tick();
        if (serializationRepository.isDeserializationDone()) {
            if (!isInitialized) {
                windowService.maximize();
                engine.start(windowService.getDisplayW(), windowService.getDisplayH(), List.of(new ToolsModule()), projectService.getProjectDirectory());
//                var r = new AddEntityRequest(List.of(ComponentType.MESH));
//                for(int i = 0; i < 100; i++){
//                    for(int j = 0; j < 100; j++){
//                        re.addRequest(r);
//                        var entityId = r.getResponse().id;
//                        re.worldRepository.bagMeshComponent.get(entityId).lod0 = "e0a8f289-c822-473b-a0ae-7485816d5b9c";
//                        re.worldRepository.bagMeshComponent.get(entityId).isCullingEnabled = false;
//                        re.worldRepository.bagTransformationComponent.get(entityId).translation.x = i + 2;
//                        re.worldRepository.bagTransformationComponent.get(entityId).translation.z = j + 2;
//                    }
//                }

                appendChild(fullscreen = new FullScreenViewportPanel());
                removeChild(fullscreen);
                isInitialized = true;
            }
            if (editorRepository.fullScreen) {
                fullscreen.render();
                if (ImGui.isKeyDown(ImGuiKey.Escape)) {
                    editorRepository.fullScreen = false;
                }
            } else {
                super.render();
            }
        } else {
            ImGui.begin("##windowLoader", OPEN, FLAGS);
            ImGui.text("Pine Engine");
            ImGui.text("Loading scene...");
            ImGui.end();
        }
        endTracking();
    }

    @Override
    public float getWindowScaleX() {
        return .4f;
    }

    @Override
    public float getWindowScaleY() {
        return .4f;
    }
}