package com.pine.component;

import com.pine.PBean;
import com.pine.inspection.MutableField;
import org.joml.Vector3f;

import java.util.Collections;
import java.util.Set;

@PBean
public class TransformationComponent extends AbstractComponent<TransformationComponent> {

    @MutableField(label = "Translation")
    public Vector3f translation = new Vector3f();
    @MutableField(label = "Scale")
    public Vector3f scaling = new Vector3f();
    @MutableField(label = "Rotation")
    public Vector3f rotationEuler = new Vector3f();
    @MutableField(label = "Pivot point")
    public Vector3f pivotPoint = new Vector3f();

    public TransformationComponent(Integer entityId) {
        super(entityId);
    }

    public TransformationComponent() {
        super();
    }

    @Override
    protected Set<Class<? extends EntityComponent>> getDependenciesInternal() {
        return Collections.emptySet();
    }

    @Override
    public String getComponentName() {
        return "Transformation";
    }
}
