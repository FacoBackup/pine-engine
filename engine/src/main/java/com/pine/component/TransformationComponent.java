package com.pine.component;

import com.pine.injection.EngineInjectable;
import com.pine.inspection.MutableField;
import com.pine.type.RotationType;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.Collections;
import java.util.Set;

@EngineInjectable
public class TransformationComponent extends AbstractComponent<TransformationComponent> {

    public RotationType rotationType = RotationType.QUATERNION;

    @MutableField(label = "Translation")
    public Vector3f translation = new Vector3f();
    @MutableField(label = "Scale")
    public Vector3f scaling = new Vector3f();
    @MutableField(label = "Rotation Euler")
    public Vector3f rotationEuler = new Vector3f();
    @MutableField(label = "Rotation Quaternion")
    public Quaternionf rotationQuaternion = new Quaternionf();
    @MutableField(label = "Pivot point")
    public Vector3f pivotPoint = new Vector3f();
    @MutableField(label = "Lock Rotation")
    public boolean lockedRotation = false;
    @MutableField(label = "Lock Translation")
    public boolean lockedTranslation = false;
    @MutableField(label = "Lock Scaling")
    public boolean lockedScaling = false;

    public transient Quaternionf rotationQuaternionFinal = new Quaternionf();
    public transient Matrix4f matrix = new Matrix4f();
    public transient Matrix4f baseTransformationMatrix = new Matrix4f();
    public transient Matrix4f previousTransformationMatrix = new Matrix4f();
    public transient Vector3f absoluteTranslation = new Vector3f();

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
