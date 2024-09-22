package com.pine.component.rendering;

import com.pine.component.InstancedSceneComponent;

import java.util.ArrayList;
import java.util.List;

public class CompositeScene {
    private final InstancedSceneComponent comp;
    public List<ScenePrimitive> primitives = new ArrayList<>();
    public final boolean isInstanced;

    public CompositeScene(boolean isInstanced) {
        this(isInstanced, null);
    }

    public CompositeScene(boolean isInstanced, InstancedSceneComponent comp) {
        this.isInstanced = isInstanced;
        this.comp = comp;
    }

    public void addPrimitive() {
        ScenePrimitive newScene = new ScenePrimitive();
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
