package com.pine.service.world;

import com.pine.PBean;
import com.pine.PInject;
import com.pine.component.EntityComponent;
import com.pine.component.TransformationComponent;
import com.pine.repository.WorldRepository;
import javax.annotation.Nullable;

import java.util.concurrent.ConcurrentHashMap;

import static com.pine.repository.WorldRepository.EMPTY_MAP;


@PBean
public class WorldService {
    private static final String TRANSFORMATION_CLASS = TransformationComponent.class.getSimpleName();

    @PInject
    public WorldRepository worldRepository;

    @Nullable
    @SuppressWarnings("unchecked")
    public <T extends EntityComponent> T getComponent(Integer entityId, Class<T> component) {
        var components = worldRepository.entities.get(entityId);
        if (components != null) {
            return (T) components.get(component.getSimpleName());
        }
        return null;
    }

    public WorldHierarchyTree getHierarchyTree() {
        return worldRepository.worldTree;
    }

    public Object getTransformationComponentUnchecked(int entityId) {
        return worldRepository.entities.get(entityId).get(TRANSFORMATION_CLASS);
    }

    public ConcurrentHashMap<String, EntityComponent> getComponents(Integer entity) {
        return worldRepository.entities.getOrDefault(entity, EMPTY_MAP);
    }
}
