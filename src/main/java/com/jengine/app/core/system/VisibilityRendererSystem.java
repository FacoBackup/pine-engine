package com.jengine.app.core.system;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.jengine.app.core.component.MeshComponent;
import com.jengine.app.core.resource.shader.VisibilityShader;
import org.springframework.beans.factory.annotation.Autowired;

@All(MeshComponent.class)
public class VisibilityRendererSystem extends IteratingSystem {
    protected ComponentMapper<MeshComponent> meshes;

    @Autowired
    private VisibilityShader visibilityShader;

    @Override
    protected void process(int id) {
        // TODO
    }
}