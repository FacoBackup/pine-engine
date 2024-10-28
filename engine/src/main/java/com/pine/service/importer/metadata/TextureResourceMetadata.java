package com.pine.service.importer.metadata;

import com.pine.inspection.InspectableField;
import com.pine.inspection.TypePreviewField;
import com.pine.repository.streaming.StreamableResourceType;

public class TextureResourceMetadata extends AbstractResourceMetadata {

    @InspectableField(label = "Width", disabled = true)
    public final int originalWidth;

    @InspectableField(label = "Height", disabled = true)
    public final int originalHeight;

    @TypePreviewField
    @InspectableField(label = "Preview", disabled = true)
    public final String pathToPreview;

    public TextureResourceMetadata(String name, String id, int originalWidth, int originalHeight, String pathToPreview) {
        super(name, id);
        this.originalWidth = originalWidth;
        this.pathToPreview = pathToPreview;
        this.originalHeight = originalHeight;
    }

    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.TEXTURE;
    }
}
