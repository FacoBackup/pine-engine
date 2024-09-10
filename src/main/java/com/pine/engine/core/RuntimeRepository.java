package com.pine.engine.core;

public class RuntimeRepository {
    private boolean forwardPressed = false;
    private boolean backwardPressed = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean inputFocused = false;
    private float mouseX = 0;
    private float mouseY = 0;
    private float viewportW = 0;
    private float viewportH = 0;

    public int windowW = 0;
    public int windowH = 0;

    public String gridShaderId;
    public String planeMeshId;
    public String spriteShaderId;
    public String visibilityShaderId;
    public String toScreenShaderId;
    public String downscaleShaderId;
    public String bilateralBlurShaderId;
    public String bokehShaderId;
    public String irradianceShaderId;
    public String prefilteredShaderId;
    public String ssgiShaderId;
    public String mbShaderId;
    public String ssaoShaderId;
    public String boxBlurShaderId;
    public String directShadowsShaderId;
    public String omniDirectShadowsShaderId;
    public String compositionShaderId;
    public String bloomShaderId;
    public String lensShaderId;
    public String gaussianShaderId;
    public String upSamplingShaderId;
    public String atmosphereShaderId;

    public boolean isForwardPressed() {
        return forwardPressed;
    }

    public void setForwardPressed(boolean forwardPressed) {
        this.forwardPressed = forwardPressed;
    }

    public boolean isBackwardPressed() {
        return backwardPressed;
    }

    public void setBackwardPressed(boolean backwardPressed) {
        this.backwardPressed = backwardPressed;
    }

    public boolean isLeftPressed() {
        return leftPressed;
    }

    public void setLeftPressed(boolean leftPressed) {
        this.leftPressed = leftPressed;
    }

    public boolean isRightPressed() {
        return rightPressed;
    }

    public void setRightPressed(boolean rightPressed) {
        this.rightPressed = rightPressed;
    }

    public boolean isUpPressed() {
        return upPressed;
    }

    public void setUpPressed(boolean upPressed) {
        this.upPressed = upPressed;
    }

    public boolean isDownPressed() {
        return downPressed;
    }

    public void setDownPressed(boolean downPressed) {
        this.downPressed = downPressed;
    }

    public boolean isInputFocused() {
        return inputFocused;
    }

    public void setInputFocused(boolean inputFocused) {
        this.inputFocused = inputFocused;
    }

    public float getMouseX() {
        return mouseX;
    }

    public void setMouseX(float mouseX) {
        this.mouseX = mouseX;
    }

    public float getMouseY() {
        return mouseY;
    }

    public void setMouseY(float mouseY) {
        this.mouseY = mouseY;
    }

    public float getViewportW() {
        return viewportW;
    }

    public void setViewportW(float viewportW) {
        this.viewportW = viewportW;
    }

    public float getViewportH() {
        return viewportH;
    }

    public void setViewportH(float viewportH) {
        this.viewportH = viewportH;
    }

}
