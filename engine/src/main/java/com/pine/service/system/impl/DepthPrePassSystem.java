package com.pine.service.system.impl;

import com.pine.Loggable;
import com.pine.repository.rendering.PrimitiveRenderRequest;
import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.resource.primitives.GLSLType;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.system.AbstractSystem;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;
import java.util.List;

public class DepthPrePassSystem extends AbstractSystem implements Loggable {

    private UniformDTO transformationIndex;
    private final IntBuffer transformationIndexBuffer = MemoryUtil.memAllocInt(1);

    @Override
    public void onInitialize() {
        transformationIndex = shaderRepository.depthPrePassShader.addUniformDeclaration("transformationIndex", GLSLType.INT);
    }

    @Override
    protected FrameBufferObject getTargetFBO() {
        return fboRepository.sceneDepth;
    }

    @Override
    protected void renderInternal() {
        ssboService.bind(ssboRepository.transformationSSBO);
        shaderService.bind(shaderRepository.depthPrePassShader);
        List<PrimitiveRenderRequest> requests = renderingRepository.requests;
        int instancedOffset = 0;
        for (int i = 0; i < requests.size(); i++) {
            var request = requests.get(i);
            transformationIndexBuffer.put(0, (i + instancedOffset));
            shaderService.bindUniform(transformationIndex, transformationIndexBuffer);
            primitiveService.bind(request.mesh, request.runtimeData);
            instancedOffset += request.transformations.size();
        }
    }
}
