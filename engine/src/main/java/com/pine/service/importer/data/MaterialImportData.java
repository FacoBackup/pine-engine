package com.pine.service.importer.data;

import com.pine.inspection.MutableField;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.type.MaterialRenderingMode;

public class MaterialImportData extends AbstractImportData {
    @MutableField(group = "Textures", label = "Height", isResource = true)
    public String heightMap;
    @MutableField(group = "Textures", label = "Normal", isResource = true)
    public String normal;
    @MutableField(group = "Textures", label = "Albedo", isResource = true)
    public String albedo;
    @MutableField(group = "Textures", label = "Metallic", isResource = true)
    public String metallic;
    @MutableField(group = "Textures", label = "Roughness", isResource = true)
    public String roughness;
    @MutableField(group = "Textures", label = "Occlusion", isResource = true)
    public String ao;
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

    public MaterialImportData(String name) {
        super(name);
    }

    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.MATERIAL;
    }
}
