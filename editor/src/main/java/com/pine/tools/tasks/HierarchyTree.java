package com.pine.tools.tasks;

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
    public String matchedWith;
    public boolean isMatch;

    public HierarchyTree(int id, String title, String icon, boolean isEntity, List<HierarchyTree> children) {
        this.title = title;
        this.titleWithId = title + "##" + id;
        this.icon = icon;
        this.isEntity = isEntity;
        this.children = children;
        titleWithIcon = icon + title;
        titleWithIconId = titleWithIcon + "##" + id;
        this.id = id;
    }

    public HierarchyTree(int id, String title, String icon) {
        this(id, title, icon, true, new ArrayList<>());
    }
}
