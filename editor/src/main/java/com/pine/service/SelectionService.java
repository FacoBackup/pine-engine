package com.pine.service;

import com.pine.component.Entity;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.repository.EditorRepository;
import com.pine.repository.WorldRepository;

import java.util.LinkedList;

@PBean
public class SelectionService {
    @PInject
    public EditorRepository stateRepository;

    @PInject
    public WorldRepository worldRepository;

    public LinkedList<Entity> getSelected() {
        return stateRepository.selected;
    }

    public void addSelected(Entity entity) {
        if (stateRepository.selected.isEmpty() || entity == null) {
            stateRepository.mainSelection = entity;
            if (stateRepository.mainSelection == worldRepository.rootEntity) {
                stateRepository.mainSelection = null;
            } else if (stateRepository.mainSelection != null) {
                stateRepository.primitiveSelected = stateRepository.mainSelection.transformation;
            }
        }
        stateRepository.selected.add(entity);
    }

    public void clearSelection() {
        stateRepository.selected.forEach(e -> e.selected = false);
        stateRepository.selected.clear();
    }

}

