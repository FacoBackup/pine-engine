package com.pine.service.system.impl;

import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.streaming.ref.TextureResourceRef;
import com.pine.service.voxelization.util.TextureUtil;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL46;

import static com.pine.service.resource.ShaderService.COMPUTE_RUNTIME_DATA;

public class VolumetricCloudsPass extends AbstractQuadPassPass {
    private static final int NUM_THREADS = 8;
    private boolean hasSamplersComputed = false;
    private TextureResourceRef cloudNoiseTexture;
    private TextureResourceRef cloudShapeTexture;

    private UniformDTO densityMultiplier;
    private UniformDTO densityOffset;
    private UniformDTO scale;
    private UniformDTO detailNoiseScale;
    private UniformDTO detailNoiseWeight;
    private UniformDTO detailWeights;
    private UniformDTO shapeNoiseWeights;
    private UniformDTO phaseParams;
    private UniformDTO numStepsLight;
    private UniformDTO rayOffsetStrength;
    private UniformDTO boundsMin;
    private UniformDTO boundsMax;
    private UniformDTO shapeOffset;
    private UniformDTO detailOffset;
    private UniformDTO lightAbsorptionTowardSun;
    private UniformDTO lightAbsorptionThroughCloud;
    private UniformDTO darknessThreshold;
    private UniformDTO lightColor;
    private UniformDTO baseSpeed;
    private UniformDTO detailSpeed;

    @Override
    public void onInitialize() {
        densityMultiplier = addUniformDeclaration("densityMultiplier");
        densityOffset = addUniformDeclaration("densityOffset");
        scale = addUniformDeclaration("scale");
        detailNoiseScale = addUniformDeclaration("detailNoiseScale");
        detailNoiseWeight = addUniformDeclaration("detailNoiseWeight");
        detailWeights = addUniformDeclaration("detailWeights");
        shapeNoiseWeights = addUniformDeclaration("shapeNoiseWeights");
        phaseParams = addUniformDeclaration("phaseParams");
        numStepsLight = addUniformDeclaration("numStepsLight");
        rayOffsetStrength = addUniformDeclaration("rayOffsetStrength");
        boundsMin = addUniformDeclaration("boundsMin");
        boundsMax = addUniformDeclaration("boundsMax");
        shapeOffset = addUniformDeclaration("shapeOffset");
        detailOffset = addUniformDeclaration("detailOffset");
        lightAbsorptionTowardSun = addUniformDeclaration("lightAbsorptionTowardSun");
        lightAbsorptionThroughCloud = addUniformDeclaration("lightAbsorptionThroughCloud");
        darknessThreshold = addUniformDeclaration("darknessThreshold");
        baseSpeed = addUniformDeclaration("baseSpeed");
        detailSpeed = addUniformDeclaration("detailSpeed");
    }

    @Override
    protected FrameBufferObject getTargetFBO() {
        return bufferRepository.auxBuffer;
    }

    @Override
    protected boolean isRenderable() {
        return atmosphere.clouds;
    }

    @Override
    protected void onBeforeRender() {
        if (!hasSamplersComputed) {
            cloudShapeTexture = TextureUtil.create3DTexture(128, 128, 128, GL46.GL_RGBA16F, GL46.GL_RGBA, GL46.GL_HALF_FLOAT);
            cloudNoiseTexture = TextureUtil.create3DTexture(32, 32, 32, GL46.GL_RGBA16F, GL46.GL_RGBA, GL46.GL_HALF_FLOAT);

            dispatch(cloudNoiseTexture, shaderRepository.cloudDetailCompute);
            dispatch(cloudShapeTexture, shaderRepository.cloudShapeCompute);

            hasSamplersComputed = true;
        }
    }

    private void dispatch(TextureResourceRef texture, Shader shader) {
        shaderService.bind(shader);

        shaderService.bindInt(texture.width, shader.addUniformDeclaration("u_Size"));

        GL46.glBindImageTexture(0, texture.texture, 0, true, 0, GL46.GL_WRITE_ONLY, GL46.GL_RGBA16F);

        COMPUTE_RUNTIME_DATA.groupX = texture.width / NUM_THREADS;
        COMPUTE_RUNTIME_DATA.groupY = texture.height / NUM_THREADS;
        COMPUTE_RUNTIME_DATA.groupZ = texture.depth / NUM_THREADS;
        COMPUTE_RUNTIME_DATA.memoryBarrier = GL46.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT;


        shaderService.dispatch(COMPUTE_RUNTIME_DATA);

//        GL46.glBindTexture(GL46.GL_TEXTURE_3D, texture.texture);
//        GL46.glGenerateMipmap(GL46.GL_TEXTURE_3D);
//        GL46.glBindTexture(GL46.GL_TEXTURE_3D, GL11.GL_NONE);
    }

    @Override
    protected void onAfterRender() {
        GL46.glBindTexture(GL46.GL_TEXTURE_3D, GL11.GL_NONE);
    }

    @Override
    protected Shader getShader() {
        return shaderRepository.cloudsRaymarcher;
    }

    @Override
    protected void bindUniforms() {
        GL46.glEnable(GL11.GL_BLEND);
        shaderService.bindSampler3dDirect(cloudShapeTexture, 0);
        shaderService.bindSampler3dDirect(cloudNoiseTexture, 1);
        shaderService.bindSampler2dDirect(bufferRepository.blueNoiseSampler, 2);
        shaderService.bindSampler2dDirect(bufferRepository.gBufferDepthIndexSampler, 3);
        shaderService.bindFloat(atmosphere.densityMultiplier, densityMultiplier);
        shaderService.bindFloat(atmosphere.densityOffset, densityOffset);
        shaderService.bindFloat(atmosphere.scale, scale);
        shaderService.bindFloat(atmosphere.detailNoiseScale, detailNoiseScale);
        shaderService.bindFloat(atmosphere.detailNoiseWeight, detailNoiseWeight);
        shaderService.bindVec3(atmosphere.detailWeights, detailWeights);
        shaderService.bindVec4(atmosphere.shapeNoiseWeights, shapeNoiseWeights);
        shaderService.bindVec4(atmosphere.phaseParams, phaseParams);
        shaderService.bindInt(atmosphere.numStepsLight, numStepsLight);
        shaderService.bindFloat(atmosphere.rayOffsetStrength, rayOffsetStrength);
        shaderService.bindVec3(atmosphere.boundsMin, boundsMin);
        shaderService.bindVec3(atmosphere.boundsMax, boundsMax);
        shaderService.bindVec3(atmosphere.shapeOffset, shapeOffset);
        shaderService.bindVec3(atmosphere.detailOffset, detailOffset);
        shaderService.bindFloat(atmosphere.lightAbsorptionTowardSun, lightAbsorptionTowardSun);
        shaderService.bindFloat(atmosphere.lightAbsorptionThroughCloud, lightAbsorptionThroughCloud);
        shaderService.bindFloat(atmosphere.darknessThreshold, darknessThreshold);
        shaderService.bindFloat(atmosphere.shapeScrollSpeed, baseSpeed);
        shaderService.bindFloat(atmosphere.detailScrollSpeed, detailSpeed);
    }

    @Override
    public String getTitle() {
        return "Volumetric clouds rendering";
    }
}
