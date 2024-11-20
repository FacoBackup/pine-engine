package com.pine.panels.inspector;

import com.pine.repository.streaming.StreamableResourceType;

public class MeshResourceField extends AbstractResourceField {
    @Override
    public String getSelected() {
        return foliage.mesh;
    }

    @Override
    public void setSelected(String selected) {
        foliage.mesh = selected;
    }

    @Override
    public StreamableResourceType getType() {
        return StreamableResourceType.MESH;
    }
}
