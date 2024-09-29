package com.pine.tools.tasks;

import com.pine.theme.Icons;

import java.util.ArrayList;
import java.util.List;

public class HierarchyTree {
    public final List<HierarchyTree> children;
    public final String title;
    public final String titleWithIcon;
    public final String titleWithIconId;
    public final String icon;
    public final boolean isEntity;
    public final int id;
    public final String titleWithId;
    public final String visibilityLabel;
    public final String pinLabel;
    public String matchedWith;
    public boolean isMatch;
    public final String visibilityOffLabel;
    public final String pinOffLabel;

    public HierarchyTree(int id, String title, String icon, boolean isEntity, List<HierarchyTree> children) {
        this.id = id;
        this.title = title;
        this.titleWithId = title + "##" + id;
        this.icon = icon;
        this.isEntity = isEntity;
        this.children = children;
        titleWithIcon = icon + title;
        titleWithIconId = titleWithIcon + "##" + id;
        visibilityLabel = Icons.visibility + "##visibility" + id;
        visibilityOffLabel = Icons.visibility_off + "##visibilityOff" + id;
        pinLabel = Icons.lock + "##pin" + id;
        pinOffLabel = Icons.lock_open + "##pinOff" + id;
    }

    public HierarchyTree(int id, String title, String icon) {
        this(id, title, icon, true, new ArrayList<>());
    }
}
