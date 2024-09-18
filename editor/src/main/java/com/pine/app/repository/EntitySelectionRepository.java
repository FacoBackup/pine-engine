package com.pine.app.repository;

import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;

@Repository
public class EntitySelectionRepository {
    private final LinkedList<Integer> selected = new LinkedList<>();

    public LinkedList<Integer> getSelected() {
        return selected;
    }

    public void addSelected(Integer entityId) {
        selected.add(entityId);
    }

    public void addSelected(List<Integer> entities) {
        selected.addAll(entities);
    }

    public void clearSelection() {
        selected.clear();
    }
}

