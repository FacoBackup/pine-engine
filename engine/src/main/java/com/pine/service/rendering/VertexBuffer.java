package com.pine.service.rendering;

import org.lwjgl.opengl.GL46;

import java.nio.FloatBuffer;

public class VertexBuffer {
    private final int id;
    private final int stride;
    private final int index;
    private final int type;
    private final int size;
    private final boolean normalized;

    public VertexBuffer(int index, FloatBuffer data, int type, int size, int dataType, boolean normalized, int renderingType, int stride) {
        id = GL46.glGenBuffers();
        GL46.glBindBuffer(type, id);
        GL46.glBufferData(type, data, renderingType);

        GL46.glVertexAttribPointer(index, size, dataType, normalized, stride, 0);
        GL46.glBindBuffer(type, 0);

        this.stride = stride;
        this.index = index;
        this.type = type;
        this.size = size;
        this.normalized = normalized;
    }

    public void enable() {
        GL46.glEnableVertexAttribArray(this.index);
        GL46.glBindBuffer(this.type, this.id);
        GL46.glVertexAttribPointer(this.index, this.size, this.type, this.normalized, this.stride, 0);
    }

    public void disable() {
        GL46.glDisableVertexAttribArray(this.index);
        GL46.glBindBuffer(this.type, 0);
    }

    public void delete() {
        GL46.glDeleteBuffers(this.id);
    }

    public int getBuffer() {
        return id;
    }
}
