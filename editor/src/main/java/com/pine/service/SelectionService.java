package com.pine.service;

import com.pine.component.Entity;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.repository.EditorRepository;
import com.pine.repository.WorldRepository;

import java.util.Collection;
import java.util.Objects;

@PBean
public class SelectionService {
    @PInject
    public EditorRepository stateRepository;

    @PInject
    public WorldRepository worldRepository;

    public void addSelected(String entity) {
        if (stateRepository.selected.isEmpty() || entity == null) {
            stateRepository.mainSelection = entity;
            if (Objects.equals(stateRepository.mainSelection, worldRepository.rootEntity.id())) {
                stateRepository.mainSelection = null;
            } else if (stateRepository.mainSelection != null) {
                updatePrimitiveSelected();
            }
        }
        if (entity != null) {
            stateRepository.selected.put(entity, true);
        }
    }

    public void clearSelection() {
        stateRepository.selected.clear();
        stateRepository.mainSelection = null;
        stateRepository.primitiveSelected = null;
    }

    public void addAllSelected(Collection<Entity> all) {
        stateRepository.selected.clear();
        var first = all.stream().findFirst().orElse(null);
        stateRepository.mainSelection = first != null ? first.id() : null;
        updatePrimitiveSelected();
        all.forEach(a -> stateRepository.selected.put(a.id(), true));
    }

    public void updatePrimitiveSelected() {
        if (stateRepository.mainSelection != null) {
            stateRepository.primitiveSelected = worldRepository.bagTransformationComponent.get(stateRepository.mainSelection);
        }
    }
}

