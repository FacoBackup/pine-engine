package com.pine.service;

import com.pine.PBean;
import com.pine.PInject;
import com.pine.component.rendering.SimpleTransformation;
import com.pine.repository.EditorStateRepository;
import com.pine.repository.WorldRepository;

import java.util.LinkedList;

@PBean
public class SelectionService {
    @PInject
    public EditorStateRepository settingsRepository;

    public LinkedList<Integer> getSelected() {
        return settingsRepository.selected;
    }

    public void addSelected(Integer entityId) {
        if (settingsRepository.selected.isEmpty() || entityId == null) {
            settingsRepository.mainSelection = entityId;
            if (settingsRepository.mainSelection == WorldRepository.ROOT_ID) {
                settingsRepository.mainSelection = null;
            }
        }
        settingsRepository.selected.add(entityId);
    }

    public void clearSelection() {
        settingsRepository.selected.clear();
    }

    public Integer getMainSelection() {
        return settingsRepository.mainSelection;
    }

    public SimpleTransformation getPrimitiveSelected() {
        return settingsRepository.primitiveSelected;
    }
}

