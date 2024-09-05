package com.pine.app.core.ui.view;

import com.pine.app.core.ui.View;
import com.pine.app.core.ui.view.tree.Branch;
import com.pine.app.core.ui.view.tree.Tree;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class TreeView extends AbstractView {
    private static final int FLAGS = ImGuiTreeNodeFlags.Bullet | ImGuiTreeNodeFlags.NoTreePushOnOpen | ImGuiTreeNodeFlags.SpanFullWidth;
    private Map<String, Boolean> selected = Collections.emptyMap();
    private Consumer<Branch> onClick;
    private Tree tree;

    public TreeView(View parent, String id) {
        super(parent, id);
    }

    @Override
    protected void renderInternal() {
        if (tree == null) {
            return;
        }
        renderNode(tree);
    }

    private void renderNode(Branch branch) {
        int flags = FLAGS;
        Boolean isSelected = selected.get(branch.getId());
        if (isSelected != null && isSelected) {
            flags |= ImGuiTreeNodeFlags.Selected;
        }

        if (branch == tree) {
            flags |= ImGuiTreeNodeFlags.DefaultOpen;
        } else {
            flags |= ImGuiTreeNodeFlags.Leaf;
        }

        boolean isOpen = ImGui.treeNodeEx(branch.getLabel(), flags);
        if (ImGui.isItemClicked() && onClick != null) {
            onClick.accept(branch);
        }

        if (isOpen) {
            List<Branch> branches = branch.getBranches();
            for (var childBranch : branches) {
                renderNode(childBranch);
            }
            ImGui.treePop();
        }
    }

    public void setSelected(Map<String, Boolean> selected) {
        this.selected = selected;
    }

    public Map<String, Boolean> getSelected() {
        return selected;
    }

    public void setOnClick(Consumer<Branch> onClick) {
        this.onClick = onClick;
    }

    public Tree getTree() {
        return tree;
    }

    public void setTree(Tree tree) {
        this.tree = tree;
    }
}
