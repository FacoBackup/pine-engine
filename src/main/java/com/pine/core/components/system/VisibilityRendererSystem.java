package com.pine.core.components.system;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.pine.core.components.component.MeshComponent;
import org.springframework.stereotype.Component;


@All(MeshComponent.class)
public class VisibilityRendererSystem extends IteratingSystem {
    protected ComponentMapper<MeshComponent> meshes;

    @Override
    protected void process(int id) {
        // TODO
    }
}