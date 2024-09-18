package com.pine.core.component;

import java.util.Set;
import java.util.Vector;

public interface EntityComponent {
    int getEntityId();

    Set<Class<? extends EntityComponent>> getDependencies();

    Vector<EntityComponent> getBag();

    String getComponentName();
}
