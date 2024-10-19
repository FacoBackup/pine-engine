package com.pine.repository;

import com.pine.Engine;
import com.pine.FSUtil;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.messaging.Loggable;
import com.pine.repository.fs.FileEntry;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.importer.metadata.AbstractResourceMetadata;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@PBean
public class FileMetadataRepository implements Loggable {
    @PInject
    public Engine engine;

    @PInject
    public EditorRepository editorRepository;

    private final Map<String, FileEntry> files = new HashMap<>();
    private final Map<StreamableResourceType, List<FileEntry>> byType = new HashMap<>();
    private boolean isLoading;

    public void refresh() {
        isLoading = true;
        new Thread(() -> {
            long start = System.currentTimeMillis();
            getLogger().warn("Refreshing files");
            List<String> filesToRead = readFiles();
            getLogger().warn("{} files found", filesToRead.size());
            putFiles(filesToRead);

            getLogger().warn("Files refreshed in {} ms", System.currentTimeMillis() - start);
            isLoading = false;
        }).start();
    }

    private @NotNull List<String> readFiles() {
        Path dir = Paths.get(engine.getMetadataDirectory());
        List<String> filesToRead = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path file : stream) {
                filesToRead.add(engine.getMetadataDirectory() + file.getFileName());
            }
        } catch (Exception e) {
            getLogger().error("Could not refresh files", e);
        }
        return filesToRead;
    }

    private void putFiles(List<String> filesToRead) {
        byType.clear();
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
    }

    public FileEntry getFile(String id) {
        return files.get(id);
    }

    public List<FileEntry> getAllByType(StreamableResourceType type) {
        return byType.getOrDefault(type, Collections.emptyList());
    }

    public boolean isLoading() {
        return isLoading;
    }
}
