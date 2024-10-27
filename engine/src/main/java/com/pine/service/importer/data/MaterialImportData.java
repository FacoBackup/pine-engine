package com.pine.service.importer.data;

import com.pine.inspection.Color;
import com.pine.inspection.InspectableField;
import com.pine.inspection.ResourceTypeField;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.type.MaterialRenderingMode;

public class MaterialImportData extends AbstractImportData {
    @InspectableField(group = "Fallback values", label = "Roughness", max = 1, min = 0)
    public float roughnessVal = 1;

    @InspectableField(group = "Fallback values", label = "Metallic", max = 1, min = 0)
    public float metallicVal = 0;

    @InspectableField(group = "Fallback values", label = "Albedo color")
    public Color albedoColor = new Color();

    @ResourceTypeField(type = StreamableResourceType.TEXTURE)
    @InspectableField(group = "Textures", label = "Albedo")
    public String albedo;

    @ResourceTypeField(type = StreamableResourceType.TEXTURE)
    @InspectableField(group = "Textures", label = "Normal")
    public String normal;

    @ResourceTypeField(type = StreamableResourceType.TEXTURE)
    @InspectableField(group = "Textures", label = "Metallic")
    public String metallic;

    @ResourceTypeField(type = StreamableResourceType.TEXTURE)
    @InspectableField(group = "Textures", label = "Roughness")
    public String roughness;

    @ResourceTypeField(type = StreamableResourceType.TEXTURE)
    @InspectableField(group = "Textures", label = "Occlusion")
    public String ao;

    @ResourceTypeField(type = StreamableResourceType.TEXTURE)
    @InspectableField(group = "Textures", label = "Height")
    public String heightMap;

    @InspectableField(group = "Material", label = "use parallax")
    public boolean useParallax = false;
    @InspectableField(group = "Material", label = "Parallax height scale")
    public float parallaxHeightScale = 1;
    @InspectableField(group = "Material", label = "Parallax layers")
    public int parallaxLayers = 16;
    @InspectableField(group = "Material", label = "Rendering mode")
    public MaterialRenderingMode renderingMode = MaterialRenderingMode.ISOTROPIC;
    @InspectableField(group = "Material", label = "Screen space reflections")
    public boolean ssrEnabled;
    @InspectableField(group = "Material", label = "Anisotropic rotation")
    public float anisotropicRotation;
    @InspectableField(group = "Material", label = "Clear coat")
    public float clearCoat;
    @InspectableField(group = "Material", label = "Anisotropy")
    public float anisotropy;
    @InspectableField(group = "Material", label = "Sheen")
    public float sheen;
    @InspectableField(group = "Material", label = "Sheen tint")
    public float sheenTint;


    public MaterialImportData(String name) {
        super(name);
    }

    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.MATERIAL;
    }
}
