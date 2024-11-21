package com.pine.repository.terrain;

import com.pine.inspection.InspectableField;
import com.pine.inspection.ResourceTypeField;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.theme.Icons;

public class MaterialInstance extends AbstractDataInstance {

    @ResourceTypeField(type = StreamableResourceType.MATERIAL)
    @InspectableField(label = "Material")
    public String material;

    public MaterialInstance(int i) {
        super(i);
        name = "New material " + i;
    }

    @Override
    public String getIcon() {
        return Icons.format_paint;
    }
}
