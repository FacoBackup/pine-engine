package com.pine.component;

import com.pine.repository.ChangeRecord;

import java.io.Serializable;
import java.util.Set;

public interface EntityComponent extends ChangeRecord, Serializable {
    int getEntityId();

    Set<Class<? extends EntityComponent>> getDependencies();

    String getTitle();

    void addComponent(EntityComponent instance);
}
