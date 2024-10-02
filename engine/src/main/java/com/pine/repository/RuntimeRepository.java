package com.pine.repository;


import com.pine.PBean;

@PBean
public class RuntimeRepository  {

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
}
