package com.pine.component;

import com.pine.Mutable;
import com.pine.SerializableRepository;

import java.util.Set;

public interface EntityComponent extends Mutable, SerializableRepository {
    Entity getEntity();

    Set<Class<? extends EntityComponent>> getDependencies();

    String getTitle();

    String getIcon();

    void addComponent(EntityComponent instance);
}
