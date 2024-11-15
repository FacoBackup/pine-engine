package com.pine.service;

import com.pine.component.Entity;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.repository.EditorRepository;
import com.pine.repository.WorldRepository;
import com.pine.service.grid.WorldService;

import java.util.Collection;

@PBean
public class SelectionService {
    @PInject
    public EditorRepository stateRepository;

    @PInject
    public WorldService worldService;

    @PInject
    public WorldRepository world;

    public void addSelected(String entity) {
        if (stateRepository.selected.isEmpty() || entity == null) {
            stateRepository.mainSelection = entity;
            if (stateRepository.mainSelection != null && stateRepository.mainSelection.contains(WorldRepository.ROOT_ID)) {
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
            for (var tile : worldService.getLoadedTiles()) {
                if (tile != null) {
                    stateRepository.primitiveSelected = world.bagTransformationComponent.get(stateRepository.mainSelection);
                }
            }
        }
    }
}

