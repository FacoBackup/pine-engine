package com.pine.engine.repository.terrain;

import com.pine.common.Icons;
import com.pine.common.inspection.InspectableField;
import com.pine.engine.inspection.ResourceTypeField;
import com.pine.engine.repository.streaming.StreamableResourceType;

public class MaterialInstance extends AbstractDataInstance {

    @ResourceTypeField(type = StreamableResourceType.TEXTURE)
    @InspectableField(label = "Albedo")
    public String albedo;

    @ResourceTypeField(type = StreamableResourceType.TEXTURE)
    @InspectableField(label = "Normal")
    public String normal;

    @ResourceTypeField(type = StreamableResourceType.TEXTURE)
    @InspectableField(label = "Roughness")
    public String roughness;

    @ResourceTypeField(type = StreamableResourceType.TEXTURE)
    @InspectableField(label = "Metallic")
    public String metallic;

    public MaterialInstance(int i) {
        super(i);
        name = "New material " + i;
    }

    @Override
    public String getIcon() {
        return Icons.format_paint;
    }
}
