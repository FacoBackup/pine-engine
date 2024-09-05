package com.pine.app.editor.panels.files;

import com.pine.app.core.ui.panel.AbstractPanel;
import com.pine.app.core.ui.view.TreeView;
import com.pine.app.core.ui.view.tree.Branch;
import com.pine.app.core.ui.view.tree.Tree;
import com.pine.common.Inject;
import com.pine.common.fs.FSService;

import java.util.HashMap;
import java.util.Map;

public class FilesTreePanel extends AbstractPanel {
    private final Map<String, Boolean> selected = new HashMap<>();

    @Inject
    public FSService fsService;
    private TreeView tree;

    @Override
    protected String getDefinition() {
        return "<tree id='filesTree'/>";
    }

    @Override
    public void onInitialize() {
        super.onInitialize();
        tree = (TreeView) document.getElementById("filesTree");
        tree.setSelected(selected);
        tree.setOnClick(this::onClick);
    }

    @Override
    public void beforeRender() {
        tree.setTree(fsService.getDirectoriesTree());
    }

    private void onClick(Branch branch, Boolean isDoubleClick) {
        if (branch instanceof Tree || !isDoubleClick) {
            return;
        }
        var context = (FilesContext) getContext();
        context.setDirectory(branch.getId());
    }
}
