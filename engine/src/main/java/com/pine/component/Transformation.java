package com.pine.component;

import com.pine.Mutable;
import com.pine.inspection.Inspectable;
import com.pine.inspection.MutableField;
import com.pine.repository.rendering.RenderingRequest;
import com.pine.theme.Icons;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Transformation extends Inspectable implements Mutable, Serializable {
    @MutableField(label = "Translation")
    public Vector3f translation = new Vector3f();
    @MutableField(label = "Scale")
    public Vector3f scale = new Vector3f(1);
    @MutableField(label = "Rotation")
    public Quaternionf rotation = new Quaternionf();

    public final boolean isInstance;
    public final Entity entity;
    public final List<Transformation> children = new LinkedList<>();
    public Transformation parent;

    public final Matrix4f globalMatrix = new Matrix4f();
    public final Matrix4f localMatrix = new Matrix4f();

    public int changes = 0;
    public int frozenVersion = -1;
    public RenderingRequest renderRequest;
    public int renderIndex;
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
}
