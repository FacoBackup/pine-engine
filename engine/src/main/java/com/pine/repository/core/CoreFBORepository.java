package com.pine.repository.core;

import com.pine.Engine;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.repository.RuntimeRepository;
import com.pine.service.resource.ShaderService;
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
    public RuntimeRepository runtimeRepository;


    public FrameBufferObject auxBuffer;
    public FrameBufferObject postProcessingBuffer;
    public FrameBufferObject ssao;
    public FrameBufferObject ssaoBlurred;
    public FrameBufferObject gBuffer;
    public final List<FrameBufferObject> upscaleBloom = new ArrayList<>();
    public final List<FrameBufferObject> downscaleBloom = new ArrayList<>();
    public final List<FrameBufferObject> all = new ArrayList<>();
    public FrameBufferObject brdfFBO;

    public int gBufferAlbedoSampler;
    public int gBufferNormalSampler;
    public int gBufferRMAOSampler;
    public int gBufferMaterialSampler;
    public int gBufferDepthIndexSampler;
    public int gBufferIndirectSampler;
    public int auxSampler;
    public int postProcessingSampler;
    public int ssaoSampler;
    public int ssaoBlurredSampler;
    public int brdfSampler;

    @Override
    public void initialize() {
        final int displayW = runtimeRepository.getDisplayW();
        final int displayH = runtimeRepository.getDisplayH();

        final int halfResW = runtimeRepository.getDisplayW() / 2;
        final int halfResH = runtimeRepository.getDisplayH() / 2;

        brdfFBO = new FrameBufferObject(512, 512).addSampler(0, GL46.GL_RG16F, GL46.GL_RG, GL46.GL_FLOAT, false, false);
        brdfSampler = brdfFBO.getSamplers().getFirst();

        gBuffer = new FrameBufferObject(displayW, displayH)
                .depthTest()
                .addSampler(0, GL46.GL_RGBA8, GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE, false, false) // Albedo + Emissive flag
                .addSampler(1, GL46.GL_RGB16F, GL46.GL_RGB, GL46.GL_FLOAT, false, false) // Normal
                .addSampler(2, GL46.GL_RGB16F, GL46.GL_RGB, GL46.GL_FLOAT, false, false) // Roughness + Metallic + AO

                // X channel: 16 bits for anisotropicRotation + 16 bits for anisotropy
                // Y channel: 16 bits for clearCoat + 16 bits for sheen
                // Z channel: 16 bits for sheenTint + 15 bits for renderingMode + 1 bit for ssrEnabled
                .addSampler(3, GL46.GL_RGB32F, GL46.GL_RGB, GL46.GL_FLOAT, false, false)
                .addSampler(4, GL46.GL_RG16F, GL46.GL_RED, GL46.GL_FLOAT, false, false) // Log depth + render index
                .addSampler(5, GL46.GL_RGB16F, GL46.GL_RGB, GL46.GL_FLOAT, false, false);
        gBufferAlbedoSampler = gBuffer.getSamplers().get(0);
        gBufferNormalSampler = gBuffer.getSamplers().get(1);
        gBufferRMAOSampler = gBuffer.getSamplers().get(2);
        gBufferMaterialSampler = gBuffer.getSamplers().get(3);
        gBufferDepthIndexSampler = gBuffer.getSamplers().get(4);
        gBufferIndirectSampler = gBuffer.getSamplers().get(5);

        auxBuffer = new FrameBufferObject(displayW, displayH)
                .depthTest()
                .addSampler(0, GL46.GL_RGBA16F, GL46.GL_RGBA, GL46.GL_FLOAT, false, false);

        postProcessingBuffer = new FrameBufferObject(displayW, displayH).addSampler();

        ssao = new FrameBufferObject(halfResW, halfResH).addSampler(0, GL46.GL_R8, GL46.GL_RED, GL46.GL_UNSIGNED_BYTE, true, false);
        ssaoBlurred = new FrameBufferObject(halfResW, halfResH).addSampler(0, GL46.GL_R8, GL46.GL_RED, GL46.GL_UNSIGNED_BYTE, true, false);

        int Q = 7;
        int w = runtimeRepository.getDisplayW();
        int h = runtimeRepository.getDisplayH();
        for (int i = 0; i < Q; i++) {
            w /= 2;
            h /= 2;
            downscaleBloom.add(new FrameBufferObject(w, h).addSampler(0, GL46.GL_RGBA, GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE, false, false));
        }
        for (int i = 0; i < (Q / 2 - 1); i++) {
            w *= 4;
            h *= 4;
            upscaleBloom.add(new FrameBufferObject(w, h).addSampler(0, GL46.GL_RGBA, GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE, false, false));
        }

        ssaoBlurredSampler = ssaoBlurred.getSamplers().getFirst();
        ssaoSampler = ssao.getSamplers().getFirst();
        auxSampler = auxBuffer.getSamplers().getFirst();
        postProcessingSampler = postProcessingBuffer.getSamplers().getFirst();

        all.add(postProcessingBuffer);
        all.add(gBuffer);
        all.add(auxBuffer);
        all.add(ssao);
        all.add(ssaoBlurred);
        all.addAll(upscaleBloom);
        all.addAll(downscaleBloom);
    }

    @Override
    public void dispose() {
        auxBuffer.dispose();
        postProcessingBuffer.dispose();
        ssao.dispose();
        ssaoBlurred.dispose();
        gBuffer.dispose();
        upscaleBloom.forEach(FrameBufferObject::dispose);
        downscaleBloom.forEach(FrameBufferObject::dispose);
        brdfFBO.dispose();
    }
}
