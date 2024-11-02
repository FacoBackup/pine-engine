package com.pine.service;

import com.pine.FSUtil;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.messaging.Loggable;
import com.pine.messaging.MessageRepository;
import com.pine.messaging.MessageSeverity;
import com.pine.repository.FSEntry;
import com.pine.repository.FilesRepository;
import com.pine.repository.streaming.StreamingRepository;
import com.pine.service.importer.ImporterService;
import com.pine.service.importer.metadata.AbstractResourceMetadata;

import java.io.File;
import java.util.Collection;
import java.util.List;

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
    public NativeDialogService nativeDialogService;

    @PInject
    public FilesRepository filesRepository;

    @PInject
    public MessageRepository messageRepository;

    public void deleteSelected(Collection<String> items) {
        deleteRecursively(items);
        projectService.saveSilently();
    }

    private void deleteRecursively(Collection<String> items) {
        for (String id : items) {
            var parentId = filesRepository.childParent.get(id);
            filesRepository.parentChildren.get(parentId).remove(id);
            filesRepository.childParent.remove(id);
            var entry = filesRepository.entry.get(id);
            if (entry.isDirectory()) {
                var children = filesRepository.parentChildren.get(id);
                deleteRecursively(children);
            } else {
                FSUtil.delete(importerService.getPathToMetadata(id, entry.getType()));
                FSUtil.delete(importerService.getPathToFile(id, entry.getType()));
                FSUtil.delete(importerService.getPathToFile(id, entry.getType()) + PREVIEW_EXT);
                filesRepository.byType.get(entry.type).remove(id);

                streamingRepository.discardedResources.put(entry.getId(), entry.getType());
                streamingRepository.loadedResources.remove(entry.getId());
                streamingRepository.toLoadResources.remove(entry.getId());
                streamingRepository.scheduleToLoad.remove(entry.getId());
            }
            filesRepository.parentChildren.remove(id);
            filesRepository.entry.remove(id);
        }
    }

    public void importFile(String currentDirectory) {
        List<String> paths = nativeDialogService.selectFile();
        if (paths.isEmpty()) {
            return;
        }
        importerService.importFiles(paths, response -> {
            filesRepository.isImporting = true;
            if (response.isEmpty()) {
                messageRepository.pushMessage("Could not import files: " + paths, MessageSeverity.ERROR);
            }

            for (var r : response) {
                createEntry(currentDirectory, r);
            }

            messageRepository.pushMessage(paths.size() + " files imported", MessageSeverity.SUCCESS);
            projectService.saveSilently();
            filesRepository.isImporting = false;
        });
    }

    public void createEntry(String currentDirectory, AbstractResourceMetadata r) {
        var entry = new FSEntry(new File(importerService.getPathToFile(r.id, r.getResourceType())), r.getResourceType(), r.id, r.name);
        filesRepository.parentChildren.get(currentDirectory).add(entry.id);
        filesRepository.entry.put(entry.id, entry);
        filesRepository.childParent.put(entry.id, currentDirectory);
        filesRepository.byType.get(entry.type).add(entry.id);
    }
}
