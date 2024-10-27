package com.pine.service;

import com.pine.component.Entity;
import com.pine.component.TransformationComponent;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.repository.EditorRepository;
import com.pine.repository.WorldRepository;

import java.util.Collection;

@PBean
public class SelectionService {
    @PInject
    public EditorRepository stateRepository;

    @PInject
    public WorldRepository worldRepository;

    public void addSelected(Entity entity) {
        if (stateRepository.selected.isEmpty() || entity == null) {
            stateRepository.mainSelection = entity;
            if (stateRepository.mainSelection == worldRepository.rootEntity) {
                stateRepository.mainSelection = null;
            } else if (stateRepository.mainSelection != null) {
                stateRepository.primitiveSelected = getPrimitiveSelected();
            }
        }
        if (entity != null) {
            stateRepository.selected.put(entity.id(), true);
        }
    }

    public void clearSelection() {
        stateRepository.selected.clear();
        stateRepository.mainSelection = null;
        stateRepository.primitiveSelected = null;
    }

    public void addAllSelected(Collection<Entity> all) {
        stateRepository.selected.clear();
        stateRepository.mainSelection = all.stream().findFirst().orElse(null);
        stateRepository.primitiveSelected = stateRepository.mainSelection != null ? getPrimitiveSelected() : null;
        all.forEach(a -> stateRepository.selected.put(a.id(), true));
    }

    private TransformationComponent getPrimitiveSelected() {
        return worldRepository.getTransformationComponent(stateRepository.mainSelection.id());
    }
}

