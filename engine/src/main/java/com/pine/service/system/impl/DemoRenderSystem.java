package com.pine.service.system.impl;

import com.pine.Engine;
import com.pine.PInject;
import com.pine.repository.CoreResourceRepository;
import com.pine.repository.RenderingRepository;
import com.pine.repository.rendering.PrimitiveRenderRequest;
import com.pine.service.resource.MeshService;
import com.pine.service.resource.SSBOService;
import com.pine.service.resource.ShaderService;
import com.pine.service.resource.fbo.FBO;
import com.pine.service.resource.primitives.GLSLType;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.system.AbstractSystem;

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

    @Override
    protected FBO getTargetFBO() {
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
        shaderService.bind(coreResourceRepository.demoShader);
        List<PrimitiveRenderRequest> requests = renderingRepository.requests;
        int instancedOffset = 0;
        for (int i = 0; i < requests.size(); i++) {
            var request = requests.get(i);
            shaderService.bindUniform(transformationIndex, (i + instancedOffset) * 3);
            meshService.bind(request.primitive, request.runtimeData);
            meshService.unbind();
            instancedOffset += request.transformations.size();
        }
        shaderService.unbind();
        ssboService.unbind();
    }
}
