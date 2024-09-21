package com.pine.component;

import com.pine.PBean;
import com.pine.service.resource.primitives.mesh.Mesh;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@PBean
public class InstancedMeshComponent extends AbstractComponent<InstancedMeshComponent> {

    public static class TransformationDTO {
        public final Vector3f translation = new Vector3f();
        public final Quaternionf rotation = new Quaternionf();
        public final Vector3f scale = new Vector3f();
        public transient Matrix4f modelMatrix = new Matrix4f();
    }

    public static class AllocationDTO {
        public transient final FloatBuffer buffer;
        public transient final int glBuffer;
        public transient final int quantity;

        public AllocationDTO(FloatBuffer buffer, int glBuffer, int quantity) {
            this.buffer = buffer;
            this.glBuffer = glBuffer;
            this.quantity = quantity;
        }
    }

    public boolean castsShadows = true;
    public boolean contributeToProbes = true;
    public transient Mesh meshInstance;
    public String meshID;
    public int numberOfInstances = 10;
    public int instanceBaseTranslationOffset = 2;
    public final List<TransformationDTO> transformations = new ArrayList<>();
    public transient AllocationDTO allocation;

    public InstancedMeshComponent(Integer entityId) {
        super(entityId);
    }

    public InstancedMeshComponent() {
        super();
    }

    @Override
    protected Set<Class<? extends EntityComponent>> getDependenciesInternal() {
        return Set.of(TransformationComponent.class);
    }


    @Override
    public String getComponentName() {
        return "Instanced Mesh";
    }
}
