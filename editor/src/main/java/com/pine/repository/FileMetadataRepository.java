package com.pine.repository;

import com.pine.Engine;
import com.pine.FSUtil;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.messaging.Loggable;
import com.pine.repository.fs.DirectoryEntry;
import com.pine.repository.fs.FileEntry;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.importer.ImporterService;
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

    @PInject
    public ImporterService importerService;

    private final Map<String, FileEntry> files = new HashMap<>();
    private final Map<StreamableResourceType, List<FileEntry>> byType = new HashMap<>();
    private boolean isLoading;

    public void refresh() {
        isLoading = true;
        new Thread(() -> {
            files.clear();
            long start = System.currentTimeMillis();
            getLogger().warn("Refreshing files");
            List<String> filesToRead = readFiles();
            getLogger().warn("{} files found", filesToRead.size());
            putFiles(filesToRead);

            List<String> filesLinked = new ArrayList<>();
            collectLinkedFiles(editorRepository.root, filesLinked);
            List<String> missingFiles = new ArrayList<>();
            for (FileEntry file : files.values()) {
                if(!filesLinked.contains(file.getId())){
                    missingFiles.add(file.getId());
                }
            }

            editorRepository.root.files.addAll(missingFiles);

            getLogger().warn("Files refreshed in {} ms", System.currentTimeMillis() - start);
            isLoading = false;
        }).start();
    }

    private void collectLinkedFiles(DirectoryEntry root, List<String> filesLinked) {
        List<String> toRemove = new ArrayList<>();
        for(var file : root.files){
            if(!files.containsKey(file)){
                toRemove.add(file);
            }
        }
        toRemove.forEach(root.files::remove);
        filesLinked.addAll(root.files);
        root.directories.values().forEach(d -> collectLinkedFiles(d, filesLinked));
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
                var data = FSUtil.read(path, StreamableResourceType.metadataClassOf(path));
                if (data != null) {
                    File file = new File(importerService.getPathToFile(data.id, data.getResourceType()));
                    var fileData = new FileEntry(data, file);
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
