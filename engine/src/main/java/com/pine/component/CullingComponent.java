package com.pine.component;

import com.pine.PBean;
import com.pine.inspection.MutableField;
import com.pine.theme.Icons;
import org.joml.Vector3f;

import java.util.Set;
import java.util.LinkedList;

@PBean
public class CullingComponent extends AbstractComponent<CullingComponent> {

    @MutableField(label = "Max distance from camera")
    public int maxDistanceFromCamera = 300;
    @MutableField(label = "Frustum box size")
    public final Vector3f frustumBoxDimensions = new Vector3f(1);

    public CullingComponent(Entity entity, LinkedList<?> bag) {
        super(entity, bag);
    }

    public CullingComponent() {}

    @Override
    public Set<Class<? extends EntityComponent>> getDependencies() {
        return Set.of(TransformationComponent.class);
    }

    @Override
    public String getTitle() {
        return "Culling";
    }


    @Override
    public String getIcon() {
        return Icons.disabled_visible;
    }
}
