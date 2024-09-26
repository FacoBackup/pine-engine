package com.pine;

import com.pine.dock.DockDescription;
import com.pine.theme.Icon;

public enum EditorDock implements DockDescription {
    Viewport("Viewport", Icon.GLOBE.codePoint, 0, 0),
    Hierarchy("Hierarchy", Icon.CODEBRANCH.codePoint),
    Inspector("Inspector", Icon.SEARCH.codePoint),
    Console("Console", Icon.TERMINAL.codePoint),
    Files("Files", Icon.FOLDEROPEN.codePoint);

    private final String title;
    private final String codePoint;
    private final float paddingX;
    private final float paddingY;

    EditorDock(String title, String codePoint) {
        this(title, codePoint, -1, -1);
    }

    EditorDock(String title, String codePoint, float paddingX, float paddingY) {
        this.title = title;
        this.codePoint = codePoint;
        this.paddingX = paddingX;
        this.paddingY = paddingY;
    }


    @Override
    public float getPaddingX() {
        return paddingX;
    }

    @Override
    public float getPaddingY() {
        return paddingY;
    }

    @Override
    public String getIcon() {
        return codePoint;
    }

    @Override
    public String getTitle() {
        return title;
    }
}
