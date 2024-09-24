package com.pine.repository;

import com.pine.Engine;
import com.pine.PBean;
import com.pine.PInject;
import com.pine.service.resource.MeshService;
import com.pine.service.resource.ResourceService;
import com.pine.service.resource.ShaderService;
import com.pine.service.resource.fbo.FBOCreationData;
import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.ShaderCreationData;
import org.lwjgl.opengl.GL46;

import java.util.ArrayList;
import java.util.List;

@PBean
public class CoreFBORepository implements CoreRepository {
    @PInject
    public Engine engine;
    @PInject
    public CorePrimitiveRepository primitiveRepository;
    @PInject
    public ShaderService shaderService;
    @PInject
    public MeshService meshService;
    @PInject
    public ResourceService resources;
    @PInject
    public EngineSettingsRepository configuration;


    public FrameBufferObject sceneDepth;
    public int sceneDepthSampler;
    public FrameBufferObject tempColorWithDepth;
    public int tempColorWithDepthSampler;
    public FrameBufferObject ssgi;
    public int ssgiSampler;
    public FrameBufferObject ssgiFallback;
    public int ssgiFallbackSampler;
    public FrameBufferObject ssao;
    public int ssaoSampler;
    public FrameBufferObject ssaoBlurred;
    public int ssaoBlurredSampler;
    public FrameBufferObject shadows;
    public int shadowsSampler;
    public int noiseSampler; // TODO
    public int brdfSampler;
    public final List<FrameBufferObject> upscaleBloom = new ArrayList<>();
    public final List<FrameBufferObject> downscaleBloom = new ArrayList<>();

    @Override
    public void initialize() {

        final int halfResW = engine.getDisplayW() / 2;
        final int halfResH = engine.getDisplayH() / 2;

        sceneDepth = (FrameBufferObject) resources.addResource(new FBOCreationData(true, true));
        tempColorWithDepth = (FrameBufferObject) resources.addResource(new FBOCreationData(false, true).addSampler().staticResource());

        ssgi = (FrameBufferObject) resources.addResource(new FBOCreationData(halfResW, halfResH).addSampler(0, GL46.GL_RGBA, GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE, true, false).staticResource());
        ssgiFallback = (FrameBufferObject) resources.addResource(new FBOCreationData(halfResW, halfResH).addSampler(0, GL46.GL_RGBA, GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE, false, false).staticResource());

        ssao = (FrameBufferObject) resources.addResource(new FBOCreationData(halfResW, halfResH).addSampler(0, GL46.GL_R8, GL46.GL_RED, GL46.GL_UNSIGNED_BYTE, true, false).staticResource());
        ssaoBlurred = (FrameBufferObject) resources.addResource(new FBOCreationData(halfResW, halfResH).addSampler(0, GL46.GL_R8, GL46.GL_RED, GL46.GL_UNSIGNED_BYTE, true, false).staticResource());

        int Q = 7;
        int w = engine.getDisplayW();
        int h = engine.getDisplayH();
        for (int i = 0; i < Q; i++) {
            w /= 2;
            h /= 2;
            downscaleBloom.add((FrameBufferObject) resources.addResource(new FBOCreationData(w, h).addSampler(0, GL46.GL_RGBA, GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE, false, false).staticResource()));
        }
        for (int i = 0; i < (Q / 2 - 1); i++) {
            w *= 4;
            h *= 4;
            upscaleBloom.add((FrameBufferObject) resources.addResource(new FBOCreationData(w, h).addSampler(0, GL46.GL_RGBA, GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE, false, false).staticResource()));
        }

        ssaoBlurredSampler = ssaoBlurred.getSamplers().getFirst();
        ssaoSampler = ssao.getSamplers().getFirst();
        ssgiSampler = ssgi.getSamplers().getFirst();
        ssgiFallbackSampler = ssgiFallback.getSamplers().getFirst();
        sceneDepthSampler = sceneDepth.getDepthSampler();
        tempColorWithDepthSampler = tempColorWithDepth.getSamplers().getFirst();

        shadows = (FrameBufferObject) resources.addResource(new FBOCreationData(configuration.shadowMapResolution, configuration.shadowMapResolution).setDepthTexture(true).staticResource());
        shadowsSampler = shadows.getDepthSampler();


        var brdfFBO = (FrameBufferObject) resources.addResource(new FBOCreationData(512, 512).addSampler(0, GL46.GL_RG32F, GL46.GL_RG, GL46.GL_FLOAT, false, false).staticResource());
        var brdfShader = (Shader) resources.addResource(new ShaderCreationData(ShaderCreationData.LOCAL_SHADER + "QUAD.vert", ShaderCreationData.LOCAL_SHADER + "BRDF_GEN.frag"));
        shaderService.bind(brdfShader);
        brdfFBO.startMapping(true);
        meshService.bind(primitiveRepository.quadMesh);
        brdfFBO.stop();
        meshService.unbind();
        shaderService.unbind();
        brdfSampler = brdfFBO.getSamplers().getFirst();
    }
}
