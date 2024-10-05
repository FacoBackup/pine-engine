package com.pine.service.resource;

import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.service.resource.compute.Compute;
import com.pine.service.resource.compute.ComputeCreationData;
import com.pine.service.resource.compute.ComputeRuntimeData;
import com.pine.service.resource.shader.UniformDTO;
import org.lwjgl.opengl.GL46;

@PBean
public class ComputeService extends AbstractResourceService<Compute, ComputeCreationData> {
    private final static ComputeRuntimeData DEFAULT_COMPUTE = ComputeRuntimeData.ofNormalWorkGroup(GL46.GL_SHADER_STORAGE_BARRIER_BIT);

    @PInject
    public ShaderService shaderService;

    @Override
    protected void bindInternal(Compute instance) {
        GL46.glUseProgram(instance.getProgram());
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

    private Compute create(String id, ComputeCreationData data) {
        var instance = new Compute(id, data);
        return (Compute) shaderService.bindWithUBO(data.code(), instance);
    }

    public void dispatch(ComputeRuntimeData data){
        GL46.glDispatchCompute(data.groupX, data.groupY, data.groupZ);
        GL46.glMemoryBarrier(data.memoryBarrier);
        unbind();
    }

    public void dispatch(){
        GL46.glDispatchCompute(DEFAULT_COMPUTE.groupX, DEFAULT_COMPUTE.groupY, DEFAULT_COMPUTE.groupZ);
        GL46.glMemoryBarrier(DEFAULT_COMPUTE.memoryBarrier);
        unbind();
    }

    @Override
    protected void removeInternal(Compute shader) {
        GL46.glDeleteProgram(shader.getProgram());
    }

    @Override
    public LocalResourceType getResourceType() {
        return LocalResourceType.COMPUTE;
    }

    public void bindUniform(UniformDTO uniformDTO, Object value) {
        shaderService.bindUniform(uniformDTO, value);
    }
}
