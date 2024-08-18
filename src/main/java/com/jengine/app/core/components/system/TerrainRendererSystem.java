package com.jengine.app.core.components.system;

import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.jengine.app.core.components.component.TerrainComponent;
import org.springframework.stereotype.Component;
import com.artemis.annotations.All;

@Component
@All(TerrainComponent.class)
public class TerrainRendererSystem extends IteratingSystem {
    @Override
    protected void process(int id) {
    }
}
