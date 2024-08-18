package com.jengine.app.engine.components.component;
import com.jengine.app.engine.components.AtmosphereType;
import org.joml.Vector3f;

import java.util.List;

public class AtmosphereComponent extends AbstractComponent{
    private boolean needsRepackaging = false;
    private float elapsedTime = 0;
    private Vector3f sunDirection = new Vector3f(0, 1, 1);
    private int maxSamples = 10;
    private int mieHeight = 1000;
    private int rayleighHeight = 8000;
    private float atmosphereRadius = 1;
    private float planetRadius = 1;
    private float intensity = 20;
    private AtmosphereType renderingType = AtmosphereType.COMBINED;
    private float[] betaRayleigh = {1.0f, 1.0f, 1.0f};
    private float[] betaMie = {1.0f, 1.0f, 1.0f};
    private float threshold = 0;

    @Override
    public List<Class<? extends AbstractComponent>> getDependencies() {
        return List.of(TransformationComponent.class);
    }

    public Vector3f getSunDirection() {
        return sunDirection;
    }

    public void setElapsedTime(float elapsedTime) {
        this.elapsedTime = elapsedTime;
        sunDirection = (new Vector3f((float)Math.sin(elapsedTime), (float)Math.cos(elapsedTime), 1.0f)).normalize(sunDirection);
        needsRepackaging = true;
    }

    public float getElapsedTime() {
        return elapsedTime;
    }

    public boolean isNeedsRepackaging() {
        return needsRepackaging;
    }

    public void setNeedsRepackaging(boolean needsRepackaging) {
        this.needsRepackaging = needsRepackaging;
    }

    public void setSunDirection(Vector3f sunDirection) {
        this.sunDirection = sunDirection;
    }

    public int getMaxSamples() {
        return maxSamples;
    }

    public void setMaxSamples(int maxSamples) {
        this.maxSamples = maxSamples;
    }

    public int getMieHeight() {
        return mieHeight;
    }

    public void setMieHeight(int mieHeight) {
        this.mieHeight = mieHeight;
    }

    public int getRayleighHeight() {
        return rayleighHeight;
    }

    public void setRayleighHeight(int rayleighHeight) {
        this.rayleighHeight = rayleighHeight;
    }

    public float getAtmosphereRadius() {
        return atmosphereRadius;
    }

    public void setAtmosphereRadius(float atmosphereRadius) {
        this.atmosphereRadius = atmosphereRadius;
    }

    public float getPlanetRadius() {
        return planetRadius;
    }

    public void setPlanetRadius(float planetRadius) {
        this.planetRadius = planetRadius;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public AtmosphereType getRenderingType() {
        return renderingType;
    }

    public void setRenderingType(AtmosphereType renderingType) {
        this.renderingType = renderingType;
    }

    public float[] getBetaRayleigh() {
        return betaRayleigh;
    }

    public void setBetaRayleigh(float[] betaRayleigh) {
        this.betaRayleigh = betaRayleigh;
    }

    public float[] getBetaMie() {
        return betaMie;
    }

    public void setBetaMie(float[] betaMie) {
        this.betaMie = betaMie;
    }

    public float getThreshold() {
        return threshold;
    }

    public void setThreshold(float threshold) {
        this.threshold = threshold;
    }
}
