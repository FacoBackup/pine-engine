package com.pine.component;

import java.util.Set;

public interface EntityComponent {
    int getEntityId();

    Set<Class<? extends EntityComponent>> getDependencies();

    String getComponentName();

    void addComponent(EntityComponent instance);

    String getLabel();
}
