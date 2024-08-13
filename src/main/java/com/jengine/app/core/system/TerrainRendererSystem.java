package com.jengine.app.core.system;

import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.jengine.app.core.component.TerrainComponent;

@All(TerrainComponent.class)
public class TerrainRendererSystem extends IteratingSystem {
    @Override
    protected void process(int id) {
    }
}
