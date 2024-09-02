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

    @Override
    protected String getDefinition() {
        return "<tree id='filesTree'/>";
    }

    @Override
    public void onInitialize() {
        super.onInitialize();
        var tree = (TreeView) document.getElementById("filesTree");
        tree.setTree(fsService.getDirectories());
        tree.setSelected(selected);
        tree.setOnClick(this::onClick);
    }

    private void onClick(Branch branch) {
        if (branch instanceof Tree) {
            return;
        }
        var context = (FilesContext) getContext();
        context.setDirectory(branch.getId());
        FilesPanel.setCurrentDirectory(fsService, context);
    }
}
