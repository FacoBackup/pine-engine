package com.pine.service.streaming.mesh;

import com.pine.repository.streaming.AbstractStreamableResource;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.rendering.VertexBuffer;
import com.pine.theme.Icons;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class MeshStreamableResource extends AbstractStreamableResource<MeshStreamData> {
    public transient int vertexCount;
    public transient int triangleCount;
    public transient int VAO;
    public transient int indexVBO;
    public transient VertexBuffer vertexVBO;
    public transient VertexBuffer uvVBO;
    public transient VertexBuffer normalVBO;

    public MeshStreamableResource(String pathToFile, String id) {
        super(pathToFile, id);
    }

    @Override
    protected void loadInternal(MeshStreamData dto) {
        this.triangleCount = dto.indices().length / 3;
        this.vertexCount = dto.vertices().length;

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
    protected void disposeInternal() {
        GL46.glDeleteVertexArrays(VAO);
        GL46.glDeleteBuffers(indexVBO);
        GL46.glDeleteBuffers(vertexVBO.getBuffer());
        if (uvVBO != null) {
            GL46.glDeleteBuffers(uvVBO.getBuffer());
        }
        if (normalVBO != null) {
            GL46.glDeleteBuffers(normalVBO.getBuffer());
        }
    }

    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.MESH;
    }

    @Override
    public String getIcon() {
        return Icons.category;
    }
}
