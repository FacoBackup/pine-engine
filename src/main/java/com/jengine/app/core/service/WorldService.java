package com.jengine.app.core.service;

import com.artemis.*;
import com.artemis.utils.IntBag;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.jengine.app.core.components.component.AbstractComponent;
import com.jengine.app.core.repository.WorldRepository;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


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