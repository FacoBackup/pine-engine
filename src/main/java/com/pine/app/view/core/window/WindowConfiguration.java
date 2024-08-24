package com.pine.app.view.core.window;

public class WindowConfiguration {
    private String title;
    private int width = 1280;
    private int height = 768;
    private boolean fullScreen = false;

    public WindowConfiguration(String title, int width, int height, boolean fullScreen) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.fullScreen = fullScreen;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(final int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(final int height) {
        this.height = height;
    }

    public boolean isFullScreen() {
        return fullScreen;
    }

    public void setFullScreen(final boolean fullScreen) {
        this.fullScreen = fullScreen;
    }
}
