package com.pine.core.service.resource;

import com.pine.core.EngineInjectable;
import com.pine.core.service.resource.primitives.mesh.Mesh;
import com.pine.core.service.resource.primitives.mesh.MeshCreationData;
import com.pine.core.service.resource.primitives.mesh.MeshRenderingMode;
import com.pine.core.service.resource.primitives.mesh.MeshRuntimeData;
import com.pine.core.service.resource.resource.AbstractResourceService;
import com.pine.core.service.resource.resource.IResource;
import com.pine.core.service.resource.resource.ResourceType;
import org.lwjgl.opengl.GL46;

@EngineInjectable
public class MeshService extends AbstractResourceService<Mesh, MeshRuntimeData, MeshCreationData> {
    private Mesh currentMesh;
    private MeshRuntimeData drawCommand;
    private boolean isInWireframeMode = false;

    @Override
    protected void bindInternal(Mesh instance, MeshRuntimeData data) {
        currentMesh = instance;
        drawCommand = data;
        draw();
    }

    @Override
    protected void bindInternal(Mesh instance) {
        currentMesh = instance;
        drawCommand = null;
        draw();
    }

    private void draw() {
        if (isInWireframeMode && (drawCommand == null || drawCommand.mode != MeshRenderingMode.WIREFRAME)) {
            GL46.glPolygonMode(GL46.GL_FRONT_AND_BACK, GL46.GL_FILL);
            isInWireframeMode = false;
        }

        if (drawCommand == null) {
            triangles();
            return;
        }
        switch (drawCommand.mode) {
            case LINE_LOOP -> lineLoop();
            case WIREFRAME -> wireframe();
            case TRIANGLE_FAN -> triangleFan();
            case TRIANGLE_STRIP -> triangleStrip();
            case LINES -> lines();
            default -> triangles();
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

    private void wireframe() {
        GL46.glPolygonMode(GL46.GL_FRONT_AND_BACK, GL46.GL_LINE);
        isInWireframeMode = true;
        triangles();
    }

    private void triangles() {
        bindResources();
        draw(GL46.GL_TRIANGLES);
        GL46.glBindVertexArray(GL46.GL_NONE);
    }

    /**
     * Draws the mesh as a line loop.
     */
    private void lineLoop() {
        bindResources();
        draw(GL46.GL_LINE_LOOP);
        GL46.glBindVertexArray(GL46.GL_NONE);
    }

    private void triangleStrip() {
        bindResources();
        draw(GL46.GL_TRIANGLE_STRIP);
        GL46.glBindVertexArray(GL46.GL_NONE);
    }

    private void triangleFan() {
        bindResources();
        draw(GL46.GL_TRIANGLE_FAN);
        GL46.glBindVertexArray(GL46.GL_NONE);
    }

    private void lines() {
        bindResources();
        draw(GL46.GL_LINES);
        GL46.glBindVertexArray(GL46.GL_NONE);
    }

    private void draw(int glTriangles) {
        if (drawCommand != null && drawCommand.instanceCount > 0) {
            GL46.glDrawElementsInstanced(GL46.GL_TRIANGLES, drawCommand.instanceCount, GL46.GL_UNSIGNED_INT, 0, drawCommand.instanceCount);
            return;
        }
        GL46.glDrawElements(glTriangles, currentMesh.vertexCount, GL46.GL_UNSIGNED_INT, 0);
    }
}
