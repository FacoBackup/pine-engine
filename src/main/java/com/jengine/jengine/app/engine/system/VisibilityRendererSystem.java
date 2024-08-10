package com.jengine.jengine.app.engine.system;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.jengine.jengine.app.engine.component.MeshComponent;
import com.jengine.jengine.app.engine.resource.shader.VisibilityShader;
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