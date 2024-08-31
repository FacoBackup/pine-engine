package com.pine.app.editor;

import com.pine.app.core.RuntimeWindow;


public class WorldEditorWindow extends RuntimeWindow {

    @Override
    public void onInitialize() {
        super.onInitialize();
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