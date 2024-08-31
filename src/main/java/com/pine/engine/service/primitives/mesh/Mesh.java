package com.pine.engine.service.primitives.mesh;

import com.pine.common.resource.ResourceType;
import com.pine.common.resource.AbstractResource;
import jakarta.annotation.Nullable;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Mesh extends AbstractResource<MeshDTO> {
    private final int vertexCount;
    private final int triangleCount;
    private final int vaoId;
    private final int vertexVboId;
    private final int indexVboId;
    private Integer uvVboId;
    private Integer normalVboId;

    public Mesh(String id, MeshDTO dto) {
        super(id);
        this.triangleCount = dto.indices().length / 3;
        this.vertexCount = dto.indices().length;

        vaoId = GL46.glGenVertexArrays();
        GL46.glBindVertexArray(vaoId);

        vertexVboId = GL46.glGenBuffers();
        GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, vertexVboId);
        FloatBuffer vertexBuffer = MemoryUtil.memAllocFloat(dto.vertices().length);
        vertexBuffer.put(dto.vertices()).flip();
        GL46.glBufferData(GL46.GL_ARRAY_BUFFER, vertexBuffer, GL46.GL_STATIC_DRAW);
        GL46.glVertexAttribPointer(0, 3, GL46.GL_FLOAT, false, 0, 0);
        GL46.glEnableVertexAttribArray(0);
        MemoryUtil.memFree(vertexBuffer);

        indexVboId = GL46.glGenBuffers();
        GL46.glBindBuffer(GL46.GL_ELEMENT_ARRAY_BUFFER, indexVboId);
        IntBuffer indexBuffer = MemoryUtil.memAllocInt(dto.indices().length);
        indexBuffer.put(dto.indices()).flip();
        GL46.glBufferData(GL46.GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL46.GL_STATIC_DRAW);
        MemoryUtil.memFree(indexBuffer);

        if (dto.uvs() != null) {
            uvVboId = GL46.glGenBuffers();
            GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, uvVboId);
            FloatBuffer uvBuffer = MemoryUtil.memAllocFloat(dto.uvs().length);
            uvBuffer.put(dto.uvs()).flip();
            GL46.glBufferData(GL46.GL_ARRAY_BUFFER, uvBuffer, GL46.GL_STATIC_DRAW);
            GL46.glVertexAttribPointer(1, 2, GL46.GL_FLOAT, false, 0, 0);
            GL46.glEnableVertexAttribArray(1);
            MemoryUtil.memFree(uvBuffer);
        }

        if (dto.normals() != null) {
            normalVboId = GL46.glGenBuffers();
            GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, normalVboId);
            FloatBuffer normalBuffer = MemoryUtil.memAllocFloat(dto.normals().length);
            normalBuffer.put(dto.normals()).flip();
            GL46.glBufferData(GL46.GL_ARRAY_BUFFER, normalBuffer, GL46.GL_STATIC_DRAW);
            GL46.glVertexAttribPointer(2, 3, GL46.GL_FLOAT, false, 0, 0);
            GL46.glEnableVertexAttribArray(2);
            MemoryUtil.memFree(normalBuffer);
        }

        GL46.glBindVertexArray(0);
    }

    public int getVaoId() {
        return vaoId;
    }

    public int getVertexVboId() {
        return vertexVboId;
    }

    public int getIndexVboId() {
        return indexVboId;
    }

    @Nullable
    public Integer getUvVboId() {
        return uvVboId;
    }

    @Nullable
    public Integer getNormalVboId() {
        return normalVboId;
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.MESH;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public int getTriangleCount() {
        return triangleCount;
    }
}