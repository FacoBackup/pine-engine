package com.pine.component;

import com.pine.PBean;
import com.pine.inspection.MutableField;
import com.pine.theme.Icons;

import java.util.Set;

@PBean
public class LightProbeComponent extends AbstractComponent<LightProbeComponent> {
    @MutableField(label = "Mipmaps")
    public int mipmaps = 6;
    /**
     * Only entities within  this radius will be rendered into the probe
     */
    @MutableField(label = "Radius")
    public int collectionRadius = 50;

    public LightProbeComponent(Integer entityId) {
        super(entityId);
    }

    public LightProbeComponent() {
        super();
    }

    @Override
    protected Set<Class<? extends EntityComponent>> getDependenciesInternal() {
        return Set.of(TransformationComponent.class);
    }

    @Override
    public String getTitle() {
        return "Probe";
    }


    @Override
    public String getIcon() {
        return Icons.panorama;
    }
}
