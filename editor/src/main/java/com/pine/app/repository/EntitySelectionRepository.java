package com.pine.app.repository;

import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;

@Repository
public class EntitySelectionRepository {
    private Integer mainSelection = null;
    private final LinkedList<Integer> selected = new LinkedList<>();

    public LinkedList<Integer> getSelected() {
        return selected;
    }

    public void addSelected(Integer entityId) {
        if(selected.isEmpty()){
            mainSelection = entityId;
        }
        selected.add(entityId);
    }

    public void addSelected(List<Integer> entities) {
        if(selected.isEmpty() && !entities.isEmpty()){
            mainSelection = entities.getFirst();
        }
        selected.addAll(entities);
    }

    public void clearSelection() {
        selected.clear();
    }

    public Integer getMainSelection() {
        return mainSelection;
    }
}

