package com.pine.service.system.impl.gbuffer;

import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.streaming.ref.MaterialResourceRef;
import com.pine.service.streaming.ref.MeshResourceRef;
import com.pine.service.system.impl.FoliageCullingPass;
import org.lwjgl.opengl.GL46;


public class FoliageGBufferPass extends AbstractGBufferPass {
    private UniformDTO terrainOffsetU;
    private UniformDTO transformOffset;
    private UniformDTO objectScale;

    @Override
    public void onInitialize() {
        super.onInitialize();
        terrainOffsetU = addUniformDeclaration("terrainOffset");
        transformOffset = addUniformDeclaration("transformOffset");
        objectScale = addUniformDeclaration("objectScale");
    }

    @Override
    protected Shader getShader() {
        return shaderRepository.gBufferFoliageShader;
    }

    @Override
    protected boolean isRenderable() {
        return terrainRepository.enabled;
    }

    @Override
    protected void renderInternal() {
        prepareCall();
        shaderService.bindVec2(terrainRepository.offset, terrainOffsetU);
        shaderService.bindSampler2dDirect(bufferRepository.windNoiseSampler, 10);
        for (var foliage : terrainRepository.foliage.values()) {
            if(FoliageCullingPass.isFoliageNotReady(foliage)){
                continue;
            }

            var mesh = (MeshResourceRef) streamingService.streamIn(foliage.mesh, StreamableResourceType.MESH);
            var material = (MaterialResourceRef) streamingService.streamIn(foliage.material, StreamableResourceType.MATERIAL);
            bindMaterial(material);
            if (mesh != null) {
                shaderService.bindVec3(foliage.objectScale, objectScale);
                shaderService.bindInt(foliage.offset, transformOffset);
                meshService.bind(mesh);
                GL46.glBindBuffer(GL46.GL_DRAW_INDIRECT_BUFFER, foliage.indirectDrawBuffer);
                GL46.glBindBufferBase(GL46.GL_SHADER_STORAGE_BUFFER, 3, foliage.transformationsBuffer);
                GL46.glDrawElementsIndirect(GL46.GL_TRIANGLES, GL46.GL_UNSIGNED_INT, GL46.GL_NONE);
            }
        }
    }

    @Override
    public String getTitle() {
        return "Foliage rendering";
    }
}
