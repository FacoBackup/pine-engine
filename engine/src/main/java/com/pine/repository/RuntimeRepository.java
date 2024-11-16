package com.pine.repository;


import com.pine.injection.PBean;
import org.joml.Vector2f;

@PBean
public class RuntimeRepository {

    public boolean isFocused;
    private int displayW;
    private int displayH;

    private final Vector2f invResolution = new Vector2f();
    public boolean fasterPressed = false;
    public boolean forwardPressed = false;
    public boolean backwardPressed = false;
    public boolean leftPressed = false;
    public boolean rightPressed = false;
    public boolean upPressed = false;
    public boolean downPressed = false;
    public boolean mousePressed;
    public float mouseX = 0;
    public float mouseY = 0;
    public float normalizedMouseX = 0;
    public float normalizedMouseY = 0;
    public float viewportW = 0;
    public float viewportH = 0;
    public float viewportX = 0;
    public float viewportY = 0;

    public void setDisplayH(int displayH) {
        this.displayH = displayH;
    }

    public void setDisplayW(int displayW) {
        this.displayW = displayW;
    }

    public void setInvDisplayW(float invDisplayW) {
        invResolution.x = invDisplayW;
    }

    public void setInvDisplayH(float invDisplayH) {
        invResolution.y = invDisplayH;
    }

    public int getDisplayH() {
        return displayH;
    }

    public int getDisplayW() {
        return displayW;
    }

    public Vector2f getInvResolution() {
        return invResolution;
    }
}
