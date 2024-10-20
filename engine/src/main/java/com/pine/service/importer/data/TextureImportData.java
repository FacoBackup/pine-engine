package com.pine.service.importer.data;

import com.pine.FSUtil;
import com.pine.repository.streaming.StreamableResourceType;

public class TextureImportData extends AbstractImportData{
    public final String path;

    public TextureImportData(String path) {
        super(FSUtil.getNameFromPath(path));
        this.path = path;
    }

    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.TEXTURE;
    }
}
