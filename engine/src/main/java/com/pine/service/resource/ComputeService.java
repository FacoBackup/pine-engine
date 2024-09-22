package com.pine.service.resource;

import com.pine.PBean;
import com.pine.PInject;
import com.pine.service.resource.compute.ComputeCreationData;
import com.pine.service.resource.compute.ComputeResource;
import com.pine.service.resource.compute.ComputeRuntimeData;
import com.pine.service.resource.resource.AbstractResourceService;
import com.pine.service.resource.resource.IResource;
import com.pine.service.resource.resource.ResourceType;
import org.lwjgl.opengl.GL46;

@PBean
public class ComputeService extends AbstractResourceService<ComputeResource, ComputeRuntimeData, ComputeCreationData> {
    private final static ComputeRuntimeData DEFAULT_COMPUTE = ComputeRuntimeData.ofNormalWorkGroup(GL46.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT);

    @PInject
    public ShaderService shaderService;

    @Override
    protected void bindInternal(ComputeResource instance, ComputeRuntimeData data) {
        GL46.glUseProgram(instance.getProgram());
        var uniforms = instance.getUniforms();
        for (var entry : data.getUniformData().entrySet()) {
            shaderService.bindUniform(uniforms.get(entry.getKey()), entry.getValue());
        }
        GL46.glDispatchCompute(data.groupX, data.groupY, data.groupZ);
        GL46.glMemoryBarrier(data.memoryBarrier);
    }

    @Override
    protected void bindInternal(ComputeResource instance) {
        GL46.glUseProgram(instance.getProgram());
        GL46.glDispatchCompute(DEFAULT_COMPUTE.groupX, DEFAULT_COMPUTE.groupY, DEFAULT_COMPUTE.groupZ);
        GL46.glMemoryBarrier(DEFAULT_COMPUTE.memoryBarrier);
    }

    @Override
    public void unbind() {
        GL46.glUseProgram(GL46.GL_NONE);
    }

    @Override
    protected IResource addInternal(ComputeCreationData data) {
        if (data.isLocalResource()) {
            return create(getId(), new ComputeCreationData(shaderService.processShader(data.code())));
        }
        return create(getId(), data);
    }

    private ComputeResource create(String id, ComputeCreationData data) {
        var instance = new ComputeResource(id, data);
        return (ComputeResource) shaderService.bindWithUBO(data.code(), instance);
    }

    @Override
    protected void removeInternal(ComputeResource shader) {
        GL46.glDeleteProgram(shader.getProgram());
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.COMPUTE;
    }
}
