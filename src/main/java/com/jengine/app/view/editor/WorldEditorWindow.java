package com.jengine.app.view.editor;

import com.jengine.app.view.component.View;
import com.jengine.app.view.component.panel.AbstractPanel;
import com.jengine.app.view.component.view.AbstractView;
import com.jengine.app.view.component.view.BlockView;
import com.jengine.app.view.core.RuntimeWindow;


public class WorldEditorWindow extends RuntimeWindow {
    public WorldEditorWindow() {
        super("World Editor");
    }

    @Override
    protected AbstractPanel setupUI() {
        return null;
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