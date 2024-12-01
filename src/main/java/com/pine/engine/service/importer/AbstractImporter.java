package com.pine.engine.service.importer;

import com.pine.FSUtil;
import com.pine.common.injection.PInject;
import com.pine.common.messaging.Loggable;
import com.pine.engine.Engine;
import com.pine.engine.repository.streaming.StreamableResourceType;
import com.pine.engine.service.importer.data.AbstractImportData;
import com.pine.engine.service.importer.metadata.AbstractResourceMetadata;

import java.io.File;
import java.util.List;

public abstract class AbstractImporter implements Loggable {

    @PInject
    public Engine engine;

    @PInject
    public ImporterService importerService;

    public List<AbstractImportData> importFile(String path) {
        return List.of();
    }

    public abstract StreamableResourceType getResourceType();

    public abstract AbstractResourceMetadata persist(AbstractImportData data);

    public File persistInternal(AbstractImportData data) {
        if (FSUtil.writeBinary(data, getPathToFile(data))) {
            return new File(getPathToFile(data));
        }
        return null;
    }

    protected String getPathToFile(AbstractImportData data) {
        return importerService.getPathToFile(data.id, data.getResourceType());
    }

    public AbstractResourceMetadata createNew() {
        return null;
    }
}