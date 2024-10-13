package com.pine.component;

import com.pine.injection.PBean;
import com.pine.inspection.MutableField;
import com.pine.theme.Icons;

import java.util.LinkedList;


public class LightProbeComponent extends AbstractComponent {
    @MutableField(label = "Mipmaps")
    public int mipmaps = 6;
    /**
     * Only entities within  this radius will be rendered into the probe
     */
    @MutableField(label = "Radius")
    public int collectionRadius = 50;

    public LightProbeComponent(Entity entity) {
        super(entity);
    }

    @Override
    public ComponentType getType() {
        return ComponentType.LIGHT_PROBE;
    }
}
