package com.pine.component;

import com.pine.injection.PBean;
import com.pine.inspection.MutableField;
import com.pine.theme.Icons;
import org.joml.Vector3f;

import java.util.LinkedList;
import java.util.Set;

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
        return Set.of();
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
