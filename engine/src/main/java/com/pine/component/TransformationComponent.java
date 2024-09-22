package com.pine.component;

import com.pine.PBean;
import com.pine.component.rendering.SimpleTransformation;
import com.pine.inspection.MutableField;
import org.joml.Vector3f;

import java.util.Collections;
import java.util.Set;

@PBean
public class TransformationComponent extends AbstractComponent<TransformationComponent> {

    @MutableField(label = "Translation")
    public Vector3f translation = new Vector3f();
    @MutableField(label = "Scale")
    public Vector3f scale = new Vector3f(1);
    @MutableField(label = "Rotation")
    public Vector3f rotation = new Vector3f();

    private SimpleTransformation simple;

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

    public SimpleTransformation toSimpleTransformation() {
        if (this.simple == null) {
            simple = new SimpleTransformation();
            simple.translation = translation;
            simple.rotation = rotation;
            simple.scale = scale;
        }
        return simple;
    }
}
