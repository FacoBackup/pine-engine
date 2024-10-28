package com.pine.component;

import com.pine.inspection.InspectableField;
import com.pine.repository.rendering.RenderingRequest;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class TransformationComponent extends AbstractComponent {
    public final boolean isInstanced;

    @InspectableField(label = "Translation")
    public Vector3f translation = new Vector3f();
    @InspectableField(label = "Scale")
    public Vector3f scale = new Vector3f(1);
    @InspectableField(label = "Rotation")
    public Quaternionf rotation = new Quaternionf();

    public Matrix4f globalMatrix = new Matrix4f();

    public transient RenderingRequest renderRequest;
    public boolean isCulled = false;

    public TransformationComponent(Entity entity) {
        this(entity, false);
    }

    public TransformationComponent(Entity entity, boolean b) {
        super(entity);
        this.isInstanced = b;
    }

    @Override
    public ComponentType getType() {
        return ComponentType.TRANSFORMATION;
    }

    @Override
    public AbstractComponent cloneComponent(Entity entity) {
        try {
            var clone = (TransformationComponent) super.cloneComponent(entity);
            clone.translation = (Vector3f) translation.clone();
            clone.rotation = (Quaternionf) rotation.clone();
            clone.scale = (Vector3f) scale.clone();
            clone.globalMatrix = (Matrix4f) globalMatrix.clone();
            return clone;
        } catch (Exception e) {
            return null;
        }
    }
}
