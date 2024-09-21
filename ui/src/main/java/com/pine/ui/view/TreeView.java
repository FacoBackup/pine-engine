package com.pine.ui.view;

import com.pine.AbstractTree;
import com.pine.ui.View;
import imgui.ImGui;
import imgui.flag.ImGuiKey;
import imgui.flag.ImGuiTreeNodeFlags;

import java.util.Objects;
import java.util.WeakHashMap;
import java.util.function.BiConsumer;

public class TreeView extends AbstractView {
    private static final int FLAGS = ImGuiTreeNodeFlags.SpanFullWidth;
    private final WeakHashMap<AbstractTree<?, ?>, Boolean> selected = new WeakHashMap<>();
    private BiConsumer<AbstractTree<?, ?>, Boolean> onClick;
    private AbstractTree<?, ?> tree;
    private BiConsumer<AbstractTree<?, ?>, AbstractTree<?, ?>> onDrop;
    private AbstractTree<?, ?> onDrag;

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

    private void renderNode(AbstractTree<?, ?> branch) {
        int flags = FLAGS;
        if (selected.containsKey(branch)) {
            flags |= ImGuiTreeNodeFlags.Selected;
        }

        if (branch == tree) {
            flags |= ImGuiTreeNodeFlags.DefaultOpen;
        }

        boolean isOpen = ImGui.treeNodeEx(branch.key, flags, branch.getName());
        handleDragDrop(branch);
        handleClick(branch);
        renderChildren(branch, isOpen);
    }

    private void handleDragDrop(AbstractTree<?, ?> target) {
        if (onDrop == null) {
            return;
        }

        if (ImGui.beginDragDropSource()) {
            ImGui.setDragDropPayload(internalIdPartial, internalIdPartial);
            this.onDrag = target;
            ImGui.text("Dragging Node " + target.getName());
            ImGui.endDragDropSource();
        }

        if (ImGui.beginDragDropTarget()) {
            if (Objects.equals(ImGui.acceptDragDropPayload(internalIdPartial), internalIdPartial)) {
                onDrop.accept(target, onDrag);
                onDrag = null;
            }
            ImGui.endDragDropTarget();
        }
    }

    private void renderChildren(AbstractTree<?, ?> branch, boolean isOpen) {
        if (isOpen) {

            for (Object extraData : branch.extraData) {
                ImGui.text(extraData.toString());
            }

            for (var childBranch : branch.branches) {
                renderNode(childBranch);
            }

            ImGui.treePop();
        }
    }

    private void handleClick(AbstractTree<?, ?> branch) {
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
    }

    public void setOnClick(BiConsumer<AbstractTree<?, ?>, Boolean> onClick) {
        this.onClick = onClick;
    }

    public void setOnDrop(BiConsumer<AbstractTree<?, ?>, AbstractTree<?, ?>> onDrop) {
        this.onDrop = onDrop;
    }

    public void setTree(AbstractTree<?, ?> tree) {
        this.tree = tree;
    }
}
