package com.pine.tools.tasks;

import com.pine.component.Entity;
import com.pine.theme.Icons;

import java.util.List;

public class HierarchyTree {
    public final List<HierarchyTree> children;
    public final String title;
    public final String titleWithIcon;
    public final String titleWithIconId;
    public final String icon;
    public final boolean isEntity;
    public final String id;
    public final String titleWithId;
    public final String visibilityLabel;
    public final String pinLabel;
    public String matchedWith;
    public boolean isMatch;
    public final String visibilityOffLabel;
    public final String pinOffLabel;
    public Entity entity;

    public HierarchyTree(Entity entity, String title, String icon, boolean isEntity, List<HierarchyTree> children) {
        this.id = entity.id;
        this.title = title;
        this.titleWithId = title + "##" + id;
        this.icon = icon;
        this.isEntity = isEntity;
        this.children = children;
        titleWithIcon = icon + title;
        titleWithIconId = titleWithIcon + "##" + id;
        visibilityLabel = Icons.visibility + "##v" + id;
        visibilityOffLabel = Icons.visibility_off + "##vO" + id;
        pinLabel = Icons.lock + "##p" + id;
        pinOffLabel = Icons.lock_open + "##pO" + id;
    }
}
