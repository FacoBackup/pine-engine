package com.pine.repository.core;

import com.pine.Engine;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.repository.RuntimeRepository;
import com.pine.service.resource.ShaderService;
import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.resource.shader.GLSLType;
import com.pine.service.resource.ssbo.SSBOCreationData;
import com.pine.service.resource.ssbo.ShaderStorageBufferObject;
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

import static com.pine.Engine.MAX_LIGHTS;

@PBean
public class CoreBufferRepository implements CoreRepository {
    public static final int[] ZERO = new int[]{0};
    public static final int MAX_INSTANCING = 500_000;
    public static final int MAX_INFO_PER_LIGHT = 16;
    private static final int LIGHT_BUFFER_SIZE = MAX_LIGHTS * MAX_INFO_PER_LIGHT;

    @PInject
    public Engine engine;
    @PInject
    public ShaderService shaderService;
    @PInject
    public RuntimeRepository runtimeRepository;

    public final FloatBuffer lightSSBOState = MemoryUtil.memAllocFloat(LIGHT_BUFFER_SIZE);
    public ShaderStorageBufferObject lightMetadataSSBO;

    public FrameBufferObject gBufferTarget;
    public FrameBufferObject postProcessingBuffer;
    public FrameBufferObject ssao;
    public FrameBufferObject ssaoBlurred;
    public FrameBufferObject gBuffer;
    public FrameBufferObject auxBufferQuaterRes;
    public FrameBufferObject sceneDepthCopy;
    public final List<FrameBufferObject> upscaleBloom = new ArrayList<>();
    public final List<FrameBufferObject> downscaleBloom = new ArrayList<>();
    public final List<FrameBufferObject> all = new ArrayList<>();
    public FrameBufferObject brdfFBO;
    public FrameBufferObject compositingBuffer;
    public FrameBufferObject noiseBuffer;

    public TextureResourceRef cloudNoiseTexture;
    public TextureResourceRef cloudShapeTexture;

    public int auxBufferQuaterResSampler;
    public int noiseSampler;
    public int sceneDepthCopySampler;
    public int gBufferAlbedoSampler;
    public int gBufferNormalSampler;
    public int gBufferRMAOSampler;
    public int gBufferMaterialSampler;
    public int gBufferDepthIndexSampler;
    public int gBufferIndirectSampler;
    public int gBufferTargetSampler;
    public int postProcessingSampler;
    public int ssaoSampler;
    public int ssaoBlurredSampler;
    public int brdfSampler;
    public int compositingSampler;

    public UniformBufferObject globalDataUBO;
    public final FloatBuffer globalDataBuffer = MemoryUtil.memAllocFloat(95);

    @Override
    public void initialize() {
        cloudShapeTexture = TextureUtil.create3DTexture(128, 128, 128, GL46.GL_RGBA16F, GL46.GL_RGBA, GL46.GL_HALF_FLOAT);
        cloudNoiseTexture = TextureUtil.create3DTexture(32, 32, 32, GL46.GL_RGBA16F, GL46.GL_RGBA, GL46.GL_HALF_FLOAT);

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

        noiseBuffer = new FrameBufferObject(256, 256)
                .addSampler(0, GL46.GL_RG16F, GL46.GL_RG, GL46.GL_FLOAT, false, true);
        noiseSampler = noiseBuffer.getSamplers().getFirst();

        brdfFBO = new FrameBufferObject(512, 512).addSampler(0, GL46.GL_RG16F, GL46.GL_RG, GL46.GL_FLOAT, false, false);
        brdfSampler = brdfFBO.getSamplers().getFirst();

        createGBuffer(displayW, displayH);
        createMainBuffers(displayW, displayH);

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

        all.add(postProcessingBuffer);
        all.add(gBuffer);
        all.add(gBufferTarget);
        all.add(ssao);
        all.add(ssaoBlurred);
        all.addAll(upscaleBloom);
        all.addAll(downscaleBloom);
        all.add(auxBufferQuaterRes);
        all.add(compositingBuffer);

        lightMetadataSSBO = new ShaderStorageBufferObject(new SSBOCreationData(
                11,
                (long) LIGHT_BUFFER_SIZE * GLSLType.FLOAT.getSize()
        ));
    }

