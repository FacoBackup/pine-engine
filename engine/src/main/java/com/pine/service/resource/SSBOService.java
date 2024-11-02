package com.pine.service.resource;

import com.pine.injection.PBean;
import com.pine.service.resource.ssbo.ShaderStorageBufferObject;
import org.lwjgl.opengl.GL46;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

@PBean
public class SSBOService extends AbstractResourceService<ShaderStorageBufferObject> {

    @Override
    public void bind(ShaderStorageBufferObject instance) {
        GL46.glBindBufferBase(GL46.GL_SHADER_STORAGE_BUFFER, instance.getBindingPoint(), instance.getBuffer());
    }

    @Override
    public void unbind() {
        GL46.glBindBuffer(GL46.GL_SHADER_STORAGE_BUFFER, GL46.GL_NONE);
    }

    public void updateBuffer(ShaderStorageBufferObject ssbo, FloatBuffer data, int offset) {
        bind(ssbo);
        GL46.glBufferSubData(GL46.GL_SHADER_STORAGE_BUFFER, offset, data);
        unbind();
    }

    public void updateBuffer(ShaderStorageBufferObject ssbo, IntBuffer data, int offset) {
        bind(ssbo);
        GL46.glBufferSubData(GL46.GL_SHADER_STORAGE_BUFFER, offset, data);
    }
}
