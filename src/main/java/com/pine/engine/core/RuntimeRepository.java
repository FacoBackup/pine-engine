package com.pine.engine.core;

import com.pine.engine.Engine;
import com.pine.engine.core.service.EngineInjectable;

public class RuntimeRepository implements EngineInjectable {
    public final int displayW;
    public final int displayH;
    
    public boolean forwardPressed = false;
    public boolean backwardPressed = false;
    public boolean leftPressed = false;
    public boolean rightPressed = false;
    public boolean upPressed = false;
    public boolean downPressed = false;
    public boolean inputFocused = false;
    public float mouseX = 0;
    public float mouseY = 0;
    public float viewportW = 0;
    public float viewportH = 0;

    public RuntimeRepository(int displayW, int displayH) {
        this.displayW = displayW;
        this.displayH = displayH;
    }

    public boolean isForwardPressed() {
        return forwardPressed;
    }

    public boolean isBackwardPressed() {
        return backwardPressed;
    }

    public boolean isLeftPressed() {
        return leftPressed;
    }

    public boolean isRightPressed() {
        return rightPressed;
    }

    public boolean isUpPressed() {
        return upPressed;
    }

    public boolean isDownPressed() {
        return downPressed;
    }

    public boolean isInputFocused() {
        return inputFocused;
    }

    public float getMouseX() {
        return mouseX;
    }

    public float getMouseY() {
        return mouseY;
    }

    public float getViewportW() {
        return viewportW;
    }

    public float getViewportH() {
        return viewportH;
    }
}
