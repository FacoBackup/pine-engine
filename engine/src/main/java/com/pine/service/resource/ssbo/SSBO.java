package com.pine.service.resource.ssbo;

import com.pine.service.resource.AbstractEngineResource;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL46;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class SSBO extends AbstractEngineResource {

    private int bindingPoint;
    private final int buffer;

    public SSBO(SSBOCreationData dto) {
        buffer = GL46.glCreateBuffers();
        GL46.glBindBuffer(GL46.GL_SHADER_STORAGE_BUFFER, buffer);
        if (dto.getData() != null) {
            if (dto.getData() instanceof IntBuffer) {
                GL46.glBufferData(GL46.GL_SHADER_STORAGE_BUFFER, (IntBuffer) dto.getData(), GL15.GL_STATIC_DRAW);
            } else if (dto.getData() instanceof FloatBuffer) {
                GL46.glBufferData(GL46.GL_SHADER_STORAGE_BUFFER, (FloatBuffer) dto.getData(), GL15.GL_STATIC_DRAW);
            }
        } else {
            GL46.glBufferData(GL46.GL_SHADER_STORAGE_BUFFER, dto.getExpectedSize(), GL46.GL_DYNAMIC_COPY);
        }
        GL46.glBindBuffer(GL46.GL_SHADER_STORAGE_BUFFER, GL46.GL_NONE);
        bindingPoint = dto.getBindingPoint();
    }

    public int getBuffer() {
        return buffer;
    }

    public int getBindingPoint() {
        return bindingPoint;
    }

    public void setBindingPoint(int bindingPoint) {
        this.bindingPoint = bindingPoint;
    }

    @Override
    public void dispose() {
        GL46.glDeleteBuffers(buffer);
    }
}
