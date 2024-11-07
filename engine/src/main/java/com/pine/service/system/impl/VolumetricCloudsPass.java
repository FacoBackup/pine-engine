package com.pine.service.system.impl;

import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.streaming.ref.TextureResourceRef;
import com.pine.service.voxelization.util.TextureUtil;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL46;

import java.nio.ByteBuffer;

import static com.pine.service.resource.ShaderService.COMPUTE_RUNTIME_DATA;

public class VolumetricCloudsPass extends AbstractQuadPassPass {
    private static final int NUM_THREADS = 8;
    private boolean hasSamplersComputed = false;
    private TextureResourceRef cloudNoiseTexture;
    private TextureResourceRef cloudShapeTexture;
    private UniformDTO uBlueNoise;
    private UniformDTO uCurlNoise;
    private UniformDTO uPlanetCenter;
    private UniformDTO uPlanetRadius;
    private UniformDTO uCloudMinHeight;
    private UniformDTO uCloudMaxHeight;
    private UniformDTO uShapeNoiseScale;
    private UniformDTO uDetailNoiseScale;
    private UniformDTO uDetailNoiseModifier;
    private UniformDTO uTurbulenceNoiseScale;
    private UniformDTO uTurbulenceAmount;
    private UniformDTO uCloudCoverage;
    private UniformDTO uWindDirection;
    private UniformDTO uWindSpeed;
    private UniformDTO uWindShearOffset;
    private UniformDTO uElapsedDayTime;
    private UniformDTO uMaxNumSteps;
    private UniformDTO uLightStepLength;
    private UniformDTO uLightConeRadius;
    private UniformDTO uSunColor;
    private UniformDTO uCloudBaseColor;
    private UniformDTO uCloudTopColor;
    private UniformDTO uPrecipitation;
    private UniformDTO uAmbientLightFactor;
    private UniformDTO uSunLightFactor;
    private UniformDTO uHenyeyGreensteinGForward;
    private UniformDTO uHenyeyGreensteinGBackward;

    @Override
    public void onInitialize() {
        uBlueNoise = addUniformDeclaration("uBlueNoise");
        uCurlNoise = addUniformDeclaration("uCurlNoise");
        uPlanetCenter = addUniformDeclaration("uPlanetCenter");
        uPlanetRadius = addUniformDeclaration("uPlanetRadius");
        uCloudMinHeight = addUniformDeclaration("uCloudMinHeight");
        uCloudMaxHeight = addUniformDeclaration("uCloudMaxHeight");
        uShapeNoiseScale = addUniformDeclaration("uShapeNoiseScale");
        uDetailNoiseScale = addUniformDeclaration("uDetailNoiseScale");
        uDetailNoiseModifier = addUniformDeclaration("uDetailNoiseModifier");
        uTurbulenceNoiseScale = addUniformDeclaration("uTurbulenceNoiseScale");
        uTurbulenceAmount = addUniformDeclaration("uTurbulenceAmount");
        uCloudCoverage = addUniformDeclaration("uCloudCoverage");
        uWindDirection = addUniformDeclaration("uWindDirection");
        uWindSpeed = addUniformDeclaration("uWindSpeed");
        uWindShearOffset = addUniformDeclaration("uWindShearOffset");
        uElapsedDayTime = addUniformDeclaration("uElapsedDayTime");
        uMaxNumSteps = addUniformDeclaration("uMaxNumSteps");
        uLightStepLength = addUniformDeclaration("uLightStepLength");
        uLightConeRadius = addUniformDeclaration("uLightConeRadius");
        uSunColor = addUniformDeclaration("uSunColor");
        uCloudBaseColor = addUniformDeclaration("uCloudBaseColor");
        uCloudTopColor = addUniformDeclaration("uCloudTopColor");
        uPrecipitation = addUniformDeclaration("uPrecipitation");
        uAmbientLightFactor = addUniformDeclaration("uAmbientLightFactor");
        uSunLightFactor = addUniformDeclaration("uSunLightFactor");
        uHenyeyGreensteinGForward = addUniformDeclaration("uHenyeyGreensteinGForward");
        uHenyeyGreensteinGBackward = addUniformDeclaration("uHenyeyGreensteinGBackward");
    }

    @Override
    protected FrameBufferObject getTargetFBO() {
        return fboRepository.auxBuffer;
    }

    @Override
    protected boolean isRenderable() {
        return cloudsRepository.enabled;
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

        COMPUTE_RUNTIME_DATA.groupX = texture.width/NUM_THREADS;
        COMPUTE_RUNTIME_DATA.groupY = texture.height/NUM_THREADS;
        COMPUTE_RUNTIME_DATA.groupZ = texture.depth/NUM_THREADS;
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
//        shaderService.bindSampler2d(cloudsRepository.blueNoise, uBlueNoise);
//        shaderService.bindSampler2d(cloudsRepository.curlNoise, uCurlNoise);
        shaderService.bindVec3(cloudsRepository.planetCenter, uPlanetCenter);
        shaderService.bindFloat(cloudsRepository.planetRadius, uPlanetRadius);
        shaderService.bindFloat(cloudsRepository.cloudMinHeight, uCloudMinHeight);
        shaderService.bindFloat(cloudsRepository.cloudMaxHeight, uCloudMaxHeight);
        shaderService.bindFloat(cloudsRepository.shapeNoiseScale, uShapeNoiseScale);
        shaderService.bindFloat(cloudsRepository.detailNoiseScale, uDetailNoiseScale);
        shaderService.bindFloat(cloudsRepository.detailNoiseModifier, uDetailNoiseModifier);
        shaderService.bindFloat(cloudsRepository.turbulenceNoiseScale, uTurbulenceNoiseScale);
        shaderService.bindFloat(cloudsRepository.turbulenceAmount, uTurbulenceAmount);
        shaderService.bindFloat(cloudsRepository.cloudCoverage, uCloudCoverage);
        shaderService.bindVec3(cloudsRepository.windDirection, uWindDirection);
        shaderService.bindFloat(cloudsRepository.windSpeed, uWindSpeed);
        shaderService.bindFloat(cloudsRepository.windShearOffset, uWindShearOffset);
        shaderService.bindFloat(atmosphere.elapsedTime, uElapsedDayTime);
        shaderService.bindFloat(cloudsRepository.maxNumSteps, uMaxNumSteps);
        shaderService.bindFloat(cloudsRepository.lightStepLength, uLightStepLength);
        shaderService.bindFloat(cloudsRepository.lightConeRadius, uLightConeRadius);
        shaderService.bindVec3(cloudsRepository.sunColor, uSunColor);
        shaderService.bindVec3(cloudsRepository.cloudBaseColor, uCloudBaseColor);
        shaderService.bindVec3(cloudsRepository.cloudTopColor, uCloudTopColor);
        shaderService.bindFloat(cloudsRepository.precipitation, uPrecipitation);
        shaderService.bindFloat(cloudsRepository.ambientLightFactor, uAmbientLightFactor);
        shaderService.bindFloat(cloudsRepository.sunLightFactor, uSunLightFactor);
        shaderService.bindFloat(cloudsRepository.henyeyGreensteinGForward, uHenyeyGreensteinGForward);
        shaderService.bindFloat(cloudsRepository.henyeyGreensteinGBackward, uHenyeyGreensteinGBackward);
    }

    @Override
    public String getTitle() {
        return "Volumetric clouds rendering";
    }
}
