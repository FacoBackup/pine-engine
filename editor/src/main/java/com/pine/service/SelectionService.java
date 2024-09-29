package com.pine.service;

import com.pine.PBean;
import com.pine.PInject;
import com.pine.component.Entity;
import com.pine.repository.EditorStateRepository;
import com.pine.repository.WorldRepository;

import java.util.LinkedList;

@PBean
public class SelectionService {
    @PInject
    public EditorStateRepository settingsRepository;

    @PInject
    public WorldRepository worldRepository;

    public LinkedList<Entity> getSelected() {
        return settingsRepository.selected;
    }

    public void addSelected(Entity entity) {
        if (settingsRepository.selected.isEmpty() || entity == null) {
            settingsRepository.mainSelection = entity;
            if (settingsRepository.mainSelection == worldRepository.rootEntity) {
                settingsRepository.mainSelection = null;
            }
        }
        settingsRepository.selected.add(entity);
    }

    public void clearSelection() {
        settingsRepository.selected.forEach(e -> e.selected = false);
        settingsRepository.selected.clear();
    }

}

