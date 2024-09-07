package com.pine.engine.components.system;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.pine.engine.Engine;
import com.pine.engine.components.component.MeshComponent;


@All(MeshComponent.class)
public class VisibilityRendererSystem extends IteratingSystem implements ISystem {
    protected ComponentMapper<MeshComponent> meshes;
    private Engine engine;

    @Override
    public void setEngine (Engine engine){
        this.engine = engine;
    }

    @Override
    protected void process(int id) {
    }
}