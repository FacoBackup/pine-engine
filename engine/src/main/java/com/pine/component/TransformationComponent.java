package com.pine.component;

import com.pine.PBean;
import com.pine.inspection.MutableField;
import com.pine.repository.rendering.PrimitiveRenderRequest;
import com.pine.theme.Icons;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Set;

@PBean
public class TransformationComponent extends AbstractComponent<TransformationComponent> {
    @MutableField(label = "Translation")
    public Vector3f translation = new Vector3f();
    @MutableField(label = "Scale")
    public Vector3f scale = new Vector3f(1);
    @MutableField(label = "Rotation")
    public Vector3f rotation = new Vector3f();

    public transient PrimitiveRenderRequest renderRequest;
    public transient int renderIndex;
    public final Matrix4f matrix = new Matrix4f();
    public transient int parentChangeId = -1;

    public TransformationComponent(Entity entity, LinkedList<?> bag) {
        super(entity, bag);
    }

    public TransformationComponent() {
    }

    @Override
    public Set<Class<? extends EntityComponent>> getDependencies() {
        return Collections.emptySet();
    }

    @Override
    public String getTitle() {
        return "Transformation";
    }

    @Override
    public String getIcon() {
        return Icons.control_camera;
    }
}
