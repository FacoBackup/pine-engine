package com.jengine.app.engine.resource.mesh;

import jakarta.annotation.Nullable;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Mesh {
    private final String id;
    private final int vertexCount;
    private final int triangleCount;
    private final int vaoId;
    private final int vertexVboId;
    private final int indexVboId;
    private Integer uvVboId;
    private Integer normalVboId;

    protected int lastUsed = 0;
    protected boolean loaded = false;

    public Mesh(String id, float[] vertices, int[] indices, @Nullable float[] normals, @Nullable float[] uvs) {
        this.id = id;
        this.triangleCount = indices.length / 3;
        this.vertexCount = indices.length;
        this.loaded = true;

        vaoId = GL46.glGenVertexArrays();
        GL46.glBindVertexArray(vaoId);

        vertexVboId = GL46.glGenBuffers();
        GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, vertexVboId);
        FloatBuffer vertexBuffer = MemoryUtil.memAllocFloat(vertices.length);
        vertexBuffer.put(vertices).flip();
        GL46.glBufferData(GL46.GL_ARRAY_BUFFER, vertexBuffer, GL46.GL_STATIC_DRAW);
        GL46.glVertexAttribPointer(0, 3, GL46.GL_FLOAT, false, 0, 0);
        GL46.glEnableVertexAttribArray(0);
        MemoryUtil.memFree(vertexBuffer);

        indexVboId = GL46.glGenBuffers();
        GL46.glBindBuffer(GL46.GL_ELEMENT_ARRAY_BUFFER, indexVboId);
        IntBuffer indexBuffer = MemoryUtil.memAllocInt(indices.length);
        indexBuffer.put(indices).flip();
        GL46.glBufferData(GL46.GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL46.GL_STATIC_DRAW);
        MemoryUtil.memFree(indexBuffer);

        if (uvs != null) {
            uvVboId = GL46.glGenBuffers();
            GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, uvVboId);
            FloatBuffer uvBuffer = MemoryUtil.memAllocFloat(uvs.length);
            uvBuffer.put(uvs).flip();
            GL46.glBufferData(GL46.GL_ARRAY_BUFFER, uvBuffer, GL46.GL_STATIC_DRAW);
            GL46.glVertexAttribPointer(1, 2, GL46.GL_FLOAT, false, 0, 0);
            GL46.glEnableVertexAttribArray(1);
            MemoryUtil.memFree(uvBuffer);
        }

        if (normals != null) {
            normalVboId = GL46.glGenBuffers();
            GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, normalVboId);
            FloatBuffer normalBuffer = MemoryUtil.memAllocFloat(normals.length);
            normalBuffer.put(normals).flip();
            GL46.glBufferData(GL46.GL_ARRAY_BUFFER, normalBuffer, GL46.GL_STATIC_DRAW);
            GL46.glVertexAttribPointer(2, 3, GL46.GL_FLOAT, false, 0, 0);
            GL46.glEnableVertexAttribArray(2);
            MemoryUtil.memFree(normalBuffer);
        }

        GL46.glBindVertexArray(0);
    }

    public void bindResources() {
        GL46.glBindVertexArray(vaoId);

        GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, vertexVboId);
        GL46.glEnableVertexAttribArray(0);

        GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, uvVboId);
        GL46.glEnableVertexAttribArray(1);

        GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, normalVboId);
        GL46.glEnableVertexAttribArray(2);

        GL46.glBindBuffer(GL46.GL_ELEMENT_ARRAY_BUFFER, indexVboId);
    }

    public void unbindResources() {
        GL46.glBindBuffer(GL46.GL_ELEMENT_ARRAY_BUFFER, 0);

        GL46.glDisableVertexAttribArray(0);
        GL46.glDisableVertexAttribArray(1);
        GL46.glDisableVertexAttribArray(2);

        GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, 0);
        GL46.glBindVertexArray(0);
    }

    public void draw() {
        bindResources();
        GL46.glDrawElements(GL46.GL_TRIANGLES, vertexCount, GL46.GL_UNSIGNED_INT, 0);
        GL46.glBindVertexArray(0);
    }

    /**
     * Draws the mesh as a line loop.
     */
    public void drawLineLoop() {
        bindResources();
        GL46.glDrawElements(GL46.GL_LINE_LOOP, vertexCount, GL46.GL_UNSIGNED_INT, 0);
        GL46.glBindVertexArray(0);
    }


    public void drawTriangleStrip() {
        bindResources();
        GL46.glDrawElements(GL46.GL_TRIANGLE_STRIP, vertexCount, GL46.GL_UNSIGNED_INT, 0);
        GL46.glBindVertexArray(0);
    }

    public void drawTriangleFan() {
        bindResources();
        GL46.glDrawElements(GL46.GL_TRIANGLE_FAN, vertexCount, GL46.GL_UNSIGNED_INT, 0);
        GL46.glBindVertexArray(0);
    }


    public void drawLines() {
        bindResources();
        GL46.glDrawElements(GL46.GL_LINES, vertexCount, GL46.GL_UNSIGNED_INT, 0);
        GL46.glBindVertexArray(0);
    }

    public String getId() {
        return id;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public int getTriangleCount() {
        return triangleCount;
    }

    public int getLastUsed() {
        return lastUsed;
    }

    public boolean isLoaded() {
        return loaded;
    }
}