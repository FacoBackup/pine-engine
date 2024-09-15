package com.pine.app.core.ui.view;

import com.pine.app.core.ui.View;
import imgui.ImGui;
import imgui.flag.ImGuiKey;
import imgui.flag.ImGuiTreeNodeFlags;

import java.util.WeakHashMap;
import java.util.function.BiConsumer;

public class TreeView extends AbstractView {
    private static final int FLAGS = ImGuiTreeNodeFlags.SpanFullWidth;
    private final WeakHashMap<AbstractTree<?>, Boolean> selected = new WeakHashMap<>();
    private BiConsumer<AbstractTree<?>, Boolean> onClick;
    private AbstractTree<?> tree;

    public TreeView(View parent, String id) {
        super(parent, id);
    }

    @Override
    public void renderInternal() {
        if (tree == null) {
            return;
        }
        renderNode(tree);
    }

    private void renderNode(AbstractTree<?> branch) {
        int flags = FLAGS;
        if (selected.containsKey(branch)) {
            flags |= ImGuiTreeNodeFlags.Selected;
        }

        if (branch == tree) {
            flags |= ImGuiTreeNodeFlags.DefaultOpen;
        } else {
            flags |= ImGuiTreeNodeFlags.Leaf;
        }

        boolean isOpen = ImGui.treeNodeEx(branch.key, flags, branch.getName());
        if (ImGui.isItemClicked()) {
            boolean isMultiSelect = ImGui.isKeyPressed(ImGuiKey.LeftCtrl);
            if (!isMultiSelect) {
                selected.clear();
            }
            selected.put(branch, true);

            if (onClick != null) {
                onClick.accept(branch, isMultiSelect);
            }
        }

        if (isOpen) {
            for (var childBranch : branch.branches) {
                renderNode(childBranch);
            }
            ImGui.treePop();
        }
    }

    public void setOnClick(BiConsumer<AbstractTree<?>, Boolean> onClick) {
        this.onClick = onClick;
    }

    public void setTree(AbstractTree<?> tree) {
        this.tree = tree;
    }
}
