package com.jengine.app.core.components.system;

import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import org.springframework.stereotype.Component;

@Component
@All
public class AtmosphereRendererSystem extends IteratingSystem {
    @Override
    protected void process(int id) {
    }
}
