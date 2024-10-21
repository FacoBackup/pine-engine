package com.pine.service.importer;

import com.pine.Engine;
import com.pine.FSUtil;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.injection.PostCreation;
import com.pine.messaging.Loggable;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.importer.data.AbstractImportData;
import com.pine.service.importer.metadata.AbstractResourceMetadata;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Responsible for parsing data and storing it locally while registering the StreamableResource on StreamableResourceRepository
 */
@PBean
public class ImporterService implements Loggable {

    @PInject
    public List<AbstractImporter> importersList;

    @PInject
    public Engine engine;

    private final Map<StreamableResourceType, AbstractImporter> importers = new HashMap<>();

    @PostCreation
    public void initialize() {
        for (var importer : importersList) {
            importers.put(importer.getResourceType(), importer);
        }
    }

    public void importFiles(List<String> paths, Consumer<List<AbstractResourceMetadata>> callback) {
        new Thread(() -> {
            List<AbstractImportData> importedFiles = new ArrayList<>();
            for (String path : paths) {
                if (path == null) {
                    continue;
                }
                final String extension = path.substring(path.lastIndexOf(".") + 1);
                for (AbstractImporter i : importersList) {
                    if (i.getResourceType().getFileExtensions().contains(extension)) {
                        importedFiles.addAll(i.importFile(path));
                    }
                }
            }

            List<AbstractResourceMetadata> response = new ArrayList<>();

            for (var imported : importedFiles) {
                try {
                    getLogger().warn("Persisting {} of type {}", imported.id, imported.getResourceType());
                    AbstractImporter importer = importers.get(imported.getResourceType());
                    if (importer != null) {
                        AbstractResourceMetadata metadata = importer.persist(imported);
                        metadata.setSize(new File(getPathToFile(metadata.id, metadata.getResourceType())).length());
                        if (FSUtil.write(metadata, getPathToMetadata(metadata.id))) {
                            response.add(metadata);
                        }
                    }
                } catch (Exception e) {
                    getLogger().error("Error while persisting file", e);
                }
            }

            callback.accept(response);
        }).start();
    }

    public String getPathToMetadata(String id) {
        return engine.getMetadataDirectory() + id + ".dat";
    }

    public AbstractImportData readFile(AbstractResourceMetadata metadata) {
        if (!metadata.getResourceType().isReadable()) {
            return null;
        }
        String path = getPathToFile(metadata.id, metadata.getResourceType());
        return (AbstractImportData) FSUtil.read(path);
    }

    public String getPathToFile(String id, StreamableResourceType type) {
        return engine.getResourceDirectory() + id + "." + type.name();
    }

    public static String getSizeWithUnit(float size) {
        String sizeUnit = "mb";
        double fileSize = (double) size / (1024 * 1024);
        if (fileSize > 1000) {
            fileSize = fileSize / 1024;
            sizeUnit = "gb";
        }
        if (fileSize < 1) {
            fileSize = (double) size / 1024;
            sizeUnit = "kb";
        }
        return String.format("%.2f", fileSize) + sizeUnit;
    }
}
