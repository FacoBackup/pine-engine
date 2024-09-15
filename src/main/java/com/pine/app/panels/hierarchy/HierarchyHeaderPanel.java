package com.pine.app.panels.hierarchy;

import com.pine.app.EditorWindow;
import com.pine.app.core.ui.panel.AbstractPanel;
import com.pine.app.core.ui.view.ButtonView;
import com.pine.app.panels.files.FilesContext;
import com.pine.common.InjectBean;
import com.pine.common.fs.FSService;
import com.pine.engine.Engine;
import com.pine.engine.core.service.world.WorldService;
import com.pine.engine.core.service.world.request.AddEntityWorldRequest;

import java.util.Collections;

public class HierarchyHeaderPanel extends AbstractPanel {
    @InjectBean
    public FSService fsService;
    private Engine engine;
    private ButtonView importFile;
    private FilesContext filesContext;

    @Override
    protected String getDefinition() {
        return """
                <inline>
                    <input id='searchEntity'/>
                    <button id='addEntity'>[Plus]</button>
                </inline>
                """;
    }

    @Override
    public void onInitialize() {
        super.onInitialize();
        filesContext = (FilesContext) getContext();
        WorldService world = ((EditorWindow) document.getWindow()).getEngine().getWorld();

        var addEntity = (ButtonView) document.getElementById("addEntity");
        addEntity.setOnClick(() -> {
            world.addRequest(new AddEntityWorldRequest(Collections.emptyList()));
        });
    }
}
