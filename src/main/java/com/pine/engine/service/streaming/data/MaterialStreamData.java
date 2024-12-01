package com.pine.engine.service.streaming.data;

import com.pine.common.inspection.Color;
import com.pine.engine.repository.streaming.StreamableResourceType;
import com.pine.engine.service.streaming.ref.TextureResourceRef;
import com.pine.engine.type.MaterialRenderingMode;

public class MaterialStreamData implements StreamData {
    public TextureResourceRef heightMap;
    public TextureResourceRef normal;
    public TextureResourceRef albedo;
    public TextureResourceRef metallic;
    public TextureResourceRef roughness;
    public TextureResourceRef ao;
    public boolean useParallax = false;
    public float parallaxHeightScale = 1;
    public int parallaxLayers = 16;
    public MaterialRenderingMode renderingMode = MaterialRenderingMode.ISOTROPIC;
    public boolean ssrEnabled;
    public float anisotropicRotation;
    public float clearCoat;
    public float anisotropy;
    public float sheen;
    public float sheenTint;

    public float roughnessVal;
    public float metallicVal;
    public Color albedoColor;


    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.MATERIAL;
    }
}
