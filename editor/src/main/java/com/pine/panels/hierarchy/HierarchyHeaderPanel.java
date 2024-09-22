package com.pine.panels.hierarchy;

import com.pine.Engine;
import com.pine.PInject;
import com.pine.component.InstancedSceneComponent;
import com.pine.service.world.request.AddEntityRequest;
import com.pine.ui.panel.AbstractPanel;
import com.pine.ui.view.ButtonView;

import java.util.List;

public class HierarchyHeaderPanel extends AbstractPanel {

    @PInject
    public Engine engine;

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
            engine.addRequest(new AddEntityRequest(List.of(InstancedSceneComponent.class)));
        });
    }
}
