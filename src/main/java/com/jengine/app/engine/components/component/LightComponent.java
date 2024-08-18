package com.jengine.app.engine.components.component;

import com.jengine.app.engine.components.LightType;
import org.joml.Matrix4f;

import java.util.List;

public class LightComponent extends AbstractComponent {
    private boolean needsRepackaging = false;
    private boolean hasSSS = false;
    private double shadowBias = 0.0001;
    private int shadowSamples = 3;
    private double zNear = 1;
    private double zFar = 10000;
    private double cutoff = 50;
    private double shadowAttenuationMinDistance = 50;
    private double[] attenuation = {0, 0};
    private double smoothing = 0.5;
    private double radius = 45;
    private double size = 35;
    private double areaRadius = 1;
    private double planeAreaWidth = 1;
    private double planeAreaHeight = 1;
    private int intensity = 1;
    private LightType type = LightType.DIRECTIONAL;
    private int[] color = {255, 255, 255};
    private double[] fixedColor = {1, 1, 1};
    private boolean shadowMap = true;
    private int[] atlasFace = {0, 0};
    private final Matrix4f __lightView = new Matrix4f();
    private final Matrix4f __lightProjection = new Matrix4f();

    @Override
    public List<Class<? extends AbstractComponent>> getDependencies() {
        return List.of(TransformationComponent.class);
    }

    public void setNeedsRepackaging(boolean needsRepackaging) {
        this.needsRepackaging = needsRepackaging;
    }

    public double getzNear() {
        return zNear;
    }

    public void setzNear(double zNear) {
        this.zNear = zNear;
    }

    public double getzFar() {
        return zFar;
    }

    public void setzFar(double zFar) {
        this.zFar = zFar;
    }

    public double[] getFixedColor() {
        return fixedColor;
    }

    public void setFixedColor(double[] fixedColor) {
        this.fixedColor = fixedColor;
    }

    public int[] getAtlasFace() {
        return atlasFace;
    }

    public void setAtlasFace(int[] atlasFace) {
        this.atlasFace = atlasFace;
    }

    public Matrix4f getLightView() {
        return __lightView;
    }

    public Matrix4f getLightProjection() {
        return __lightProjection;
    }


    public double getPlaneAreaHeight() {
        return planeAreaHeight;
    }

    public void setPlaneAreaHeight(double planeAreaHeight) {
        this.needsRepackaging = true;
        this.planeAreaHeight = planeAreaHeight;
    }

    public double getPlaneAreaWidth() {
        return planeAreaWidth;
    }

    public void setPlaneAreaWidth(double planeAreaWidth) {
        this.needsRepackaging = true;
        this.planeAreaWidth = planeAreaWidth;
    }

    public double getAreaRadius() {
        return areaRadius;
    }

    public void setAreaRadius(double areaRadius) {
        this.needsRepackaging = true;
        this.areaRadius = areaRadius;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.needsRepackaging = true;
        this.size = size;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.needsRepackaging = true;
        this.radius = radius;
    }

    public double getSmoothing() {
        return smoothing;
    }

    public void setSmoothing(double smoothing) {
        this.needsRepackaging = true;
        this.smoothing = smoothing;
    }

    public double[] getAttenuation() {
        return attenuation;
    }

    public void setAttenuation(double[] attenuation) {
        this.needsRepackaging = true;
        this.attenuation = attenuation;
    }

    public double getShadowAttenuationMinDistance() {
        return shadowAttenuationMinDistance;
    }

    public void setShadowAttenuationMinDistance(double shadowAttenuationMinDistance) {
        this.needsRepackaging = true;
        this.shadowAttenuationMinDistance = shadowAttenuationMinDistance;
    }

    public double getCutoff() {
        return cutoff;
    }

    public void setCutoff(double cutoff) {
        this.needsRepackaging = true;
        this.cutoff = cutoff;
    }

    public double getZFar() {
        return zFar;
    }

    public void setZFar(double zFar) {
        this.needsRepackaging = true;
        this.zFar = zFar;
    }

    public double getZNear() {
        return zNear;
    }

    public void setZNear(double zNear) {
        this.needsRepackaging = true;
        this.zNear = zNear;
    }

    public int getShadowSamples() {
        return shadowSamples;
    }

    public void setShadowSamples(int shadowSamples) {
        this.needsRepackaging = true;
        this.shadowSamples = shadowSamples;
    }

    public double getShadowBias() {
        return shadowBias;
    }

    public void setShadowBias(double shadowBias) {
        this.needsRepackaging = true;
        this.shadowBias = shadowBias;
    }

    public boolean isHasSSS() {
        return hasSSS;
    }

    public void setHasSSS(boolean hasSSS) {
        this.needsRepackaging = true;
        this.hasSSS = hasSSS;
    }

    public boolean isNeedsRepackaging() {
        return needsRepackaging;
    }

    public int getIntensity() {
        return intensity;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
        this.fixedColor = new double[]{this.color[0] * this.intensity / 255.0, this.color[1] * this.intensity / 255.0, this.color[2] * this.intensity / 255.0};
        this.needsRepackaging = true;
    }

    public LightType getType() {
        return type;
    }

    public void setType(LightType type) {
        this.needsRepackaging = true;
        this.type = type;
    }

    public int[] getColor() {
        return color;
    }

    public void setColor(int[] color) {
        this.color = color;
        this.needsRepackaging = true;
        this.fixedColor = new double[]{this.color[0] * this.intensity / 255.0, this.color[1] * this.intensity / 255.0, this.color[2] * this.intensity / 255.0};
    }

    public boolean isShadowMap() {
        return shadowMap;
    }

    public void setShadowMap(boolean shadowMap) {
        if (this.shadowMap != shadowMap) {
            this.needsRepackaging = true;
        }
        this.shadowMap = shadowMap;
    }
}

