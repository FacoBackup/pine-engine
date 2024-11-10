package com.pine.service.streaming.impl;

import com.pine.FSUtil;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.repository.ClockRepository;
import com.pine.repository.rendering.RenderingMode;
import com.pine.repository.streaming.AbstractResourceRef;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.repository.streaming.StreamingRepository;
import com.pine.service.streaming.data.StreamData;
import com.pine.service.streaming.ref.MeshResourceRef;
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
    public StreamData stream(String pathToFile, Map<String, StreamableResourceType> schedule, Map<String, AbstractResourceRef<?>> streamableResources){
        return (StreamData) FSUtil.readBinary(pathToFile);
    }

    @Override
    public AbstractResourceRef<?> newInstance(String key) {
        return new MeshResourceRef(key);
    }
}
