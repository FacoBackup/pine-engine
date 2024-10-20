package com.pine.service.importer.data;

import com.pine.inspection.MutableField;
import com.pine.inspection.ResourceTypeField;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.type.MaterialRenderingMode;

public class MaterialImportData extends AbstractImportData {
    @ResourceTypeField(type = StreamableResourceType.TEXTURE)
    @MutableField(group = "Textures", label = "Height")
    public String heightMap;

    @ResourceTypeField(type = StreamableResourceType.TEXTURE)
    @MutableField(group = "Textures", label = "Normal")
    public String normal;

    @ResourceTypeField(type = StreamableResourceType.TEXTURE)
    @MutableField(group = "Textures", label = "Albedo")
    public String albedo;
    
    @ResourceTypeField(type = StreamableResourceType.TEXTURE)
    @MutableField(group = "Textures", label = "Metallic")
    public String metallic;

    @ResourceTypeField(type = StreamableResourceType.TEXTURE)
    @MutableField(group = "Textures", label = "Roughness")
    public String roughness;

    @ResourceTypeField(type = StreamableResourceType.TEXTURE)
    @MutableField(group = "Textures", label = "Occlusion")
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
