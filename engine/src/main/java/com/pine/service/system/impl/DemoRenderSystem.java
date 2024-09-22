package com.pine.service.system.impl;

import com.pine.repository.rendering.PrimitiveRenderRequest;
import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.resource.primitives.GLSLType;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.system.AbstractSystem;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;
import java.util.List;

public class DemoRenderSystem extends AbstractSystem {
    private UniformDTO transformationIndex;
    private final IntBuffer transformationIndexBuffer = MemoryUtil.memAllocInt(1);

    @Override
    protected FrameBufferObject getTargetFBO() {
        return engine.getTargetFBO();
    }

    @Override
    public void onInitialize() {
        super.onInitialize();
        transformationIndex = shaderRepository.demoShader.addUniformDeclaration("transformationIndex", GLSLType.INT);
    }

    @Override
    protected void renderInternal() {
        ssboService.bind(ssboRepository.transformationSSBO);
        ssboService.bind(ssboRepository.modelSSBO);
        shaderService.bind(shaderRepository.demoShader);

        List<PrimitiveRenderRequest> requests = renderingRepository.requests;
        int instancedOffset = 0;
        for (int i = 0; i < requests.size(); i++) {
            var request = requests.get(i);
            transformationIndexBuffer.put(0, (i + instancedOffset) * 3);
            shaderService.bindUniform(transformationIndex, transformationIndexBuffer);
            meshService.bind(request.primitive, request.runtimeData);
            meshService.unbind();
            instancedOffset += request.transformations.size();
        }
        shaderService.unbind();
        ssboService.unbind();
    }
}
