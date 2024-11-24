package com.pine.engine.service.streaming.impl;

import com.pine.FSUtil;
import com.pine.common.injection.PBean;
import com.pine.common.injection.PInject;
import com.pine.engine.repository.ClockRepository;
import com.pine.engine.repository.rendering.RenderingMode;
import com.pine.engine.repository.streaming.AbstractResourceRef;
import com.pine.engine.repository.streaming.StreamableResourceType;
import com.pine.engine.repository.streaming.StreamingRepository;
import com.pine.engine.repository.terrain.TerrainChunk;
import com.pine.engine.repository.terrain.TerrainRepository;
import com.pine.engine.service.resource.shader.ShaderService;
import com.pine.engine.service.resource.shader.UniformDTO;
import com.pine.engine.service.streaming.StreamingService;
import com.pine.engine.service.streaming.data.StreamData;
import com.pine.engine.service.streaming.ref.MeshResourceRef;
import com.pine.engine.service.streaming.ref.TextureResourceRef;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL46;

import java.util.Map;

@PBean
public class MeshService extends AbstractStreamableService<MeshResourceRef> {
    private RenderingMode renderingMode;
    private int instanceCount;
    private boolean isInWireframeMode = false;

    @PInject
    public StreamingRepository repository;

    @PInject
    public ClockRepository clockRepository;

    @PInject
    public TerrainRepository terrain;

    @PInject
    public StreamingService streamingService;

    @PInject
    public ShaderService shaderService;

    public void setRenderingMode(RenderingMode renderingMode) {
        this.renderingMode = renderingMode;
    }

    public void setInstanceCount(int instanceCount) {
        this.instanceCount = instanceCount;
    }

    public void draw() {
        if (currentResource == null) {
            return;
        }
        if (isInWireframeMode && (renderingMode == null || renderingMode != RenderingMode.WIREFRAME)) {
            GL46.glPolygonMode(GL46.GL_FRONT_AND_BACK, GL46.GL_FILL);
            isInWireframeMode = false;
        }

        if (renderingMode == null) {
            draw(GL46.GL_TRIANGLES);
            return;
        }
        switch (renderingMode) {
            case LINE_LOOP -> draw(GL46.GL_LINE_LOOP);
            case WIREFRAME -> wireframe();
            case TRIANGLE_FAN -> draw(GL46.GL_TRIANGLE_FAN);
            case TRIANGLE_STRIP -> draw(GL46.GL_TRIANGLE_STRIP);
            case LINES -> draw(GL46.GL_LINES);
            default -> draw(GL46.GL_TRIANGLES);
        }
        setInstanceCount(0);
    }

    private void wireframe() {
        GL46.glPolygonMode(GL46.GL_FRONT_AND_BACK, GL46.GL_LINE);
        isInWireframeMode = true;
        draw(GL46.GL_TRIANGLES);
    }

    private void draw(int mode) {
        if (renderingMode != null && instanceCount > 0) {
            GL46.glDrawElementsInstanced(mode, currentResource.vertexCount, GL46.GL_UNSIGNED_INT, 0, instanceCount);
            return;
        }
        GL46.glDrawElements(mode, currentResource.indicesCount, GL46.GL_UNSIGNED_INT, 0);
    }

    @Override
    protected void bindInternal() {
        currentResource.lastUse = clockRepository.totalTime;
        GL46.glBindVertexArray(currentResource.VAO);
        GL46.glBindBuffer(GL46.GL_ELEMENT_ARRAY_BUFFER, currentResource.indexVBO);
        currentResource.vertexVBO.enable();
        if (currentResource.normalVBO != null) {
            currentResource.normalVBO.enable();
        }
        if (currentResource.uvVBO != null) {
            currentResource.uvVBO.enable();
        }
    }

    @Override
    public void unbind() {
        if (currentResource == null) {
            return;
        }
        GL46.glBindBuffer(GL46.GL_ELEMENT_ARRAY_BUFFER, GL46.GL_NONE);
        currentResource.vertexVBO.disable();

        if (currentResource.uvVBO != null) {
            currentResource.uvVBO.disable();
        }
        if (currentResource.normalVBO != null) {
            currentResource.normalVBO.disable();
        }

        GL46.glBindVertexArray(GL46.GL_NONE);
        currentResource = null;
    }

    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.MESH;
    }

    @Override
    public StreamData stream(String pathToFile, Map<String, StreamableResourceType> schedule, Map<String, AbstractResourceRef<?>> streamableResources) {
        return (StreamData) FSUtil.readBinary(pathToFile);
    }

    @Override
    public AbstractResourceRef<?> newInstance(String key) {
        return new MeshResourceRef(key);
    }

    public void renderTerrain(UniformDTO textureSize, UniformDTO terrainOffset, UniformDTO heightScale, UniformDTO tilesScaleTranslation) {
        if (terrain.chunks == null) {
            return;
        }

        TextureResourceRef heightMap = (TextureResourceRef) streamingService.streamIn(terrain.heightMapTexture, StreamableResourceType.TEXTURE);
        if(heightMap == null){
            return;
        }
        heightMap.lastUse = clockRepository.totalTime;

        Vector4f terrainLocation = new Vector4f();

        shaderService.bindFloat(terrain.heightScale, heightScale);
        shaderService.bindInt(heightMap.width, textureSize);
        shaderService.bindVec2(terrain.offset, terrainOffset);

        shaderService.bindInt(heightMap.width, textureSize);
        shaderService.bindSampler2dDirect(heightMap, 8);

        for (TerrainChunk chunk : terrain.chunks) {
            if (chunk.isCulled()) {
                continue;
            }
            terrainLocation.x = chunk.getTiles();
            terrainLocation.y = chunk.getDivider();
            terrainLocation.z = chunk.x * terrain.quads;
            terrainLocation.w = chunk.z * terrain.quads;
            shaderService.bindVec4(terrainLocation, tilesScaleTranslation);

            GL46.glDrawArrays(GL46.GL_TRIANGLES, 0, chunk.getTriangles());
        }
    }
}
