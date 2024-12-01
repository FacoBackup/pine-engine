package com.pine.engine.service.resource.ubo;

import com.pine.engine.service.resource.AbstractEngineResource;
import org.lwjgl.opengl.GL46;

public class UBO extends AbstractEngineResource {
    private static int blockPointIncrement = 0;

    private final int buffer;
    private final String blockName;
    private final int blockPoint;

    public UBO(UBOCreationData dto) {
        int bufferSize = UBOService.calculateAllocation(dto.data());

        this.blockName = dto.blockName();
        this.blockPoint = blockPointIncrement++;
        buffer = GL46.glCreateBuffers();
        GL46.glBindBuffer(GL46.GL_UNIFORM_BUFFER, buffer);
        GL46.glBufferData(GL46.GL_UNIFORM_BUFFER, bufferSize, GL46.GL_DYNAMIC_DRAW);
        GL46.glBindBuffer(GL46.GL_UNIFORM_BUFFER, GL46.GL_NONE);
        GL46.glBindBufferBase(GL46.GL_UNIFORM_BUFFER, this.blockPoint, buffer);
    }

    public int getBuffer() {
        return buffer;
    }

    public String getBlockName() {
        return blockName;
    }

    public int getBlockPoint() {
        return blockPoint;
    }

    @Override
    public void dispose() {
        GL46.glDeleteBuffers(buffer);
    }
}
