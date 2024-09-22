package com.pine.service.system.impl;

import com.pine.Engine;
import com.pine.PInject;
import com.pine.repository.CoreResourceRepository;
import com.pine.repository.RenderingRepository;
import com.pine.repository.rendering.PrimitiveRenderRequest;
import com.pine.service.resource.MeshService;
import com.pine.service.resource.SSBOService;
import com.pine.service.resource.ShaderService;
import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.resource.primitives.GLSLType;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.system.AbstractSystem;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;
import java.util.List;

public class DemoRenderSystem extends AbstractSystem {
    @PInject
    public Engine engine;

    @PInject
    public RenderingRepository renderingRepository;

    @PInject
    public ShaderService shaderService;

    @PInject
    public SSBOService ssboService;

    @PInject
    public MeshService meshService;

    @PInject
    public CoreResourceRepository coreResourceRepository;

    private UniformDTO transformationIndex;
    private final IntBuffer transformationIndexBuffer = MemoryUtil.memAllocInt(1);

    @Override
    protected FrameBufferObject getTargetFBO() {
        return engine.getTargetFBO();
    }

    @Override
    public void onInitialize() {
        super.onInitialize();
        transformationIndex = coreResourceRepository.demoShader.addUniformDeclaration("transformationIndex", GLSLType.INT);
    }

    @Override
    protected void renderInternal() {
        ssboService.bind(coreResourceRepository.transformationSSBO);
        ssboService.bind(coreResourceRepository.modelSSBO);
        shaderService.bind(coreResourceRepository.demoShader);

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
