package com.pine.editor.panels.viewport.header;

import com.pine.common.injection.PInject;
import com.pine.editor.core.AbstractView;
import com.pine.editor.core.UIUtil;
import com.pine.editor.repository.EditorMode;
import com.pine.editor.repository.EditorRepository;
import com.pine.engine.service.resource.fbo.FBOService;
import com.pine.engine.service.resource.fbo.FBOTextureData;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImInt;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.pine.editor.core.dock.DockSpacePanel.FRAME_SIZE;

public class ViewportHeaderPanel extends AbstractView {
    private static final int FLAGS = ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoCollapse;
    private static final ImVec2 PADDING = new ImVec2(4, 4);
    private static final float HEIGHT = 35;

    @PInject
    public EditorRepository editorRepository;

    @PInject
    public FBOService fboService;

    private AbstractViewportSettingsPanel paintGizmo;
    private AbstractViewportSettingsPanel gizmo;
    private final ImInt bufferVisualization = new ImInt(0);
    private String[] samplerNames;
    private int[] samplerIds;
    private int viewBuffer = -1;
    private int defaultBuffer;
    private int prevSize = -1;


    public int getViewBuffer() {
        return viewBuffer;
    }

    public void setViewBuffer(int viewBuffer) {
        this.viewBuffer = viewBuffer;
        this.defaultBuffer = viewBuffer;
        bufferVisualization.set(0);
    }

    @Override
    public void onInitialize() {
        appendChild(gizmo = new GizmoSettingsPanel());
        appendChild(paintGizmo = new PaintingSettingsPanel());
    }

    @Override
    public void render() {
        AbstractViewportSettingsPanel currentPanel;
        if (editorRepository.editorMode == EditorMode.TRANSFORM) {
            currentPanel = gizmo;
        } else {
            currentPanel = paintGizmo;
        }
        ImGui.setNextWindowPos(ImGui.getWindowPosX(), ImGui.getWindowPosY() + FRAME_SIZE);
        ImGui.setNextWindowSize(ImGui.getWindowSizeX(), HEIGHT);
        ImGui.setNextWindowBgAlpha(.4f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, PADDING);
        ImGui.begin(imguiId, UIUtil.OPEN, FLAGS);
        ImGui.popStyleVar();
        currentPanel.render();

        UIUtil.dynamicSpacing(154);
        ImGui.setNextItemWidth(150);
        if(prevSize != fboService.data.size()){
            List<FBOTextureData> samplers = new ArrayList<>();
            for (var fbo : fboService.data.values()) {
                samplers.addAll(fbo.getSamplers());
            }
            samplerNames = new String[samplers.size() + 1];
            samplerNames[0] = "Default";
            samplerIds = new int[samplers.size()];
            for (int i = 0; i < samplers.size(); i++) {
                var sampler = samplers.get(i);
                samplerNames[i + 1] = sampler.name();
                samplerIds[i] = sampler.id();
            }
            prevSize = fboService.data.size();
        }
        if (ImGui.combo(imguiId, bufferVisualization, samplerNames)) {
            if (bufferVisualization.get() == 0) {
                viewBuffer = defaultBuffer;
            } else {
                viewBuffer = samplerIds[bufferVisualization.get() - 1];
            }
        }
        ImGui.end();

        currentPanel.renderOutside();
    }

}
