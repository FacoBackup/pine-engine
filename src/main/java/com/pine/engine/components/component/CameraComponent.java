package com.pine.engine.components.component;

import java.util.List;

public class CameraComponent extends AbstractComponent {
    private float fov = 45.0f;
    private boolean dynamicAspectRatio = true;
    private float aspectRatio = 1.0f;
    private float zFar = 100.0f;
    private float zNear = 0.1f;
    private boolean distortion = false;
    private float distortionStrength = 1.0f;
    private boolean chromaticAberration = false;
    private float chromaticAberrationStrength = 1.0f;
    private boolean vignette = false;
    private float vignetteStrength = 0.25f;
    private boolean filmGrain = false;
    private float filmGrainStrength = 1.0f;
    private boolean bloom = false;
    private float mbVelocityScale = 1.0f;
    private int mbSamples = 50;
    private float bloomThreshold = 0.75f;
    private int bloomQuality = 8;
    private float bloomOffset = 0.0f;
    private float gamma = 2.2f;
    private float exposure = 1.0f;
    private boolean motionBlurEnabled = true;
    private boolean cameraMotionBlur = false;
    private boolean ortho = false;
    private float size = 10.0f;
    private float apertureDOF = 1.2f;
    private float focalLengthDOF = 10.0f;
    private float focusDistanceDOF = 100.0f;
    private int samplesDOF = 100;
    private boolean enabledDOF = false;

    @Override
    public List<Class<? extends AbstractComponent>> getDependencies() {
        return List.of(TransformationComponent.class);
    }

    public float getFov() {
        return fov;
    }

    public void setFov(float fov) {
        this.fov = fov;
    }

    public boolean isDynamicAspectRatio() {
        return dynamicAspectRatio;
    }

    public void setDynamicAspectRatio(boolean dynamicAspectRatio) {
        this.dynamicAspectRatio = dynamicAspectRatio;
    }

    public float getAspectRatio() {
        return aspectRatio;
    }

    public void setAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public float getzFar() {
        return zFar;
    }

    public void setzFar(float zFar) {
        this.zFar = zFar;
    }

    public float getzNear() {
        return zNear;
    }

    public void setzNear(float zNear) {
        this.zNear = zNear;
    }

    public boolean isDistortion() {
        return distortion;
    }

    public void setDistortion(boolean distortion) {
        this.distortion = distortion;
    }

    public float getDistortionStrength() {
        return distortionStrength;
    }

    public void setDistortionStrength(float distortionStrength) {
        this.distortionStrength = distortionStrength;
    }

    public boolean isChromaticAberration() {
        return chromaticAberration;
    }

    public void setChromaticAberration(boolean chromaticAberration) {
        this.chromaticAberration = chromaticAberration;
    }

    public float getChromaticAberrationStrength() {
        return chromaticAberrationStrength;
    }

    public void setChromaticAberrationStrength(float chromaticAberrationStrength) {
        this.chromaticAberrationStrength = chromaticAberrationStrength;
    }

    public boolean isVignette() {
        return vignette;
    }

    public void setVignette(boolean vignette) {
        this.vignette = vignette;
    }

    public float getVignetteStrength() {
        return vignetteStrength;
    }

    public void setVignetteStrength(float vignetteStrength) {
        this.vignetteStrength = vignetteStrength;
    }

    public boolean isFilmGrain() {
        return filmGrain;
    }

    public void setFilmGrain(boolean filmGrain) {
        this.filmGrain = filmGrain;
    }

    public float getFilmGrainStrength() {
        return filmGrainStrength;
    }

    public void setFilmGrainStrength(float filmGrainStrength) {
        this.filmGrainStrength = filmGrainStrength;
    }

    public boolean isBloom() {
        return bloom;
    }

    public void setBloom(boolean bloom) {
        this.bloom = bloom;
    }

    public float getMbVelocityScale() {
        return mbVelocityScale;
    }

    public void setMbVelocityScale(float mbVelocityScale) {
        this.mbVelocityScale = mbVelocityScale;
    }

    public int getMbSamples() {
        return mbSamples;
    }

    public void setMbSamples(int mbSamples) {
        this.mbSamples = mbSamples;
    }

    public float getBloomThreshold() {
        return bloomThreshold;
    }

    public void setBloomThreshold(float bloomThreshold) {
        this.bloomThreshold = bloomThreshold;
    }

    public int getBloomQuality() {
        return bloomQuality;
    }

    public void setBloomQuality(int bloomQuality) {
        this.bloomQuality = bloomQuality;
    }

    public float getBloomOffset() {
        return bloomOffset;
    }

    public void setBloomOffset(float bloomOffset) {
        this.bloomOffset = bloomOffset;
    }

    public float getGamma() {
        return gamma;
    }

    public void setGamma(float gamma) {
        this.gamma = gamma;
    }

    public float getExposure() {
        return exposure;
    }

    public void setExposure(float exposure) {
        this.exposure = exposure;
    }

    public boolean isMotionBlurEnabled() {
        return motionBlurEnabled;
    }

    public void setMotionBlurEnabled(boolean motionBlurEnabled) {
        this.motionBlurEnabled = motionBlurEnabled;
    }

    public boolean isCameraMotionBlur() {
        return cameraMotionBlur;
    }

    public void setCameraMotionBlur(boolean cameraMotionBlur) {
        this.cameraMotionBlur = cameraMotionBlur;
    }

    public boolean isOrtho() {
        return ortho;
    }

    public void setOrtho(boolean ortho) {
        this.ortho = ortho;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public float getApertureDOF() {
        return apertureDOF;
    }

    public void setApertureDOF(float apertureDOF) {
        this.apertureDOF = apertureDOF;
    }

    public float getFocalLengthDOF() {
        return focalLengthDOF;
    }

    public void setFocalLengthDOF(float focalLengthDOF) {
        this.focalLengthDOF = focalLengthDOF;
    }

    public float getFocusDistanceDOF() {
        return focusDistanceDOF;
    }

    public void setFocusDistanceDOF(float focusDistanceDOF) {
        this.focusDistanceDOF = focusDistanceDOF;
    }

    public int getSamplesDOF() {
        return samplesDOF;
    }

    public void setSamplesDOF(int samplesDOF) {
        this.samplesDOF = samplesDOF;
    }

    public boolean isEnabledDOF() {
        return enabledDOF;
    }

    public void setEnabledDOF(boolean enabledDOF) {
        this.enabledDOF = enabledDOF;
    }
}
