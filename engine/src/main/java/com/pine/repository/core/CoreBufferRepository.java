package com.pine.repository.core;

import com.pine.Engine;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.repository.RuntimeRepository;
import com.pine.service.resource.ShaderService;
import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.resource.shader.GLSLType;
import com.pine.service.resource.ubo.UBOCreationData;
import com.pine.service.resource.ubo.UBOData;
import com.pine.service.resource.ubo.UniformBufferObject;
import com.pine.service.streaming.ref.TextureResourceRef;
import com.pine.service.voxelization.util.TextureUtil;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

@PBean
public class CoreBufferRepository implements CoreRepository {
    public static final int[] ZERO = new int[]{0};
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

    public int atomicCounterBuffer;
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

    public TextureResourceRef brdfSampler;
    public TextureResourceRef curlNoiseSampler;
    public TextureResourceRef blueNoiseSampler;


    public UniformBufferObject globalDataUBO;
    public final FloatBuffer globalDataBuffer = MemoryUtil.memAllocFloat(95);


    @Override
    public void initialize() {
        atomicCounterBuffer = GL46.glGenBuffers();
        GL46.glBindBuffer(GL46.GL_ATOMIC_COUNTER_BUFFER, atomicCounterBuffer);
        GL46.glBufferData(GL46.GL_ATOMIC_COUNTER_BUFFER, Integer.BYTES, GL46.GL_DYNAMIC_DRAW);
        GL46.glBindBufferBase(GL46.GL_ATOMIC_COUNTER_BUFFER, 0, atomicCounterBuffer);

        globalDataUBO = new UniformBufferObject(new UBOCreationData(
                "GlobalData",
                new UBOData("viewProjection", GLSLType.MAT_4), // Offset: 0
                new UBOData("viewMatrix", GLSLType.MAT_4), // Offset: 16
                new UBOData("invViewMatrix", GLSLType.MAT_4), // Offset: 32
                new UBOData("cameraWorldPosition", GLSLType.VEC_4), // Offset: 48
                new UBOData("projectionMatrix", GLSLType.MAT_4), // Offset: 52
                new UBOData("invProjectionMatrix", GLSLType.MAT_4), // Offset: 68
                new UBOData("bufferResolution", GLSLType.VEC_2), // Offset: 84, 85
                new UBOData("logDepthFC", GLSLType.FLOAT), // Offset: 86
                new UBOData("timeOfDay", GLSLType.FLOAT), // Offset: 88

                new UBOData("sunLightDirection", GLSLType.VEC_3), // Offset: 89, 90, 91
                new UBOData("sunLightColor", GLSLType.VEC_3) // Offset: 92, 93, 94
        ));

        createFrameBuffers();
    }

    private void createFrameBuffers() {
        final int displayW = runtimeRepository.getDisplayW();
        final int displayH = runtimeRepository.getDisplayH();

        final int halfResW = runtimeRepository.getDisplayW() / 2;
        final int halfResH = runtimeRepository.getDisplayH() / 2;

        curlNoiseSampler = TextureUtil.loadTextureFromResource("/textures/curlNoise.png");
        brdfSampler = TextureUtil.loadTextureFromResource("/textures/brdf.png");
        blueNoiseSampler = TextureUtil.loadTextureFromResource("/textures/blueNoise.png");

        gBuffer = new FrameBufferObject(displayW, displayH)
                .depthTest()
                .addSampler(0, GL46.GL_RGBA8, GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE, false, false) // Albedo + Emissive flag
                .addSampler(1, GL46.GL_RGB16F, GL46.GL_RGB, GL46.GL_FLOAT, false, false) // Normal
                .addSampler(2, GL46.GL_RGB16F, GL46.GL_RGB, GL46.GL_FLOAT, false, false) // Roughness + Metallic + AO

                // X channel: 16 bits for anisotropicRotation + 16 bits for anisotropy
                // Y channel: 16 bits for clearCoat + 16 bits for sheen
                // Z channel: 16 bits for sheenTint + 15 bits for renderingMode + 1 bit for ssrEnabled
                .addSampler(3, GL46.GL_RGB32F, GL46.GL_RGB, GL46.GL_FLOAT, false, false)
                .addSampler(4, GL46.GL_RGBA16F, GL46.GL_RED, GL46.GL_FLOAT, false, false) // Log depth + render index + UV
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
        brdfSampler.dispose();
        curlNoiseSampler.dispose();
        blueNoiseSampler.dispose();

        globalDataUBO.dispose();
        auxBuffer.dispose();
        postProcessingBuffer.dispose();
        ssao.dispose();
        ssaoBlurred.dispose();
        gBuffer.dispose();
        upscaleBloom.forEach(FrameBufferObject::dispose);
        downscaleBloom.forEach(FrameBufferObject::dispose);
    }
}
