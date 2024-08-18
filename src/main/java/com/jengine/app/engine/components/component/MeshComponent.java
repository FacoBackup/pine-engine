package com.jengine.app.engine.components.component;

import java.util.List;

public class MeshComponent extends AbstractComponent{
    private boolean castsShadows = true;
    private String meshID;
    private String materialID;
    private boolean contributeToProbes = true;
    private boolean overrideMaterialUniforms = false;

    @Override
    public List<Class<? extends AbstractComponent>> getDependencies() {
        return List.of(TransformationComponent.class);
    }

    public boolean isCastsShadows() {
        return castsShadows;
    }

    public void setCastsShadows(boolean castsShadows) {
        this.castsShadows = castsShadows;
    }

    public String getMeshID() {
        return meshID;
    }

    public void setMeshID(String meshID) {
        this.meshID = meshID;
    }

    public String getMaterialID() {
        return materialID;
    }

    public void setMaterialID(String materialID) {
        this.materialID = materialID;
    }

    public boolean isContributeToProbes() {
        return contributeToProbes;
    }

    public void setContributeToProbes(boolean contributeToProbes) {
        this.contributeToProbes = contributeToProbes;
    }

    public boolean isOverrideMaterialUniforms() {
        return overrideMaterialUniforms;
    }

    public void setOverrideMaterialUniforms(boolean overrideMaterialUniforms) {
        this.overrideMaterialUniforms = overrideMaterialUniforms;
    }
}
