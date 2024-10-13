package com.pine.service.streaming.material;

import com.pine.inspection.Inspectable;
import com.pine.inspection.MutableField;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.streaming.StreamLoadData;
import com.pine.service.streaming.texture.TextureStreamableResource;
import com.pine.theme.Icons;
import com.pine.type.MaterialRenderingMode;

import javax.swing.*;
import java.nio.ByteBuffer;

public class MaterialStreamData implements StreamLoadData {
    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.MATERIAL;
    }
}
