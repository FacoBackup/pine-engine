package com.jengine.app.engine.service;

import com.jengine.app.engine.components.component.AbstractComponent;
import com.jengine.app.engine.repository.WorldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;


@Repository
public class WorldService {
    @Autowired
    private WorldRepository worldRepository;

    public int addEntity() {
        return worldRepository.getWorld().create();
    }

    public <T extends AbstractComponent> List<AbstractComponent> addComponent(int entity, final Class<T> component) {
        List<AbstractComponent> added = new ArrayList<>();
        AbstractComponent comp = worldRepository.getWorld().edit(entity).create(component);
        for (var dep : comp.getDependencies()) {
            added.addAll(addComponent(entity, dep));
        }
        added.add(comp);
        return added;
    }


}