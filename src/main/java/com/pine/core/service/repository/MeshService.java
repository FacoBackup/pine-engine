package com.pine.core.service.repository;

import com.pine.core.service.common.IResource;
import com.pine.core.service.repository.primitives.mesh.Mesh;
import com.pine.core.service.common.IResourceService;
import com.pine.core.service.repository.primitives.mesh.MeshDTO;
import com.pine.core.service.repository.primitives.mesh.MeshRenderingMode;
import com.pine.core.service.repository.primitives.mesh.MeshRuntimeData;
import jakarta.annotation.Nullable;
import org.lwjgl.opengl.GL46;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public class MeshService implements IResourceService<Mesh, MeshRuntimeData, MeshDTO> {
    private Mesh currentMesh;
    private boolean isInWireframeMode = false;

    @Override
    public void bind(@NonNull Mesh instance, MeshRuntimeData data) {
        currentMesh = instance;
    }

    @Override
    public void bind(@NonNull Mesh instance) {
        currentMesh = instance;
    }

    private void bindInternal(@Nullable MeshRuntimeData data) {
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
    public IResource add(MeshDTO data) {
        return null;
    }

    @Override
    public void remove(Mesh id) {
        // TODO - remove last used and unbind if is bound for some reason (probably will never happen)
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

    public void unbindResources() {
        GL46.glBindBuffer(GL46.GL_ELEMENT_ARRAY_BUFFER, 0);

        GL46.glDisableVertexAttribArray(0);
        GL46.glDisableVertexAttribArray(1);
        GL46.glDisableVertexAttribArray(2);

        GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, 0);
        GL46.glBindVertexArray(0);
    }

    public void drawWireframe() {
        GL46.glPolygonMode(GL46.GL_FRONT_AND_BACK, GL46.GL_LINE);
        isInWireframeMode = true;
        draw();
    }

    public void draw() {
        bindResources();
        GL46.glDrawElements(GL46.GL_TRIANGLES, currentMesh.getVertexCount(), GL46.GL_UNSIGNED_INT, 0);
        GL46.glBindVertexArray(0);
    }

    /**
     * Draws the mesh as a line loop.
     */
    public void drawLineLoop() {
        bindResources();
        GL46.glDrawElements(GL46.GL_LINE_LOOP, currentMesh.getVertexCount(), GL46.GL_UNSIGNED_INT, 0);
        GL46.glBindVertexArray(0);
    }

    public void drawTriangleStrip() {
        bindResources();
        GL46.glDrawElements(GL46.GL_TRIANGLE_STRIP, currentMesh.getVertexCount(), GL46.GL_UNSIGNED_INT, 0);
        GL46.glBindVertexArray(0);
    }

    public void drawTriangleFan() {
        bindResources();
        GL46.glDrawElements(GL46.GL_TRIANGLE_FAN, currentMesh.getVertexCount(), GL46.GL_UNSIGNED_INT, 0);
        GL46.glBindVertexArray(0);
    }


    public void drawLines() {
        bindResources();
        GL46.glDrawElements(GL46.GL_LINES, currentMesh.getVertexCount(), GL46.GL_UNSIGNED_INT, 0);
        GL46.glBindVertexArray(0);
    }
}
