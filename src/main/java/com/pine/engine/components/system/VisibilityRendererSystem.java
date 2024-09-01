package com.pine.engine.components.system;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.pine.engine.components.component.MeshComponent;


@All(MeshComponent.class)
public class VisibilityRendererSystem extends IteratingSystem {
    protected ComponentMapper<MeshComponent> meshes;

    @Override
    protected void process(int id) {
        // TODO
    }
}