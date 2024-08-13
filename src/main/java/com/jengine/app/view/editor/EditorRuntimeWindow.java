package com.jengine.app.view.editor;

import com.jengine.app.view.core.RuntimeWindow;
import com.jengine.app.view.core.state.FloatState;
import com.jengine.app.view.core.state.State;
import com.jengine.app.view.core.state.StringState;
import com.jengine.app.view.core.window.Icons;
import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;

public class EditorRuntimeWindow extends RuntimeWindow {
    private final State<Integer> count = new State<>(0);
    private final StringState str = new StringState(5);
    private final FloatState flt = new FloatState(1);

    public EditorRuntimeWindow() {
        super( "World Editor");
    }

    @Override
    public void renderUI() {
        ImGui.text("OS: [" + System.getProperty("os.name") + "] Arch: [" + System.getProperty("os.arch") + "]");
        ImGui.text("Hello, World! ");
        if (ImGui.button(Icons.SAVE + " Save")) {
            count.setState(count.getState()+1);
        }
        ImGui.sameLine();
        ImGui.text(String.valueOf(count));
        ImGui.inputText("string", str.getState(), ImGuiInputTextFlags.CallbackResize);
        ImGui.text("Result: " + str);
        ImGui.sliderFloat("float", flt.getState(), 0, 1);
        ImGui.separator();
        ImGui.text("Extra");
    }

}