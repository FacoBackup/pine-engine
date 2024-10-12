package com.pine.repository.core;

import com.pine.Engine;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.repository.EngineSettingsRepository;
import com.pine.repository.RuntimeRepository;
import com.pine.service.resource.ResourceService;
import com.pine.service.resource.ShaderService;
import com.pine.service.resource.fbo.FBOCreationData;
import com.pine.service.resource.fbo.FrameBufferObject;
import org.lwjgl.opengl.GL46;

import java.util.ArrayList;
import java.util.List;

@PBean
public class CoreFBORepository implements CoreRepository {
    @PInject
    public Engine engine;
    @PInject
    public ShaderService shaderService;
    @PInject
    public ResourceService resources;
    @PInject
    public EngineSettingsRepository configuration;
    @PInject
    public RuntimeRepository runtimeRepository;


    public FrameBufferObject gBuffer;
    public int gBufferAlbedoSampler;
    public int gBufferNormalSampler;
    public int gBufferRMAOSampler;
    public int gBufferMaterialSampler;
    public int gBufferDepthSampler;
    public FrameBufferObject auxBuffer;
    public int auxSampler;
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
    public int brdfSampler;
    public final List<FrameBufferObject> upscaleBloom = new ArrayList<>();
    public final List<FrameBufferObject> downscaleBloom = new ArrayList<>();
    public final List<FrameBufferObject> all = new ArrayList<>();
    public FrameBufferObject brdfFBO;

    @Override
    public void initialize() {

        final int halfResW = runtimeRepository.getDisplayW() / 2;
        final int halfResH = runtimeRepository.getDisplayH() / 2;

        brdfFBO = (FrameBufferObject) resources.addResource(new FBOCreationData(512, 512).addSampler(0, GL46.GL_RG16F, GL46.GL_RG, GL46.GL_FLOAT, false, false).staticResource());
        brdfSampler = brdfFBO.getSamplers().getFirst();

        gBuffer = (FrameBufferObject) resources.addResource(new FBOCreationData(false, true)
                .addSampler(0, GL46.GL_RGBA16F, GL46.GL_RGBA, GL46.GL_FLOAT, false, false) // Albedo + Emissive flag
                .addSampler(1, GL46.GL_RGB16F, GL46.GL_RGB, GL46.GL_FLOAT, false, false) // Normal
                .addSampler(2, GL46.GL_RGB16F, GL46.GL_RGB, GL46.GL_FLOAT, false, false) // Roughness + Metallic + AO

                // X channel: 16 bits for anisotropicRotation + 16 bits for anisotropy
                // Y channel: 16 bits for clearCoat + 16 bits for sheen
                // Z channel: 16 bits for sheenTint + 15 bits for renderingMode + 1 bit for ssrEnabled
                .addSampler(3, GL46.GL_RGB32F, GL46.GL_RGB, GL46.GL_FLOAT, false, false)
                .addSampler(4, GL46.GL_R16F, GL46.GL_RED, GL46.GL_FLOAT, false, false) // Log depth
        );
        gBufferAlbedoSampler = gBuffer.getSamplers().get(0);
        gBufferNormalSampler = gBuffer.getSamplers().get(1);
        gBufferRMAOSampler = gBuffer.getSamplers().get(2);
        gBufferMaterialSampler = gBuffer.getSamplers().get(3);
        gBufferDepthSampler = gBuffer.getSamplers().get(4);

        auxBuffer = (FrameBufferObject) resources.addResource(new FBOCreationData(false, true)
                .addSampler(0, GL46.GL_RGBA16F, GL46.GL_RGBA, GL46.GL_FLOAT, true, false)
                .staticResource());

        ssgi = (FrameBufferObject) resources.addResource(new FBOCreationData(halfResW, halfResH).addSampler(0, GL46.GL_RGBA, GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE, true, false).staticResource());
        ssgiFallback = (FrameBufferObject) resources.addResource(new FBOCreationData(halfResW, halfResH).addSampler(0, GL46.GL_RGBA, GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE, false, false).staticResource());

        ssao = (FrameBufferObject) resources.addResource(new FBOCreationData(halfResW, halfResH).addSampler(0, GL46.GL_R8, GL46.GL_RED, GL46.GL_UNSIGNED_BYTE, true, false).staticResource());
        ssaoBlurred = (FrameBufferObject) resources.addResource(new FBOCreationData(halfResW, halfResH).addSampler(0, GL46.GL_R8, GL46.GL_RED, GL46.GL_UNSIGNED_BYTE, true, false).staticResource());

        int Q = 7;
        int w = runtimeRepository.getDisplayW();
        int h = runtimeRepository.getDisplayH();
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
        auxSampler = auxBuffer.getSamplers().getFirst();

        shadows = (FrameBufferObject) resources.addResource(new FBOCreationData(configuration.shadowMapResolution, configuration.shadowMapResolution).setDepthTexture(true).staticResource());
        shadowsSampler = shadows.getDepthSampler();

        all.add(gBuffer);
        all.add(auxBuffer);
        all.add(ssgi);
        all.add(ssgiFallback);
        all.add(ssao);
        all.add(ssaoBlurred);
        all.add(shadows);
        all.addAll(upscaleBloom);
        all.addAll(downscaleBloom);
    }
}
