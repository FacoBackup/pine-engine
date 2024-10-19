package com.pine.service;

import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.messaging.Loggable;
import com.pine.repository.fs.FileEntry;
import com.pine.repository.streaming.StreamingRepository;
import com.pine.service.importer.ImporterService;

import java.io.File;

@PBean
public class FilesService implements Loggable {

    @PInject
    public ImporterService importerService;

    @PInject
    public StreamingRepository streamingRepository;

    public void delete(FileEntry file) {
        try {
            if (new File(file.path).delete()) {
                if(new File(importerService.getPathToFile(file.getId(), file.metadata.getResourceType())).delete()){
                    getLogger().warn("Deleted file {}", file.metadata.name);
                }
                streamingRepository.failedStreams.put(file.getId(), file.metadata.getResourceType());
                streamingRepository.streamableResources.remove(file.getId());
                streamingRepository.loadedResources.remove(file.getId());
                streamingRepository.schedule.remove(file.getId());
            }
        } catch (Exception e) {
            getLogger().error("Error while deleting file {}", file.getId());
        }
    }
}
