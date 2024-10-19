package com.pine.repository;

import com.pine.Engine;
import com.pine.FSUtil;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.messaging.Loggable;
import com.pine.repository.fs.FileEntry;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.importer.metadata.AbstractResourceMetadata;

import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@PBean
public class FileMetadataRepository implements Loggable {
    @PInject
    public Engine engine;
    private final Map<String, FileEntry> files = new HashMap<>();
    private final Map<StreamableResourceType, List<FileEntry>> byType;

    public void refresh() {
        Path dir = Paths.get(engine.getMetadataDirectory());
        List<String> filesToRead = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path file : stream) {
                filesToRead.add(String.valueOf(file.getFileName()));
            }
        } catch (Exception e) {
            getLogger().error("Could not refresh files", e);
        }

        byType.clear();
        filesToRead.clear();
        new Thread(() -> {
            for (var path : filesToRead) {
                try {
                    Object data = FSUtil.read(path);
                    if (data != null) {
                        File file = new File(path);
                        var fileData = new FileEntry((AbstractResourceMetadata) data, file);
                        byType.putIfAbsent(fileData.metadata.getResourceType(), new ArrayList<>());
                        byType.get(fileData.metadata.getResourceType()).add(fileData);
                        files.put(fileData.getId(), fileData);
                    }
                } catch (Exception e) {
                    getLogger().error("Could not read file {}", path, e);
                }
            }
        }).start();
    }

    public FileEntry getFile(String id) {
        return files.get(id);
    }

    public List<FileEntry> getAllByType(StreamableResourceType type) {
        return byType.get(type);
    }
}
