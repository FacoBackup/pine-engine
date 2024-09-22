package com.pine.component;

import com.pine.PBean;
import com.pine.component.rendering.CompositeScene;
import com.pine.inspection.MutableField;
import com.pine.inspection.NumericFieldRule;
import com.pine.inspection.ResourceField;
import com.pine.inspection.ResourceRef;
import com.pine.repository.rendering.PrimitiveRenderRequest;
import com.pine.service.resource.primitives.mesh.MeshRuntimeData;
import com.pine.service.resource.primitives.mesh.Primitive;
import com.pine.service.resource.resource.ResourceType;

import java.util.Set;

@PBean
public class InstancedSceneComponent extends AbstractComponent<InstancedSceneComponent> {
    @MutableField(label = "Scene members")
    public final CompositeScene compositeScene = new CompositeScene(true, this);

    @MutableField(label = "Casts shadows")
    public boolean castsShadows = true;
    @MutableField(label = "Contribute to probes")
    public boolean contributeToProbes = true;

    @ResourceField(type = ResourceType.TEXTURE)
    @MutableField(label = "Primitive instance")
    public ResourceRef<Primitive> primitive;

    @NumericFieldRule(min = 1, max = 200, isAngle = false, isDirectChange = false)
    @MutableField(label = "Number of instances")
    public int numberOfInstances = 10;

    public transient MeshRuntimeData runtimeData;
    public transient PrimitiveRenderRequest request;

    public InstancedSceneComponent(Integer entityId) {
        super(entityId);
    }

    public InstancedSceneComponent() {
        super();
    }

    @Override
    protected Set<Class<? extends EntityComponent>> getDependenciesInternal() {
        return Set.of(CullingComponent.class);
    }

    @Override
    public String getComponentName() {
        return "Instanced Mesh";
    }
}
