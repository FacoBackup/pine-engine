package com.jengine.app.view.editor;

import com.jengine.app.view.component.AbstractUI;
import com.jengine.app.view.component.ContainerUI;
import com.jengine.app.view.core.RuntimeWindow;
import com.jengine.app.view.core.state.FloatState;
import com.jengine.app.view.core.state.State;
import com.jengine.app.view.core.state.StringState;


public class WorldEditorWindow extends RuntimeWindow {
    public WorldEditorWindow() {
        super("World Editor");
    }

    @Override
    protected AbstractUI<?> setupUI() {
        return new ContainerUI("E", true);
    }

    //    @Override
    public void renderUI() {
//        ImGui.text("OS: [" + System.getProperty("os.name") + "] Arch: [" + System.getProperty("os.arch") + "]");
//        ImGui.text("Hello, World! ");
//        if (ImGui.button(Icons.SAVE + " Save")) {
//            count.setState(count.getState() + 1);
//        }
//        ImGui.sameLine();
//        ImGui.text(String.valueOf(count));
//        ImGui.inputText("string", str.getState(), ImGuiInputTextFlags.CallbackResize);
//        ImGui.text("Result: " + str);
//        ImGui.sliderFloat("float", flt.getState(), 0, 1);
//        ImGui.separator();
//        ImGui.text("Extra");
    }


    public int getWindowWidth() {
        return 960;
    }

    public String getWindowName() {
        return "World Editor";
    }

    public int getWindowHeight() {
        return 540;
    }

    public boolean isFullScreen() {
        return false;
    }
}