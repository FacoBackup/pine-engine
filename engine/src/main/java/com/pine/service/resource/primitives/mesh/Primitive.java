package com.pine.service.resource.primitives.mesh;

import com.pine.service.resource.resource.AbstractResource;
import com.pine.service.resource.resource.ResourceType;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Primitive extends AbstractResource {
    public final int vertexCount;
    public final int triangleCount;
    public final int VAO;
    public final int indexVBO;
    public final VertexBuffer vertexVBO;
    public final VertexBuffer uvVBO;
    public final VertexBuffer normalVBO;

    public Primitive(String id, MeshCreationData dto) {
        super(id);
        this.triangleCount = dto.indices().length / 3;
        this.vertexCount = dto.indices().length;

        FloatBuffer vertexBuffer = MemoryUtil.memAllocFloat(dto.vertices().length);
        vertexBuffer.put(dto.vertices()).flip();

        IntBuffer indexBuffer = MemoryUtil.memAllocInt(dto.indices().length);
        indexBuffer.put(dto.indices()).flip();


        this.VAO = GL46.glCreateVertexArrays();
        GL46.glBindVertexArray(this.VAO);

        indexVBO = GL46.glCreateBuffers();
        GL46.glBindBuffer(GL46.GL_ELEMENT_ARRAY_BUFFER, indexVBO);
        GL46.glBufferData(GL46.GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL46.GL_STATIC_DRAW);

        this.vertexVBO = new VertexBuffer(0, vertexBuffer, GL46.GL_ARRAY_BUFFER, 3, GL46.GL_FLOAT, false, GL46.GL_STATIC_DRAW, 0);

        if (dto.uvs() != null && dto.uvs().length > 0) {
            FloatBuffer uvBuffer = MemoryUtil.memAllocFloat(dto.uvs().length);
            uvBuffer.put(dto.uvs()).flip();
            this.uvVBO = new VertexBuffer(1, uvBuffer, GL46.GL_ARRAY_BUFFER, 2, GL46.GL_FLOAT, false, GL46.GL_STATIC_DRAW, 0);
            MemoryUtil.memFree(uvBuffer);
        } else {
            this.uvVBO = null;
        }

        if (dto.normals() != null && dto.normals().length > 0) {
            FloatBuffer normalBuffer = MemoryUtil.memAllocFloat(dto.normals().length);
            normalBuffer.put(dto.normals()).flip();
            this.normalVBO = new VertexBuffer(2, normalBuffer, GL46.GL_ARRAY_BUFFER, 3, GL46.GL_FLOAT, false, GL46.GL_STATIC_DRAW, 0);
            MemoryUtil.memFree(normalBuffer);
        } else {
            this.normalVBO = null;
        }

        GL46.glBindVertexArray(GL46.GL_NONE);
        GL46.glBindBuffer(GL46.GL_ELEMENT_ARRAY_BUFFER, GL46.GL_NONE);
        GL46.glBindVertexArray(0);
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.PRIMITIVE;
    }
}