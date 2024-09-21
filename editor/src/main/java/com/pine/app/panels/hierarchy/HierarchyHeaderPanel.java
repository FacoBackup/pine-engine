package com.pine.app.panels.hierarchy;

import com.pine.app.EditorWindow;
import com.pine.component.InstancedMeshComponent;
import com.pine.service.world.request.AddEntityRequest;
import com.pine.ui.panel.AbstractPanel;
import com.pine.ui.view.ButtonView;

import java.util.List;

public class HierarchyHeaderPanel extends AbstractPanel {

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
        var addEntity = (ButtonView) document.getElementById("addEntity");
        addEntity.setOnClick(() -> {
            ((EditorWindow) document.getWindow()).getEngine().addRequest(new AddEntityRequest(List.of(InstancedMeshComponent.class)));
        });
    }
}
