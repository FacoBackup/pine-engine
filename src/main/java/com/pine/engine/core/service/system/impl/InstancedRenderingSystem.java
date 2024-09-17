package com.pine.engine.core.service.system.impl;

import com.pine.engine.core.EngineDependency;
import com.pine.engine.core.EngineInjectable;
import com.pine.engine.core.component.InstancedMeshComponent;
import com.pine.engine.core.repository.CoreResourceRepository;
import com.pine.engine.core.service.resource.MeshService;
import com.pine.engine.core.service.resource.ShaderService;
import com.pine.engine.core.service.resource.fbo.FBO;
import com.pine.engine.core.service.resource.primitives.GLSLType;
import com.pine.engine.core.service.resource.primitives.mesh.MeshRenderingMode;
import com.pine.engine.core.service.resource.primitives.mesh.MeshRuntimeData;
import com.pine.engine.core.service.resource.shader.UniformDTO;
import com.pine.engine.core.service.system.AbstractSystem;
import com.pine.engine.core.service.world.WorldService;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.List;

public class InstancedRenderingSystem extends AbstractSystem {
    private static final int MAT4_SIZE = 16;
    private static final MeshRuntimeData DRAW_COMMAND = new MeshRuntimeData(MeshRenderingMode.TRIANGLES, 0);

    @EngineDependency
    public InstancedMeshComponent instancedMeshes;

    @EngineDependency
    public WorldService worldService;

    @EngineDependency
    public CoreResourceRepository coreResourceRepository;

    @EngineDependency
    public ShaderService shaderService;

    @EngineDependency
    public MeshService meshService;

    private UniformDTO baseModelMatrix;

    @Override
    public void onInitialize() {
        baseModelMatrix = coreResourceRepository.demoInstancedShader.addUniformDeclaration("baseModelMatrix", GLSLType.MAT_4);
    }

    @Override
    protected FBO getTargetFBO() {
        return coreResourceRepository.finalFrame;
    }

    @Override
    protected boolean shouldClearFBO() {
        return true;
    }

    @Override
    protected void renderInternal() {
        shaderService.bind(coreResourceRepository.demoInstancedShader);

        for (var component : instancedMeshes.bag) {
            var mesh = (InstancedMeshComponent) component;
            DRAW_COMMAND.instanceCount = mesh.numberOfInstances;
            generateBuffers(mesh);
            shaderService.bindUniform(baseModelMatrix, worldService.getTransformationComponentUnchecked(mesh.getEntityId()));
            meshService.bind(coreResourceRepository.cubeMesh);
            meshService.unbind();
        }

        shaderService.unbind();
    }

    private void generateBuffers(InstancedMeshComponent component) {
        if (component.allocation != null && component.allocation.quantity != component.numberOfInstances) {
            MemoryUtil.memFree(component.allocation.buffer);
            GL46.glDeleteBuffers(component.allocation.glBuffer);
            component.allocation = null;
        }

        if (component.allocation == null) {
            FloatBuffer buffer = buildBuffer(component);

            int instanceVBO = GL46.glGenBuffers();
            GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, instanceVBO);
            GL46.glBufferData(GL46.GL_ARRAY_BUFFER, buffer, GL46.GL_STATIC_DRAW);
            int vec4Size = 4 * Float.BYTES;

            for (int i = 0; i < 4; i++) {
                GL46.glEnableVertexAttribArray(3 + i);
                GL46.glVertexAttribPointer(3 + i, 4, GL46.GL_FLOAT, false, MAT4_SIZE * Float.BYTES, i * vec4Size);
                GL46.glVertexAttribDivisor(3 + i, 1);
            }

            GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, 0);
            component.allocation = new InstancedMeshComponent.AllocationDTO(buffer, instanceVBO, component.numberOfInstances);
        }
    }

    private static FloatBuffer buildBuffer(InstancedMeshComponent component) {
        FloatBuffer buffer = MemoryUtil.memAllocFloat(component.numberOfInstances * MAT4_SIZE);
        buildTransformationList(component);
        for (var transformation : component.transformations) {
            transformation.modelMatrix.get(buffer);
        }
        buffer.flip();
        return buffer;
    }

    private static void buildTransformationList(InstancedMeshComponent component) {
        if (component.transformations.size() != component.numberOfInstances) {
            component.transformations.clear();
            while (component.transformations.size() < component.numberOfInstances) {
                var transform = new InstancedMeshComponent.TransformationDTO();
                transform.translation.x = component.instanceBaseTranslationOffset;
                transform.translation.y = component.instanceBaseTranslationOffset;
                transform.translation.z = component.instanceBaseTranslationOffset;
                transform.modelMatrix.translate(transform.translation);
                component.transformations.add(transform);
            }
        }
    }
}
