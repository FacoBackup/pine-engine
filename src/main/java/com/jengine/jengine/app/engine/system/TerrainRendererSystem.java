package com.jengine.jengine.app.engine.system;

import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.jengine.jengine.app.engine.component.TerrainComponent;

@All(TerrainComponent.class)
public class TerrainRendererSystem extends IteratingSystem {
    @Override
    protected void process(int id) {
    }
}
