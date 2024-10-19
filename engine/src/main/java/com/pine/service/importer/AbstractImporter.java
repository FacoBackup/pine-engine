package com.pine.service.importer;

import com.pine.Engine;
import com.pine.FSUtil;
import com.pine.injection.PInject;
import com.pine.messaging.Loggable;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.importer.data.AbstractImportData;
import com.pine.service.importer.metadata.AbstractResourceMetadata;

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
        if (FSUtil.write(data, getPathToFile(data))) {
            return new File(getPathToFile(data));
        }
        return null;
    }

    protected String getPathToFile(AbstractImportData data) {
        return importerService.getPathToFile(data.id, data.getResourceType());
    }
}
