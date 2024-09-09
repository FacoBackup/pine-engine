package com.pine.engine.core.service.resource;

import com.pine.engine.core.service.resource.primitives.mesh.Mesh;
import com.pine.engine.core.service.resource.primitives.mesh.MeshCreationData;
import com.pine.engine.core.service.resource.primitives.mesh.MeshRenderingMode;
import com.pine.engine.core.service.resource.primitives.mesh.MeshRuntimeData;
import com.pine.engine.core.service.resource.resource.AbstractResourceService;
import com.pine.engine.core.service.resource.resource.IResource;
import com.pine.engine.core.service.resource.resource.ResourceType;
import jakarta.annotation.Nullable;
import org.lwjgl.opengl.GL46;

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
            draw();
            return;
        }
        switch (data.mode()) {
            case LINE_LOOP -> drawLineLoop();
            case WIREFRAME -> drawWireframe();
            case TRIANGLE_FAN -> drawTriangleFan();
            case TRIANGLE_STRIP -> drawTriangleStrip();
            case LINES -> drawLines();
            default -> draw();
        }
    }

    @Override
    public void unbind() {
        unbindResources();
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
        GL46.glBindVertexArray(currentMesh.getVaoId());

        GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, currentMesh.getVertexVboId());
        GL46.glEnableVertexAttribArray(0);

        if (currentMesh.getUvVboId() != null) {
            GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, currentMesh.getUvVboId());
            GL46.glEnableVertexAttribArray(1);
        }

        if (currentMesh.getNormalVboId() != null) {
            GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, currentMesh.getNormalVboId());
            GL46.glEnableVertexAttribArray(2);
        }

        GL46.glBindBuffer(GL46.GL_ELEMENT_ARRAY_BUFFER, currentMesh.getIndexVboId());
    }

    private void unbindResources() {
        GL46.glBindBuffer(GL46.GL_ELEMENT_ARRAY_BUFFER, 0);

        GL46.glDisableVertexAttribArray(0);
        GL46.glDisableVertexAttribArray(1);
        GL46.glDisableVertexAttribArray(2);

        GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, 0);
        GL46.glBindVertexArray(0);
    }

    private void drawWireframe() {
        GL46.glPolygonMode(GL46.GL_FRONT_AND_BACK, GL46.GL_LINE);
        isInWireframeMode = true;
        draw();
    }

    private void draw() {
        bindResources();
        GL46.glDrawElements(GL46.GL_TRIANGLES, currentMesh.getVertexCount(), GL46.GL_UNSIGNED_INT, 0);
        GL46.glBindVertexArray(0);
    }

    /**
     * Draws the mesh as a line loop.
     */
    private void drawLineLoop() {
        bindResources();
        GL46.glDrawElements(GL46.GL_LINE_LOOP, currentMesh.getVertexCount(), GL46.GL_UNSIGNED_INT, 0);
        GL46.glBindVertexArray(0);
    }

    private void drawTriangleStrip() {
        bindResources();
        GL46.glDrawElements(GL46.GL_TRIANGLE_STRIP, currentMesh.getVertexCount(), GL46.GL_UNSIGNED_INT, 0);
        GL46.glBindVertexArray(0);
    }

    private void drawTriangleFan() {
        bindResources();
        GL46.glDrawElements(GL46.GL_TRIANGLE_FAN, currentMesh.getVertexCount(), GL46.GL_UNSIGNED_INT, 0);
        GL46.glBindVertexArray(0);
    }

    private void drawLines() {
        bindResources();
        GL46.glDrawElements(GL46.GL_LINES, currentMesh.getVertexCount(), GL46.GL_UNSIGNED_INT, 0);
        GL46.glBindVertexArray(0);
    }
}
