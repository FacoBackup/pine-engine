package com.pine.engine.core.service.resource;

import com.pine.engine.core.EngineInjectable;
import com.pine.engine.core.service.resource.primitives.mesh.Mesh;
import com.pine.engine.core.service.resource.primitives.mesh.MeshCreationData;
import com.pine.engine.core.service.resource.primitives.mesh.MeshRenderingMode;
import com.pine.engine.core.service.resource.primitives.mesh.MeshRuntimeData;
import com.pine.engine.core.service.resource.resource.AbstractResourceService;
import com.pine.engine.core.service.resource.resource.IResource;
import com.pine.engine.core.service.resource.resource.ResourceType;
import jakarta.annotation.Nullable;
import org.lwjgl.opengl.GL46;

@EngineInjectable
public class MeshService extends AbstractResourceService<Mesh, MeshRuntimeData, MeshCreationData> {
    private Mesh currentMesh;
    private boolean isInWireframeMode = false;

    @Override
    protected void bindInternal(Mesh instance, MeshRuntimeData data) {
        currentMesh = instance;
        draw(data);
    }

    @Override
    protected void bindInternal(Mesh instance) {
        currentMesh = instance;
        draw(null);
    }

    public void draw(@Nullable MeshRuntimeData data) {
        if (isInWireframeMode && (data == null || data.mode() != MeshRenderingMode.WIREFRAME)) {
            GL46.glPolygonMode(GL46.GL_FRONT_AND_BACK, GL46.GL_FILL);
            isInWireframeMode = false;
        }

        if (data == null) {
            drawTriangles();
            return;
        }
        switch (data.mode()) {
            case LINE_LOOP -> drawLineLoop();
            case WIREFRAME -> drawWireframe();
            case TRIANGLE_FAN -> drawTriangleFan();
            case TRIANGLE_STRIP -> drawTriangleStrip();
            case LINES -> drawLines();
            default -> drawTriangles();
        }
    }

    @Override
    public void unbind() {
        GL46.glBindBuffer(GL46.GL_ELEMENT_ARRAY_BUFFER, GL46.GL_NONE);
        currentMesh.vertexVBO.disable();

        if (currentMesh.uvVBO != null) {
            currentMesh.uvVBO.disable();
        }
        if (currentMesh.normalVBO != null) {
            currentMesh.normalVBO.disable();
        }

        GL46.glBindVertexArray(GL46.GL_NONE);
        currentMesh = null;
    }

    @Override
    public IResource addInternal(MeshCreationData data) {
        return new Mesh(getId(), data);
    }

    @Override
    public void removeInternal(Mesh id) {
        // TODO - remove last used and unbind if is bound for some reason (probably will never happen)
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.MESH;
    }

    public void bindResources() {
        GL46.glBindVertexArray(currentMesh.VAO);
        GL46.glBindBuffer(GL46.GL_ELEMENT_ARRAY_BUFFER, currentMesh.indexVBO);
        currentMesh.vertexVBO.enable();
        if (currentMesh.normalVBO != null) {
            currentMesh.normalVBO.enable();
        }
        if (currentMesh.uvVBO != null) {
            currentMesh.uvVBO.enable();
        }
    }

    private void drawWireframe() {
        GL46.glPolygonMode(GL46.GL_FRONT_AND_BACK, GL46.GL_LINE);
        isInWireframeMode = true;
        drawTriangles();
    }

    private void drawTriangles() {
        bindResources();
        GL46.glDrawElements(GL46.GL_TRIANGLES, currentMesh.vertexCount, GL46.GL_UNSIGNED_INT, 0);
        GL46.glBindVertexArray(GL46.GL_NONE);
    }

    /**
     * Draws the mesh as a line loop.
     */
    private void drawLineLoop() {
        bindResources();
        GL46.glDrawElements(GL46.GL_LINE_LOOP, currentMesh.vertexCount, GL46.GL_UNSIGNED_INT, 0);
        GL46.glBindVertexArray(GL46.GL_NONE);
    }

    private void drawTriangleStrip() {
        bindResources();
        GL46.glDrawElements(GL46.GL_TRIANGLE_STRIP, currentMesh.vertexCount, GL46.GL_UNSIGNED_INT, 0);
        GL46.glBindVertexArray(GL46.GL_NONE);
    }

    private void drawTriangleFan() {
        bindResources();
        GL46.glDrawElements(GL46.GL_TRIANGLE_FAN, currentMesh.vertexCount, GL46.GL_UNSIGNED_INT, 0);
        GL46.glBindVertexArray(GL46.GL_NONE);
    }

    private void drawLines() {
        bindResources();
        GL46.glDrawElements(GL46.GL_LINES, currentMesh.vertexCount, GL46.GL_UNSIGNED_INT, 0);
        GL46.glBindVertexArray(GL46.GL_NONE);
    }
}
