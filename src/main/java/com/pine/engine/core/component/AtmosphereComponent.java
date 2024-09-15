package com.pine.engine.core.component;

import com.pine.engine.core.EngineInjectable;
import com.pine.engine.core.type.AtmosphereType;
import org.joml.Vector3f;

import java.util.Set;

@EngineInjectable
public class AtmosphereComponent extends AbstractComponent<AtmosphereComponent> {
    
    public float elapsedTime = 0;
    public Vector3f sunDirection = new Vector3f(0, 1, 1);
    public int maxSamples = 10;
    public int mieHeight = 1000;
    public int rayleighHeight = 8000;
    public float atmosphereRadius = 1;
    public float planetRadius = 1;
    public float intensity = 20;
    public AtmosphereType renderingType = AtmosphereType.COMBINED;
    public float[] betaRayleigh = {1.0f, 1.0f, 1.0f};
    public float[] betaMie = {1.0f, 1.0f, 1.0f};
    public float threshold = 0;

    public AtmosphereComponent() {
        super();
    }

    public AtmosphereComponent(Integer entityId) {
        super(entityId);
    }

    @Override
    protected Set<Class<? extends AbstractComponent>> getDependenciesInternal() {
        return Set.of(TransformationComponent.class);
    }

    public void setElapsedTime(float elapsedTime) {
        this.elapsedTime = elapsedTime;
        sunDirection = (new Vector3f((float) Math.sin(elapsedTime), (float) Math.cos(elapsedTime), 1.0f)).normalize();
    }
}
