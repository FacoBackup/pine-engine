package com.pine.engine.core.components.component;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Vector4;
import com.pine.engine.core.components.RotationType;

import java.util.List;

public class TransformationComponent extends AbstractComponent {
    private boolean underChange = false;
    private boolean changed = false;
    private Vector4 rotationQuaternion = new Vector4();
    private Vector4 rotationQuaternionFinal = new Vector4();
    private Vector3 translation = new Vector3();
    private Vector3 scaling = new Vector3();
    private Quaternion rotationEuler = new Quaternion();
    private RotationType rotationType = RotationType.QUATERNION;
    private Vector3 pivotPoint = new Vector3();
    private Matrix4 matrix = new Matrix4();
    private Matrix4 baseTransformationMatrix = new Matrix4();
    private Matrix4 previousModelMatrix = new Matrix4();
    private boolean lockedRotation = false;
    private boolean lockedTranslation = false;
    private boolean lockedScaling = false;
    private Vector3 absoluteTranslation = new Vector3();

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

    public Vector4 getRotationQuaternion() {
        return rotationQuaternion;
    }

    public void setRotationQuaternion(Vector4 rotationQuaternion) {
        this.rotationQuaternion = rotationQuaternion;
    }

    public Vector4 getRotationQuaternionFinal() {
        return rotationQuaternionFinal;
    }

    public void setRotationQuaternionFinal(Vector4 rotationQuaternionFinal) {
        this.rotationQuaternionFinal = rotationQuaternionFinal;
    }

    public Vector3 getTranslation() {
        return translation;
    }

    public void setTranslation(Vector3 translation) {
        this.translation = translation;
    }

    public Vector3 getScaling() {
        return scaling;
    }

    public void setScaling(Vector3 scaling) {
        this.scaling = scaling;
    }

    public Quaternion getRotationEuler() {
        return rotationEuler;
    }

    public void setRotationEuler(Quaternion rotationEuler) {
        this.rotationEuler = rotationEuler;
    }

    public RotationType getRotationType() {
        return rotationType;
    }

    public void setRotationType(RotationType rotationType) {
        this.rotationType = rotationType;
    }

    public Vector3 getPivotPoint() {
        return pivotPoint;
    }

    public void setPivotPoint(Vector3 pivotPoint) {
        this.pivotPoint = pivotPoint;
    }

    public Matrix4 getMatrix() {
        return matrix;
    }

    public void setMatrix(Matrix4 matrix) {
        this.matrix = matrix;
    }

    public Matrix4 getBaseTransformationMatrix() {
        return baseTransformationMatrix;
    }

    public void setBaseTransformationMatrix(Matrix4 baseTransformationMatrix) {
        this.baseTransformationMatrix = baseTransformationMatrix;
    }

    public Matrix4 getPreviousModelMatrix() {
        return previousModelMatrix;
    }

    public void setPreviousModelMatrix(Matrix4 previousModelMatrix) {
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

    public Vector3 getAbsoluteTranslation() {
        return absoluteTranslation;
    }

    public void setAbsoluteTranslation(Vector3 absoluteTranslation) {
        this.absoluteTranslation = absoluteTranslation;
    }
}
