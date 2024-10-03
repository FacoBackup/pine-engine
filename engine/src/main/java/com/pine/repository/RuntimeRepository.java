package com.pine.repository;


import com.pine.injection.PBean;

@PBean
public class RuntimeRepository  {


    private int displayW;
    private int displayH;
    private float invDisplayW;
    private float invDisplayH;

    public boolean fasterPressed = false;
    public boolean forwardPressed = false;
    public boolean backwardPressed = false;
    public boolean leftPressed = false;
    public boolean rightPressed = false;
    public boolean upPressed = false;
    public boolean downPressed = false;
    public float mouseX = 0;
    public float mouseY = 0;
    public float viewportW = 0;
    public float viewportH = 0;

    public void setDisplayH(int displayH) {
        this.displayH = displayH;
    }

    public void setDisplayW(int displayW) {
        this.displayW = displayW;
    }

    public void setInvDisplayW(float invDisplayW) {
        this.invDisplayW = invDisplayW;
    }

    public void setInvDisplayH(float invDisplayH) {
        this.invDisplayH = invDisplayH;
    }

    public int getDisplayH() {
        return displayH;
    }

    public int getDisplayW() {
        return displayW;
    }

    public float getInvDisplayH() {
        return invDisplayH;
    }

    public float getInvDisplayW() {
        return invDisplayW;
    }
}