    private void createGBuffer(int displayW, int displayH) {
        gBuffer = new FrameBufferObject(displayW, displayH)
                .depthTest()
                .addSampler(0, GL46.GL_RGBA8, GL46.GL_RGBA, GL46.GL_UNSIGNED_BYTE, false, false) // Albedo + Emissive flag
                .addSampler(1, GL46.GL_RGB16F, GL46.GL_RGB, GL46.GL_FLOAT, false, false) // Normal
                .addSampler(2, GL46.GL_RGB16F, GL46.GL_RGB, GL46.GL_FLOAT, false, false) // Roughness + Metallic + AO

                // X channel: 16 bits for anisotropicRotation + 16 bits for anisotropy
                // Y channel: 16 bits for clearCoat + 16 bits for sheen
                // Z channel: 16 bits for sheenTint + 15 bits for renderingMode + 1 bit for ssrEnabled
                .addSampler(3, GL46.GL_RGB32F, GL46.GL_RGB, GL46.GL_FLOAT, false, false)
                .addSampler(4, GL46.GL_RGBA32F, GL46.GL_RED, GL46.GL_FLOAT, false, false) // Log depth + render index + UV
                .addSampler(5, GL46.GL_RGB16F, GL46.GL_RGB, GL46.GL_FLOAT, false, false);
        gBufferAlbedoSampler = gBuffer.getSamplers().get(0);
        gBufferNormalSampler = gBuffer.getSamplers().get(1);
        gBufferRMAOSampler = gBuffer.getSamplers().get(2);
        gBufferMaterialSampler = gBuffer.getSamplers().get(3);
        gBufferDepthIndexSampler = gBuffer.getSamplers().get(4);
        gBufferIndirectSampler = gBuffer.getSamplers().get(5);
    }

    private void createMainBuffers(int displayW, int displayH) {
        gBufferTarget = new FrameBufferObject(displayW, displayH)
                .depthTest()
                .addSampler(0, GL46.GL_RGBA16F, GL46.GL_RGBA, GL46.GL_FLOAT, false, false);
        gBufferTargetSampler = gBufferTarget.getSamplers().getFirst();

        compositingBuffer = new FrameBufferObject(displayW, displayH)
                .addSampler(0, GL46.GL_RGB16F, GL46.GL_RGB, GL46.GL_FLOAT, false, false);
        compositingSampler = compositingBuffer.getSamplers().getFirst();

        auxBufferQuaterRes = new FrameBufferObject(displayW /4, displayH /4)
                .addSampler(0, GL46.GL_RGB16F, GL46.GL_RGB, GL46.GL_FLOAT, true, false);
        auxBufferQuaterResSampler = auxBufferQuaterRes.getMainSampler();

        postProcessingBuffer = new FrameBufferObject(displayW, displayH)
                .addSampler(0, GL46.GL_RGB, GL46.GL_RGB, GL46.GL_UNSIGNED_BYTE, false, false);
        postProcessingSampler = postProcessingBuffer.getSamplers().getFirst();

        sceneDepthCopy = new FrameBufferObject(displayW, displayH)
                .addSampler(0, GL46.GL_RG32F, GL46.GL_RG, GL46.GL_FLOAT, false, false);
        sceneDepthCopySampler = sceneDepthCopy.getSamplers().getFirst();
    }

    @Override
    public void dispose() {
        lightMetadataSSBO.dispose();
        globalDataUBO.dispose();
        auxBufferQuaterRes.dispose();
        gBufferTarget.dispose();
        postProcessingBuffer.dispose();
        ssao.dispose();
        ssaoBlurred.dispose();
        gBuffer.dispose();
        upscaleBloom.forEach(FrameBufferObject::dispose);
        downscaleBloom.forEach(FrameBufferObject::dispose);
        brdfFBO.dispose();
    }
}
