package com.pine.service.resource.ssbo;

import com.pine.injection.PBean;
import com.pine.service.resource.AbstractResourceService;
import org.lwjgl.opengl.GL46;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

@PBean
public class SSBOService extends AbstractResourceService<SSBO, SSBOCreationData> {

    @Override
    public void bind(SSBO instance) {
        GL46.glBindBufferBase(GL46.GL_SHADER_STORAGE_BUFFER, instance.getBindingPoint(), instance.getBuffer());
    }

    @Override
    protected SSBO createInternal(SSBOCreationData data) {
        return new SSBO(data);
    }

    @Override
    public void unbind() {
        GL46.glBindBuffer(GL46.GL_SHADER_STORAGE_BUFFER, GL46.GL_NONE);
    }

    public void updateBuffer(SSBO ssbo, FloatBuffer data, int offset) {
        bind(ssbo);
        GL46.glBufferSubData(GL46.GL_SHADER_STORAGE_BUFFER, offset, data);
        unbind();
    }
}
