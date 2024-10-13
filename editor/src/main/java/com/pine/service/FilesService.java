package com.pine.service;

import com.pine.Engine;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.repository.EditorRepository;
import com.pine.repository.fs.ResourceEntry;
import com.pine.repository.fs.ResourceEntryType;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.repository.streaming.StreamingRepository;

import java.io.File;

@PBean
public class FilesService {

    @PInject
    public Engine engine;

    @PInject
    public ProjectService projectService;

    @PInject
    public EditorRepository editorRepository;

    @PInject
    public StreamingRepository streamingRepository;

    public ResourceEntryType getType(StreamableResourceType type) {
        return switch (type) {
            case MATERIAL -> ResourceEntryType.MATERIAL;
            case SCENE -> ResourceEntryType.SCENE;
            case MESH -> ResourceEntryType.MESH;
            case TEXTURE -> ResourceEntryType.TEXTURE;
            case AUDIO -> ResourceEntryType.AUDIO;
        };
    }

    public void delete(ResourceEntry selected) {
        editorRepository.inspectFile = selected == editorRepository.inspectFile ? null : editorRepository.inspectFile;
        if (selected != editorRepository.rootDirectory) {
            streamingRepository.streamableResources.remove(selected.streamableResource);
            selected.parent.children.remove(selected);
            if (selected.type != ResourceEntryType.DIRECTORY) {
                new File(engine.getResourceTargetDirectory() + selected.streamableResource.pathToFile).delete();
            }
            projectService.saveSilently();
        }
    }
}
