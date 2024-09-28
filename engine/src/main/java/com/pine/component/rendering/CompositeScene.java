package com.pine.component.rendering;

import com.pine.component.InstancedSceneComponent;
import com.pine.inspection.MutableField;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CompositeScene implements Serializable {
    private final InstancedSceneComponent comp;
    private final int entityId;

    @MutableField
    public List<ScenePrimitive> primitives = new ArrayList<>();
    public final boolean isInstanced;

    public CompositeScene(int entityId) {
        this.isInstanced = false;
        comp = null;
        this.entityId = entityId;
    }

    public CompositeScene(boolean isInstanced, InstancedSceneComponent comp) {
        this.isInstanced = isInstanced;
        this.comp = comp;
        this.entityId = comp.getEntityId();
    }

    public void addPrimitive() {
        ScenePrimitive newScene = new ScenePrimitive(new SimpleTransformation(entityId));
        if (isInstanced) {
            newScene.castsShadows = comp.castsShadows;
            newScene.contributeToProbes = comp.contributeToProbes;
            newScene.primitive = comp.primitive;
            float offset = primitives.size() + 1;
            newScene.transformation.translation.set(offset * 2, offset * 2, 0.);
        }
        primitives.add(newScene);
    }

    public void removePrimitive(int index) {
        primitives.remove(index);
    }
}
