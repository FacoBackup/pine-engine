package com.jengine.app.engine.components.system;

import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.jengine.app.engine.components.component.TerrainComponent;
import org.springframework.stereotype.Component;

@Component
@All(TerrainComponent.class)
public class TerrainRendererSystem extends IteratingSystem {
    @Override
    protected void process(int id) {
    }
}
