package com.pine.component;

import com.pine.SerializableRepository;
import com.pine.repository.ChangeRecord;

import java.io.Serializable;
import java.util.Set;

public interface EntityComponent extends ChangeRecord, SerializableRepository {
    Entity getEntity();

    Set<Class<? extends EntityComponent>> getDependencies();

    String getTitle();

    String getIcon();

    void addComponent(EntityComponent instance);
}
