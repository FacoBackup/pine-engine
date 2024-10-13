package com.pine.repository.core;

import com.pine.Engine;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.service.resource.ResourceService;
import com.pine.service.resource.shader.GLSLType;
import com.pine.service.resource.ubo.UBOCreationData;
import com.pine.service.resource.ubo.UBOData;
import com.pine.service.resource.ubo.UniformBufferObject;
import com.pine.type.UBODeclaration;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

@PBean
public class CoreUBORepository implements CoreRepository {
    @PInject
    public Engine engine;
    @PInject
    public ResourceService resources;


    public UniformBufferObject cameraViewUBO;

    public final FloatBuffer cameraViewUBOState = MemoryUtil.memAllocFloat(87);
    public final FloatBuffer lensPostProcessingUBOState = MemoryUtil.memAllocFloat(1);
    public final FloatBuffer ssaoUBOState = MemoryUtil.memAllocFloat(1);

    @Override
    public void initialize() {
        cameraViewUBO = (UniformBufferObject) resources.addResource(new UBOCreationData(
                UBODeclaration.CAMERA_VIEW.getBlockName(),
                new UBOData("viewProjection", GLSLType.MAT_4),
                new UBOData("viewMatrix", GLSLType.MAT_4),
                new UBOData("invViewMatrix", GLSLType.MAT_4),
                new UBOData("placement", GLSLType.VEC_4),
                new UBOData("projectionMatrix", GLSLType.MAT_4),
                new UBOData("invProjectionMatrix", GLSLType.MAT_4),
                new UBOData("bufferResolution", GLSLType.VEC_2),
                new UBOData("logDepthFC", GLSLType.FLOAT)

//                new UBOData("textureSizeXDOF", GLSLType.FLOAT),
//                new UBOData("textureSizeYDOF", GLSLType.FLOAT),
//                new UBOData("distortionIntensity", GLSLType.FLOAT),
//                new UBOData("chromaticAberrationIntensity", GLSLType.FLOAT),
//                new UBOData("distortionEnabled", GLSLType.BOOL),
//                new UBOData("chromaticAberrationEnabled", GLSLType.BOOL),
//                new UBOData("bloomEnabled", GLSLType.BOOL),
//                new UBOData("focusDistanceDOF", GLSLType.FLOAT),
//                new UBOData("apertureDOF", GLSLType.FLOAT),
//                new UBOData("focalLengthDOF", GLSLType.FLOAT),
//                new UBOData("samplesDOF", GLSLType.FLOAT),
//                new UBOData("vignetteEnabled", GLSLType.BOOL),
//                new UBOData("vignetteStrength", GLSLType.FLOAT),
//                new UBOData("gamma", GLSLType.FLOAT),
//                new UBOData("exposure", GLSLType.FLOAT)


        ).staticResource());

    }
}
