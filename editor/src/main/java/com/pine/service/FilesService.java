package com.pine.service;

import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.messaging.Loggable;
import com.pine.panels.files.FilesContext;
import com.pine.repository.FileMetadataRepository;
import com.pine.repository.fs.DirectoryEntry;
import com.pine.repository.fs.FileEntry;
import com.pine.repository.streaming.StreamingRepository;
import com.pine.service.importer.ImporterService;

import java.io.File;

import static com.pine.service.importer.impl.TextureImporter.PREVIEW_EXT;

@PBean
public class FilesService implements Loggable {

    @PInject
    public ImporterService importerService;

    @PInject
    public StreamingRepository streamingRepository;

    @PInject
    public ProjectService projectService;

    @PInject
    public FileMetadataRepository fileMetadataRepository;

    public void deleteSelected(FilesContext context) {
        for (String id : context.selected.keySet()) {
            FileEntry file = fileMetadataRepository.getFile(id);
            if (file != null) {
                delete(file);
                context.currentDirectory.files.remove(id);
            } else {
                deleteRecursively(context.currentDirectory.directories.get(id));
                context.currentDirectory.directories.remove(id);
            }
        }
        fileMetadataRepository.refresh();
        projectService.saveSilently();
    }

    private void deleteRecursively(DirectoryEntry directoryEntry) {
        for (var file : directoryEntry.files) {
            delete(fileMetadataRepository.getFile(file));
        }
        for (var dir : directoryEntry.directories.values()) {
            deleteRecursively(dir);
        }
    }

    private void delete(FileEntry file) {
        try {
            var metadataPath = file.path;
            var filePath = importerService.getPathToFile(file.getId(), file.metadata.getResourceType());
            var previewPath = filePath + PREVIEW_EXT;

            new File(metadataPath).delete();
            new File(filePath).delete();

            getLogger().warn("Deleted file {}", file.metadata.name);

            streamingRepository.failedStreams.put(file.getId(), file.metadata.getResourceType());
            streamingRepository.streamableResources.remove(file.getId());
            streamingRepository.loadedResources.remove(file.getId());
            streamingRepository.schedule.remove(file.getId());
            new File(previewPath).delete();
        } catch (Exception e) {
            getLogger().error("Error while deleting file {}", file.getId());
        }
    }
}
