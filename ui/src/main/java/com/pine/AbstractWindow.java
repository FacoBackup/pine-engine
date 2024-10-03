package com.pine;

import com.pine.dock.DockPanel;
import com.pine.theme.Icons;
import com.pine.view.AbstractView;
import com.pine.view.View;
import imgui.*;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;
import java.util.Objects;

public abstract class AbstractWindow extends AbstractView implements Initializable {
    protected final DockPanel root = new DockPanel() {
        @Override
        protected ImVec4 getAccentColor() {
            return AbstractWindow.this.getAccentColor();
        }
    };

    @Override
    final public void onInitialize() {
        appendChild(root);
        onInitializeInternal();
        root.setHeader(getHeader());
        root.onInitialize();
    }

    protected abstract void onInitializeInternal();

    protected abstract View getHeader();

    @Override
    final public void render() {
        tick();
        ImGui.pushStyleColor(ImGuiCol.Button, getNeutralPalette());
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, getAccentColor());
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, getAccentColor());
        renderInternal();
        ImGui.popStyleColor(3);
    }

    protected abstract ImVec4 getNeutralPalette();

    protected abstract ImVec4 getAccentColor();
}
