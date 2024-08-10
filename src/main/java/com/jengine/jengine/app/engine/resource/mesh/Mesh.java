package com.jengine.jengine.app.engine.resource.mesh;

import jakarta.annotation.Nullable;
import org.lwjgl.opengl.GL30;
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

        vaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoId);

        vertexVboId = GL30.glGenBuffers();
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vertexVboId);
        FloatBuffer vertexBuffer = MemoryUtil.memAllocFloat(vertices.length);
        vertexBuffer.put(vertices).flip();
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, vertexBuffer, GL30.GL_STATIC_DRAW);
        GL30.glVertexAttribPointer(0, 3, GL30.GL_FLOAT, false, 0, 0);
        GL30.glEnableVertexAttribArray(0);
        MemoryUtil.memFree(vertexBuffer);

        indexVboId = GL30.glGenBuffers();
        GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, indexVboId);
        IntBuffer indexBuffer = MemoryUtil.memAllocInt(indices.length);
        indexBuffer.put(indices).flip();
        GL30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL30.GL_STATIC_DRAW);
        MemoryUtil.memFree(indexBuffer);

        if (uvs != null) {
            uvVboId = GL30.glGenBuffers();
            GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, uvVboId);
            FloatBuffer uvBuffer = MemoryUtil.memAllocFloat(uvs.length);
            uvBuffer.put(uvs).flip();
            GL30.glBufferData(GL30.GL_ARRAY_BUFFER, uvBuffer, GL30.GL_STATIC_DRAW);
            GL30.glVertexAttribPointer(1, 2, GL30.GL_FLOAT, false, 0, 0);
            GL30.glEnableVertexAttribArray(1);
            MemoryUtil.memFree(uvBuffer);
        }

        if (normals != null) {
            normalVboId = GL30.glGenBuffers();
            GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, normalVboId);
            FloatBuffer normalBuffer = MemoryUtil.memAllocFloat(normals.length);
            normalBuffer.put(normals).flip();
            GL30.glBufferData(GL30.GL_ARRAY_BUFFER, normalBuffer, GL30.GL_STATIC_DRAW);
            GL30.glVertexAttribPointer(2, 3, GL30.GL_FLOAT, false, 0, 0);
            GL30.glEnableVertexAttribArray(2);
            MemoryUtil.memFree(normalBuffer);
        }

        GL30.glBindVertexArray(0);
    }

    public void bindResources() {
        GL30.glBindVertexArray(vaoId);

        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vertexVboId);
        GL30.glEnableVertexAttribArray(0);

        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, uvVboId);
        GL30.glEnableVertexAttribArray(1);

        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, normalVboId);
        GL30.glEnableVertexAttribArray(2);

        GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, indexVboId);
    }

    public void unbindResources() {
        GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, 0);

        GL30.glDisableVertexAttribArray(0);
        GL30.glDisableVertexAttribArray(1);
        GL30.glDisableVertexAttribArray(2);

        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
    }

    public void draw() {
        bindResources();
        GL30.glDrawElements(GL30.GL_TRIANGLES, vertexCount, GL30.GL_UNSIGNED_INT, 0);
        GL30.glBindVertexArray(0);
    }

    /**
     * Draws the mesh as a line loop.
     */
    public void drawLineLoop() {
        bindResources();
        GL30.glDrawElements(GL30.GL_LINE_LOOP, vertexCount, GL30.GL_UNSIGNED_INT, 0);
        GL30.glBindVertexArray(0);
    }


    public void drawTriangleStrip() {
        bindResources();
        GL30.glDrawElements(GL30.GL_TRIANGLE_STRIP, vertexCount, GL30.GL_UNSIGNED_INT, 0);
        GL30.glBindVertexArray(0);
    }

    public void drawTriangleFan() {
        bindResources();
        GL30.glDrawElements(GL30.GL_TRIANGLE_FAN, vertexCount, GL30.GL_UNSIGNED_INT, 0);
        GL30.glBindVertexArray(0);
    }


    public void drawLines() {
        bindResources();
        GL30.glDrawElements(GL30.GL_LINES, vertexCount, GL30.GL_UNSIGNED_INT, 0);
        GL30.glBindVertexArray(0);
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







