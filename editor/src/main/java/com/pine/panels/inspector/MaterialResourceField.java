package com.pine.panels.inspector;

import com.pine.repository.streaming.StreamableResourceType;

public class MaterialResourceField extends AbstractResourceField {
    @Override
    public String getSelected() {
        return foliage.material;
    }

    @Override
    public void setSelected(String selected) {
        foliage.material = selected;
    }

    @Override
    public StreamableResourceType getType() {
        return StreamableResourceType.MATERIAL;
    }
}
