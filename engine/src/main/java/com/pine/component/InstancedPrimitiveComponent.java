package com.pine.component;

import com.pine.injection.PBean;
import com.pine.inspection.MutableField;
import com.pine.inspection.ResourceTypeField;
import com.pine.repository.rendering.PrimitiveRenderRequest;
import com.pine.service.resource.primitives.mesh.Mesh;
import com.pine.service.resource.primitives.mesh.MeshRuntimeData;
import com.pine.service.resource.resource.ResourceType;
import com.pine.theme.Icons;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@PBean
public class InstancedPrimitiveComponent extends AbstractComponent<InstancedPrimitiveComponent> {
    @MutableField(label = "Scene members")
    public List<Transformation> primitives = new ArrayList<>();

    @MutableField(label = "Casts shadow")
    public boolean castsShadows = true;

    @MutableField(label = "Contribute to probes")
    public boolean contributeToProbes = true;

    @ResourceTypeField(type = ResourceType.PRIMITIVE)
    @MutableField(label = "Primitive")
    public ResourceRef<Mesh> primitive;

    @MutableField(label = "Number of instances", min = 1)
    public int numberOfInstances = 10;

    public transient MeshRuntimeData runtimeData;
    public transient PrimitiveRenderRequest renderRequest;

    public InstancedPrimitiveComponent(Entity entity, LinkedList<?> bag) {
        super(entity, bag);
    }

    public InstancedPrimitiveComponent() {
    }

    @Override
    public Set<Class<? extends EntityComponent>> getDependencies() {
        return Set.of(CullingComponent.class);
    }

    @Override
    public String getTitle() {
        return "Instanced Mesh";
    }

    @Override
    public String getIcon() {
        return Icons.filter_9_plus;
    }
}
