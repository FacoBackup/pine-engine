package com.pine.service.streaming.material;

import com.pine.inspection.MutableField;
import com.pine.repository.streaming.AbstractStreamableResource;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.streaming.texture.TextureStreamableResource;
import com.pine.theme.Icons;
import com.pine.type.MaterialRenderingMode;

import javax.swing.*;


public class MaterialStreamableResource extends AbstractStreamableResource<MaterialStreamData> {
    @MutableField(group = "Textures", label = "Height")
    public TextureStreamableResource heightMap;
    @MutableField(group = "Textures", label = "Normal")
    public TextureStreamableResource normal;
    @MutableField(group = "Textures", label = "Albedo")
    public TextureStreamableResource albedo;
    @MutableField(group = "Textures", label = "Metallic")
    public TextureStreamableResource metallic;
    @MutableField(group = "Textures", label = "Roughness")
    public TextureStreamableResource roughness;
    @MutableField(group = "Textures", label = "Occlusion")
    public TextureStreamableResource ao;
    @MutableField(group = "Material", label = "use parallax")
    public boolean useParallax = false;
    @MutableField(group = "Material", label = "Parallax height scale")
    public float parallaxHeightScale = 1;
    @MutableField(group = "Material", label = "Parallax layers")
    public int parallaxLayers = 16;
    @MutableField(group = "Material", label = "Rendering mode")
    public MaterialRenderingMode renderingMode = MaterialRenderingMode.ISOTROPIC;
    @MutableField(group = "Material", label = "Screen space reflections")
    public boolean ssrEnabled;
    @MutableField(group = "Material", label = "Anisotropic rotation")
    public float anisotropicRotation;
    @MutableField(group = "Material", label = "Clear coat")
    public float clearCoat;
    @MutableField(group = "Material", label = "Anisotropy")
    public float anisotropy;
    @MutableField(group = "Material", label = "Sheen")
    public float sheen;
    @MutableField(group = "Material", label = "Sheen tint")
    public float sheenTint;

    public MaterialStreamableResource(String pathToFile, String id) {
        super(pathToFile, id);
    }

    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.MATERIAL;
    }

    @Override
    protected void loadInternal(MaterialStreamData data) {
    }

    @Override
    protected void disposeInternal() {
    }

    @Override
    public String getIcon() {
        return Icons.format_paint;
    }
}
