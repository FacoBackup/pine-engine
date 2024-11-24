package com.pine.engine.repository.terrain;

import com.pine.common.Icons;
import com.pine.common.inspection.InspectableField;
import com.pine.engine.inspection.ResourceTypeField;
import com.pine.engine.repository.streaming.StreamableResourceType;

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
