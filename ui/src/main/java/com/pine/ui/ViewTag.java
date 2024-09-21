package com.pine.ui;

import com.pine.ui.view.*;
import jakarta.annotation.Nullable;

public enum ViewTag {
    TEXT("text", TextView.class, false),
    FRAGMENT("fragment", FragmentView.class, true),
    WINDOW("window", WindowView.class, true),
    BUTTON("button", ButtonView.class, false),
    INPUT("input", InputView.class, false),
    ACCORDION("accordion", AccordionView.class, true),
    GROUP("group", GroupView.class, true),
    INLINE("inline", InlineView.class, true),
    DIV("div", DivView.class, true),
    TABLE("table", TableView.class, true),
    TREE("tree", TreeView.class, false),
    LIST("list", ListView.class, false);

    private final String tag;
    private final Class<? extends AbstractView> clazz;
    private final boolean childrenSupported;

    ViewTag(String tag, Class<? extends AbstractView> clazz, boolean childrenSupported) {
        this.tag = tag;
        this.clazz = clazz;
        this.childrenSupported = childrenSupported;
    }

    public static ViewTag valueOfTag(AbstractView instance) {
        for (var t : ViewTag.values()) {
            if (t.clazz.isAssignableFrom(instance.getClass())) {
                return t;
            }
        }
        throw new IllegalArgumentException("Unknown view tag: " + instance.getClass().getName());
    }

    public boolean isChildrenSupported() {
        return childrenSupported;
    }

    @Nullable
    public static ViewTag valueOfTag(String tag) {
        for (var t : ViewTag.values()) {
            if (t.tag.equals(tag)) {
                return t;
            }
        }
        return null;
    }

    public Class<? extends AbstractView> getClazz() {
        return clazz;
    }

    public String getTag() {
        return tag;
    }
}
