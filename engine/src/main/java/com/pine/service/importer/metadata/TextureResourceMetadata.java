package com.pine.service.importer.metadata;

import com.pine.repository.streaming.StreamableResourceType;

public class TextureResourceMetadata extends AbstractResourceMetadata {
    public final int originalWidth;
    public final String pathToPreview;
    public final int originalHeight;

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
