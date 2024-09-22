package com.pine.service.resource;

import com.pine.PBean;
import com.pine.PInject;
import com.pine.service.resource.primitives.mesh.MeshCreationData;
import com.pine.service.resource.primitives.mesh.MeshRenderingMode;
import com.pine.service.resource.primitives.mesh.MeshRuntimeData;
import com.pine.service.resource.primitives.mesh.Primitive;
import com.pine.service.resource.primitives.texture.TextureResource;
import com.pine.service.resource.resource.AbstractResourceService;
import com.pine.service.resource.resource.IResource;
import com.pine.service.resource.resource.ResourceType;
import org.lwjgl.opengl.GL46;

@PBean
public class MeshService extends AbstractResourceService<Primitive, MeshRuntimeData, MeshCreationData> {
    private Primitive currentMesh;
    private MeshRuntimeData drawCommand;
    private boolean isInWireframeMode = false;

    @PInject
    public ResourceService resourceService;


    @Override
    protected void bindInternal(Primitive instance, MeshRuntimeData data) {
        currentMesh = instance;
        drawCommand = data;
        draw();
    }

    @Override
    protected void bindInternal(Primitive instance) {
        currentMesh = instance;
        drawCommand = null;
        draw();
    }

    private void draw() {
        if(currentMesh == null){
            return;
        }
        if (isInWireframeMode && (drawCommand == null || drawCommand.mode != MeshRenderingMode.WIREFRAME)) {
            GL46.glPolygonMode(GL46.GL_FRONT_AND_BACK, GL46.GL_FILL);
            isInWireframeMode = false;
        }

        if (drawCommand == null) {
            draw(GL46.GL_TRIANGLES);
            return;
        }
        switch (drawCommand.mode) {
            case LINE_LOOP -> draw(GL46.GL_LINE_LOOP);
            case WIREFRAME -> wireframe();
            case TRIANGLE_FAN -> draw(GL46.GL_TRIANGLE_FAN);
            case TRIANGLE_STRIP -> draw(GL46.GL_TRIANGLE_STRIP);
            case LINES ->         draw(GL46.GL_LINES);
            default -> draw(GL46.GL_TRIANGLES);
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
        return new Primitive(getId(), data);
    }

    @Override
    public void removeInternal(Primitive id) {
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
        draw(GL46.GL_TRIANGLES);
    }

    private void draw(int mode) {
        bindResources();

        if (drawCommand != null && drawCommand.instanceCount > 0) {
            GL46.glDrawElementsInstanced(mode, currentMesh.vertexCount, GL46.GL_UNSIGNED_INT, 0, drawCommand.instanceCount );
            return;
        }
        GL46.glDrawElements(mode, currentMesh.vertexCount, GL46.GL_UNSIGNED_INT, 0);
    }

    public Primitive createTerrain(String heightMapTexture) {
        IResource byId = resourceService.getOrCreateResource(heightMapTexture);
        if(byId instanceof TextureResource){
            var t = (TextureResource) byId;
//            t.getWidth();
//            t.getHeight();
            // TODO - COMPUTE TERRAIN
        }
        return null;
    }
}
