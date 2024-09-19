package com.pine.component;

import com.pine.annotation.EngineInjectable;
import com.pine.type.RotationType;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.Collections;
import java.util.Set;

@EngineInjectable
public class TransformationComponent extends AbstractComponent<TransformationComponent> {
    public Vector4f rotationQuaternion = new Vector4f();
    public Vector4f rotationQuaternionFinal = new Vector4f();
    public Vector3f translation = new Vector3f();
    public Vector3f scaling = new Vector3f();
    public Quaternionf rotationEuler = new Quaternionf();
    public RotationType rotationType = RotationType.QUATERNION;
    public Vector3f pivotPoint = new Vector3f();
    public Matrix4f matrix = new Matrix4f();
    public Matrix4f baseTransformationMatrix = new Matrix4f();
    public Matrix4f previousTransformationMatrix = new Matrix4f();
    public boolean lockedRotation = false;
    public boolean lockedTranslation = false;
    public boolean lockedScaling = false;
    public Vector3f absoluteTranslation = new Vector3f();

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
