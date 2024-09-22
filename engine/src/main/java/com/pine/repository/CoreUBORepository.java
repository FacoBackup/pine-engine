package com.pine.repository;

import com.pine.Engine;
import com.pine.PBean;
import com.pine.PInject;
import com.pine.service.resource.ResourceService;
import com.pine.service.resource.primitives.GLSLType;
import com.pine.service.resource.ubo.UBOCreationData;
import com.pine.service.resource.ubo.UBOData;
import com.pine.service.resource.ubo.UniformBufferObject;
import com.pine.type.BlockPoint;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

@PBean
public class CoreUBORepository implements CoreRepository {
    @PInject
    public Engine engine;
    @PInject
    public ResourceService resources;


    public UniformBufferObject cameraViewUBO;
    public UniformBufferObject frameCompositionUBO;
    public UniformBufferObject lensPostProcessingUBO;
    public UniformBufferObject ssaoUBO;
    public UniformBufferObject uberUBO;
    public UniformBufferObject lightsUBO;
    public UniformBufferObject cameraProjectionUBO;

    public final FloatBuffer cameraViewUBOState = MemoryUtil.memAllocFloat(52);
    public final FloatBuffer cameraProjectionUBOState = MemoryUtil.memAllocFloat(35);
    public final FloatBuffer frameCompositionUBOState = MemoryUtil.memAllocFloat(1);
    public final FloatBuffer lensPostProcessingUBOState = MemoryUtil.memAllocFloat(1);
    public final FloatBuffer ssaoUBOState = MemoryUtil.memAllocFloat(1);
    public final FloatBuffer uberUBOState = MemoryUtil.memAllocFloat(1);

    @Override
    public void initialize() {
        cameraViewUBO = (UniformBufferObject) resources.addResource(new UBOCreationData(
                BlockPoint.CAMERA_VIEW.getBlockName(),
                new UBOData("viewProjection", GLSLType.MAT_4),
                new UBOData("viewMatrix", GLSLType.MAT_4),
                new UBOData("invViewMatrix", GLSLType.MAT_4),
                new UBOData("placement", GLSLType.VEC_4)).staticResource());

        cameraProjectionUBO = (UniformBufferObject) resources.addResource(new UBOCreationData(
                BlockPoint.CAMERA_PROJECTION.getBlockName(),
                new UBOData("projectionMatrix", GLSLType.MAT_4),
                new UBOData("invProjectionMatrix", GLSLType.MAT_4),
                new UBOData("bufferResolution", GLSLType.VEC_2),
                new UBOData("logDepthFC", GLSLType.FLOAT),
                new UBOData("logC", GLSLType.FLOAT)).staticResource());

        frameCompositionUBO = (UniformBufferObject) resources.addResource(new UBOCreationData(
                BlockPoint.FRAME_COMPOSITION.getBlockName(),
                new UBOData("inverseFilterTextureSize", GLSLType.VEC_2),
                new UBOData("useFXAA", GLSLType.BOOL),
                new UBOData("filmGrainEnabled", GLSLType.BOOL),
                new UBOData("FXAASpanMax", GLSLType.FLOAT),
                new UBOData("FXAAReduceMin", GLSLType.FLOAT),
                new UBOData("FXAAReduceMul", GLSLType.FLOAT),
                new UBOData("filmGrainStrength", GLSLType.FLOAT)).staticResource());

        lensPostProcessingUBO = (UniformBufferObject) resources.addResource(new UBOCreationData(
                BlockPoint.LENS_PP.getBlockName(),
                new UBOData("textureSizeXDOF", GLSLType.FLOAT),
                new UBOData("textureSizeYDOF", GLSLType.FLOAT),
                new UBOData("distortionIntensity", GLSLType.FLOAT),
                new UBOData("chromaticAberrationIntensity", GLSLType.FLOAT),
                new UBOData("distortionEnabled", GLSLType.BOOL),
                new UBOData("chromaticAberrationEnabled", GLSLType.BOOL),
                new UBOData("bloomEnabled", GLSLType.BOOL),
                new UBOData("focusDistanceDOF", GLSLType.FLOAT),
                new UBOData("apertureDOF", GLSLType.FLOAT),
                new UBOData("focalLengthDOF", GLSLType.FLOAT),
                new UBOData("samplesDOF", GLSLType.FLOAT),
                new UBOData("vignetteEnabled", GLSLType.BOOL),
                new UBOData("vignetteStrength", GLSLType.FLOAT),
                new UBOData("gamma", GLSLType.FLOAT),
                new UBOData("exposure", GLSLType.FLOAT)
        ).staticResource());

        ssaoUBO = (UniformBufferObject) resources.addResource(new UBOCreationData(
                BlockPoint.SSAO.getBlockName(),
                new UBOData("settings", GLSLType.VEC_4),
                new UBOData("samples", GLSLType.VEC_4, 64),
                new UBOData("noiseScale", GLSLType.VEC_2)
        ).staticResource());

        uberUBO = (UniformBufferObject) resources.addResource(new UBOCreationData(
                BlockPoint.UBER.getBlockName(),
                new UBOData("shadowMapsQuantity", GLSLType.FLOAT),
                new UBOData("shadowMapResolution", GLSLType.FLOAT),
                new UBOData("lightQuantity", GLSLType.INT),
                new UBOData("SSRFalloff", GLSLType.FLOAT),
                new UBOData("stepSizeSSR", GLSLType.FLOAT),
                new UBOData("maxSSSDistance", GLSLType.FLOAT),
                new UBOData("SSSDepthThickness", GLSLType.FLOAT),
                new UBOData("SSSEdgeAttenuation", GLSLType.FLOAT),
                new UBOData("skylightSamples", GLSLType.FLOAT),
                new UBOData("SSSDepthDelta", GLSLType.FLOAT),
                new UBOData("SSAOFalloff", GLSLType.FLOAT),
                new UBOData("maxStepsSSR", GLSLType.INT),
                new UBOData("maxStepsSSS", GLSLType.INT),
                new UBOData("hasSkylight", GLSLType.BOOL),
                new UBOData("hasAmbientOcclusion", GLSLType.BOOL)
        ).staticResource());
    }
}
