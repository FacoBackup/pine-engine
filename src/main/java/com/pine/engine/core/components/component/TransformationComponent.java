package com.pine.engine.core.components.component;

import com.pine.engine.core.components.RotationType;

import org.joml.*;
import java.util.List;

public class TransformationComponent extends AbstractComponent {
    private boolean underChange = false;
    private boolean changed = false;
    private Vector4f rotationQuaternion = new Vector4f();
    private Vector4f rotationQuaternionFinal = new Vector4f();
    private Vector3f translation = new Vector3f();
    private Vector3f scaling = new Vector3f();
    private Quaternionf rotationEuler = new Quaternionf();
    private RotationType rotationType = RotationType.QUATERNION;
    private Vector3f pivotPoint = new Vector3f();
    private Matrix4f matrix = new Matrix4f();
    private Matrix4f baseTransformationMatrix = new Matrix4f();
    private Matrix4f previousModelMatrix = new Matrix4f();
    private boolean lockedRotation = false;
    private boolean lockedTranslation = false;
    private boolean lockedScaling = false;
    private Vector3f absoluteTranslation = new Vector3f();

    @Override
    public List<Class<? extends AbstractComponent>> getDependencies() {
        return List.of();
    }

    public boolean isUnderChange() {
        return underChange;
    }

    public void setUnderChange(boolean underChange) {
        this.underChange = underChange;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public Vector4f getRotationQuaternion() {
        return rotationQuaternion;
    }

    public void setRotationQuaternion(Vector4f rotationQuaternion) {
        this.rotationQuaternion = rotationQuaternion;
    }

    public Vector4f getRotationQuaternionFinal() {
        return rotationQuaternionFinal;
    }

    public void setRotationQuaternionFinal(Vector4f rotationQuaternionFinal) {
        this.rotationQuaternionFinal = rotationQuaternionFinal;
    }

    public Vector3f getTranslation() {
        return translation;
    }

    public void setTranslation(Vector3f translation) {
        this.translation = translation;
    }

    public Vector3f getScaling() {
        return scaling;
    }

    public void setScaling(Vector3f scaling) {
        this.scaling = scaling;
    }

    public Quaternionf getRotationEuler() {
        return rotationEuler;
    }

    public void setRotationEuler(Quaternionf rotationEuler) {
        this.rotationEuler = rotationEuler;
    }

    public RotationType getRotationType() {
        return rotationType;
    }

    public void setRotationType(RotationType rotationType) {
        this.rotationType = rotationType;
    }

    public Vector3f getPivotPoint() {
        return pivotPoint;
    }

    public void setPivotPoint(Vector3f pivotPoint) {
        this.pivotPoint = pivotPoint;
    }

    public Matrix4f getMatrix() {
        return matrix;
    }

    public void setMatrix(Matrix4f matrix) {
        this.matrix = matrix;
    }

    public Matrix4f getBaseTransformationMatrix() {
        return baseTransformationMatrix;
    }

    public void setBaseTransformationMatrix(Matrix4f baseTransformationMatrix) {
        this.baseTransformationMatrix = baseTransformationMatrix;
    }

    public Matrix4f getPreviousModelMatrix() {
        return previousModelMatrix;
    }

    public void setPreviousModelMatrix(Matrix4f previousModelMatrix) {
        this.previousModelMatrix = previousModelMatrix;
    }

    public boolean isLockedRotation() {
        return lockedRotation;
    }

    public void setLockedRotation(boolean lockedRotation) {
        this.lockedRotation = lockedRotation;
    }

    public boolean isLockedTranslation() {
        return lockedTranslation;
    }

    public void setLockedTranslation(boolean lockedTranslation) {
        this.lockedTranslation = lockedTranslation;
    }

    public boolean isLockedScaling() {
        return lockedScaling;
    }

    public void setLockedScaling(boolean lockedScaling) {
        this.lockedScaling = lockedScaling;
    }

    public Vector3f getAbsoluteTranslation() {
        return absoluteTranslation;
    }

    public void setAbsoluteTranslation(Vector3f absoluteTranslation) {
        this.absoluteTranslation = absoluteTranslation;
    }
}
