package com.pine.component;

import com.pine.Mutable;
import com.pine.inspection.Inspectable;
import com.pine.inspection.InspectableField;
import com.pine.repository.rendering.RenderingRequest;
import com.pine.theme.Icons;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Transformation extends Inspectable implements Mutable, Serializable {
    @InspectableField(label = "Translation")
    public Vector3f translation = new Vector3f();
    @InspectableField(label = "Scale")
    public Vector3f scale = new Vector3f(1);
    @InspectableField(label = "Rotation")
    public Quaternionf rotation = new Quaternionf();

    public final boolean isInstance;
    public final Entity entity;
    public final List<Transformation> children = new LinkedList<>();
    public Transformation parent;
    public final Matrix4f globalMatrix = new Matrix4f();
    public final Matrix4f localMatrix = new Matrix4f();
    public int changes = 0;
    public int frozenVersion = -1;
    public transient RenderingRequest renderRequest;
    public int parentChangeId = -1;
    public boolean isCulled = false;

    public Transformation(Entity entity, boolean isInstance) {
        this.entity = entity;
        this.isInstance = isInstance;
    }

    @Override
    public String getTitle() {
        return "Transformation";
    }

    @Override
    public String getIcon() {
        return Icons.control_camera;
    }

    @Override
    public int getChangeId() {
        return changes;
    }

    @Override
    public void registerChange() {
        changes = (int) (Math.random() * 10000);
    }

    @Override
    public boolean isNotFrozen() {
        return frozenVersion != getChangeId();
    }

    @Override
    public void freezeVersion() {
        frozenVersion = getChangeId();
    }

    public Transformation clone(Entity entity, boolean isInstance)  {
        var clone = new Transformation(entity, isInstance);
        clone.translation.set(translation);
        clone.rotation.set(rotation);
        clone.scale.set(scale);
        clone.parent = parent;
        clone.globalMatrix.set(globalMatrix);
        clone.localMatrix.set(localMatrix);

        return clone;
    }
}
