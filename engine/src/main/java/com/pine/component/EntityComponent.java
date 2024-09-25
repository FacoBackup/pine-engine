package com.pine.component;

import com.pine.repository.ChangeRecord;

import java.util.Set;

public interface EntityComponent extends ChangeRecord {
    int getEntityId();

    Set<Class<? extends EntityComponent>> getDependencies();

    String getComponentName();

    void addComponent(EntityComponent instance);

    String getLabel();
}
