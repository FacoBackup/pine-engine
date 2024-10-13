package com.pine.service.importer.impl;

import com.pine.injection.PBean;
import com.pine.repository.streaming.AbstractStreamableResource;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.importer.AbstractImporter;

import java.util.Collections;
import java.util.List;

@PBean
public class AudioImporter extends AbstractImporter {

    @Override
    public List<AbstractStreamableResource<?>> load(String path) {
        return Collections.emptyList();
    }

    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.AUDIO;
    }
}
