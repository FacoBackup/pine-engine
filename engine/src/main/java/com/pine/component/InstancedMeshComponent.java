package com.pine.component;

import com.pine.injection.PBean;
import com.pine.inspection.MutableField;
import com.pine.repository.rendering.RenderingRequest;
import com.pine.repository.streaming.MeshStreamableResource;
import com.pine.theme.Icons;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@PBean
public class InstancedMeshComponent extends AbstractComponent<InstancedMeshComponent> {
    @MutableField(label = "Scene members")
    public List<Transformation> primitives = new ArrayList<>();

    @MutableField(label = "Casts shadow")
    public boolean castsShadows = true;

    @MutableField(label = "Contribute to probes")
    public boolean contributeToProbes = true;

    @MutableField(label = "Primitive")
    public MeshStreamableResource primitive;

    @MutableField(label = "Number of instances", min = 1)
    public int numberOfInstances = 10;

    public transient RenderingRequest renderRequest;

    public InstancedMeshComponent(Entity entity, LinkedList<?> bag) {
        super(entity, bag);
    }

    public InstancedMeshComponent() {
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
