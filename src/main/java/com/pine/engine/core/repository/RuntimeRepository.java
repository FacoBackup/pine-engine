package com.pine.engine.core.repository;


import com.pine.engine.core.EngineInjectable;

@EngineInjectable
public class RuntimeRepository  {

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
