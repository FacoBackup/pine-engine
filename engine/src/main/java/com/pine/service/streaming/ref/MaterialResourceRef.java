package com.pine.service.streaming.ref;

import com.pine.repository.streaming.AbstractResourceRef;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.streaming.data.MaterialStreamData;
import com.pine.type.MaterialRenderingMode;


public class MaterialResourceRef extends AbstractResourceRef<MaterialStreamData> {
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


    public UniformDTO anisotropicRotationUniform;
    public UniformDTO anisotropyUniform;
    public UniformDTO clearCoatUniform;
    public UniformDTO sheenUniform;
    public UniformDTO sheenTintUniform;
    public UniformDTO renderingModeUniform;
    public UniformDTO ssrEnabledUniform;
    public UniformDTO parallaxHeightScaleUniform;
    public UniformDTO parallaxLayersUniform;
    public UniformDTO useParallaxUniform;


    public int albedoLocation;
    public int roughnessLocation;
    public int metallicLocation;
    public int aoLocation;
    public int normalLocation;
    public int heightMapLocation;

    public MaterialResourceRef(String id) {
        super(id);
    }

    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.MATERIAL;
    }

    @Override
    protected void loadInternal(MaterialStreamData data) {
        heightMap = data.heightMap;
        normal = data.normal;
        albedo = data.albedo;
        metallic = data.metallic;
        roughness = data.roughness;
        ao = data.ao;
        useParallax = data.useParallax;
        parallaxHeightScale = data.parallaxHeightScale;
        parallaxLayers = data.parallaxLayers;
        renderingMode = data.renderingMode;
        ssrEnabled = data.ssrEnabled;
        anisotropicRotation = data.anisotropicRotation;
        clearCoat = data.clearCoat;
        anisotropy = data.anisotropy;
        sheen = data.sheen;
        sheenTint = data.sheenTint;
    }

    @Override
    protected void disposeInternal() {
    }
}
