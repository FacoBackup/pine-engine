package com.pine.service.system.impl;

import com.pine.PInject;
import com.pine.Engine;
import com.pine.repository.CoreResourceRepository;
import com.pine.repository.RenderingRepository;
import com.pine.repository.rendering.RuntimeDrawDTO;
import com.pine.service.resource.MeshService;
import com.pine.service.resource.ShaderService;
import com.pine.service.resource.fbo.FBO;
import com.pine.service.resource.primitives.GLSLType;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.system.AbstractSystem;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.List;

public class DemoRenderSystem extends AbstractSystem {
    @PInject
    public Engine engine;

    @PInject
    public RenderingRepository renderingRepository;

    @PInject
    public ShaderService shaderService;

    @PInject
    public MeshService meshService;

    @PInject
    public CoreResourceRepository coreResourceRepository;
    private UniformDTO translation;
    private UniformDTO rotation;
    private UniformDTO scale;

    private final FloatBuffer translationB = MemoryUtil.memAllocFloat(3);
    private final FloatBuffer rotationB = MemoryUtil.memAllocFloat(3);
    private final FloatBuffer scaleB = MemoryUtil.memAllocFloat(3);

    @Override
    protected FBO getTargetFBO() {
        return engine.getTargetFBO();
    }

    @Override
    public void onInitialize() {
        super.onInitialize();
        rotation = coreResourceRepository.demoShader.addUniformDeclaration("rotation", GLSLType.VEC_3);
        scale = coreResourceRepository.demoShader.addUniformDeclaration("scale", GLSLType.VEC_3);
        translation = coreResourceRepository.demoShader.addUniformDeclaration("translation", GLSLType.VEC_3);
    }

    @Override
    protected void renderInternal() {
        shaderService.bind(coreResourceRepository.demoShader);
        List<RuntimeDrawDTO> requests = renderingRepository.requests;
        for (int i = 0; i < requests.size(); i++) {
            var request = requests.get(i);

            request.transformation.translation.get(translationB);
            request.transformation.rotation.get(rotationB);
            request.transformation.scale.get(scaleB);

            shaderService.bindUniform(translation, translationB);
            shaderService.bindUniform(rotation, rotationB);
            shaderService.bindUniform(scale, scaleB);

            meshService.bind(request.primitive, request.runtimeData);
        }
        shaderService.unbind();
    }
}
