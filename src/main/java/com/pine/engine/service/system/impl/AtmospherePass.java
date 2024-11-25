package com.pine.engine.service.system.impl;

import com.pine.engine.service.resource.fbo.FBO;
import com.pine.engine.service.resource.shader.Shader;
import com.pine.engine.service.resource.shader.UniformDTO;
import com.pine.engine.service.streaming.ref.TextureResourceRef;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL46;

import static com.pine.engine.service.resource.shader.ShaderService.COMPUTE_RUNTIME_DATA;

public class AtmospherePass extends AbstractQuadPass {
    private static final int NUM_THREADS = 8;
    private boolean hasSamplersComputed = false;

    private UniformDTO densityMultiplier;
    private UniformDTO cloudCoverage;
    private UniformDTO scale;
    private UniformDTO detailNoiseScale;
    private UniformDTO cloudErosionStrength;
    private UniformDTO numStepsLight;
    private UniformDTO rayOffsetStrength;
    private UniformDTO boundsMin;
    private UniformDTO boundsMax;
    private UniformDTO lightAbsorptionTowardSun;
    private UniformDTO lightAbsorptionThroughCloud;
    private UniformDTO darknessThreshold;
    private UniformDTO baseSpeed;
    private UniformDTO detailSpeed;
    private UniformDTO type;
    private UniformDTO rayleighBeta;
    private UniformDTO mieBeta;
    private UniformDTO intensity;
    private UniformDTO atmosphereRadius;
    private UniformDTO planetRadius;
    private UniformDTO rayleighHeight;
    private UniformDTO mieHeight;
    private UniformDTO threshold;
    private UniformDTO samples;

    private final Vector3f boundsMinVal = new Vector3f();
    private final Vector3f boundsMaxVal = new Vector3f();

    @Override
    public void onInitialize() {
        type = addUniformDeclaration("type");
        rayleighBeta = addUniformDeclaration("rayleighBeta");
        mieBeta = addUniformDeclaration("mieBeta");
        intensity = addUniformDeclaration("intensity");
        atmosphereRadius = addUniformDeclaration("atmosphereRadius");
        planetRadius = addUniformDeclaration("planetRadius");
        rayleighHeight = addUniformDeclaration("rayleighHeight");
        mieHeight = addUniformDeclaration("mieHeight");
        threshold = addUniformDeclaration("threshold");
        samples = addUniformDeclaration("samples");

        densityMultiplier = addUniformDeclaration("densityMultiplier");
        cloudCoverage = addUniformDeclaration("cloudCoverage");
        scale = addUniformDeclaration("scale");
        detailNoiseScale = addUniformDeclaration("detailNoiseScale");
        cloudErosionStrength = addUniformDeclaration("cloudErosionStrength");
        numStepsLight = addUniformDeclaration("numStepsLight");
        rayOffsetStrength = addUniformDeclaration("rayOffsetStrength");
        boundsMin = addUniformDeclaration("boundsMin");
        boundsMax = addUniformDeclaration("boundsMax");
        lightAbsorptionTowardSun = addUniformDeclaration("lightAbsorptionTowardSun");
        lightAbsorptionThroughCloud = addUniformDeclaration("lightAbsorptionThroughCloud");
        baseSpeed = addUniformDeclaration("baseSpeed");
        detailSpeed = addUniformDeclaration("detailSpeed");
    }

    @Override
    protected FBO getTargetFBO() {
        return bufferRepository.auxBufferQuaterRes;
    }

    @Override
    protected boolean isRenderable() {
        return atmosphere.enabled;
    }

    @Override
    protected void onBeforeRender() {
        if (!hasSamplersComputed) {
            dispatch(bufferRepository.cloudNoiseTexture, shaderRepository.cloudDetailCompute);
            dispatch(bufferRepository.cloudShapeTexture, shaderRepository.cloudShapeCompute);

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
    }

    @Override
    protected Shader getShader() {
        return shaderRepository.cloudsRaymarcher;
    }

    @Override
    protected void bindUniforms() {
        shaderService.bindSampler3dDirect(bufferRepository.cloudShapeTexture, 0);
        shaderService.bindSampler3dDirect(bufferRepository.cloudNoiseTexture, 1);
        shaderService.bindFloat(atmosphere.densityMultiplier/10, densityMultiplier);
        shaderService.bindFloat(atmosphere.cloudCoverage, cloudCoverage);
        shaderService.bindFloat(atmosphere.scale/100, scale);
        shaderService.bindFloat(atmosphere.detailNoiseScale, detailNoiseScale);
        shaderService.bindFloat(atmosphere.cloudErosionStrength, cloudErosionStrength);
        shaderService.bindInt(atmosphere.numStepsLight, numStepsLight);
        shaderService.bindFloat(atmosphere.rayOffsetStrength, rayOffsetStrength);

        boundsMinVal.x = -atmosphere.cloudsSize;
        boundsMinVal.y = atmosphere.cloudsAltitude;
        boundsMinVal.z = -atmosphere.cloudsSize;
        boundsMaxVal.x = atmosphere.cloudsSize;
        boundsMaxVal.y = atmosphere.cloudsAltitude + atmosphere.cloudsHeight;
        boundsMaxVal.z = atmosphere.cloudsSize;

        shaderService.bindVec3(boundsMinVal, boundsMin);
        shaderService.bindVec3(boundsMaxVal, boundsMax);
        shaderService.bindFloat(atmosphere.lightAbsorptionTowardSun, lightAbsorptionTowardSun);
        shaderService.bindFloat(atmosphere.lightAbsorptionThroughCloud, lightAbsorptionThroughCloud);
        shaderService.bindFloat(atmosphere.shapeScrollSpeed, baseSpeed);
        shaderService.bindFloat(atmosphere.detailScrollSpeed, detailSpeed);

        // ATMOSPHERE
        shaderService.bindInt(atmosphere.renderingType.getId(), type);
        shaderService.bindVec3(atmosphere.betaRayleigh, rayleighBeta);
        shaderService.bindVec3(atmosphere.betaMie, mieBeta);
        shaderService.bindFloat(atmosphere.intensity, intensity);
        shaderService.bindFloat(atmosphere.atmosphereRadius, atmosphereRadius);
        shaderService.bindFloat(atmosphere.planetRadius, planetRadius);
        shaderService.bindFloat(atmosphere.rayleighHeight, rayleighHeight);
        shaderService.bindFloat(atmosphere.mieHeight, mieHeight);
        shaderService.bindFloat(atmosphere.threshold, threshold);
        shaderService.bindInt(atmosphere.maxSamples, samples);
    }

    @Override
    public String getTitle() {
        return "Volumetric clouds rendering";
    }
}
