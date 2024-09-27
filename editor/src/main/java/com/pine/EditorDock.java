package com.pine;

import com.pine.dock.DockDescription;
import com.pine.theme.Icons;

public enum EditorDock implements DockDescription {
    Viewport("Viewport", Icons.ipublic, 0, 0),
    Hierarchy("Hierarchy", Icons.account_tree),
    Inspector("Inspector", Icons.search),
    Console("Console", Icons.terminal),
    Files("Files", Icons.folder_open);

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
