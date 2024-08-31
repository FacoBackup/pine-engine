package com.pine.engine.components.system;

import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.pine.engine.components.component.TerrainComponent;


@All(TerrainComponent.class)
public class TerrainRendererSystem extends IteratingSystem {
    @Override
    protected void process(int id) {
    }
}
