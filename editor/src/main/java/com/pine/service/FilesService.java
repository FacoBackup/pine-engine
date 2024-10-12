package com.pine.service;

import com.pine.Engine;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.repository.EditorRepository;
import com.pine.repository.fs.ResourceEntry;
import com.pine.repository.fs.ResourceEntryType;
import com.pine.repository.streaming.StreamableResourceType;

import java.io.File;

@PBean
public class FilesService {

    @PInject
    public Engine engine;

    @PInject
    public EditorRepository editorRepository;

    public ResourceEntryType getType(StreamableResourceType type) {
        return switch (type) {
            case MESH -> ResourceEntryType.MESH;
            case TEXTURE -> ResourceEntryType.TEXTURE;
            case AUDIO -> ResourceEntryType.AUDIO;
        };
    }

    public void delete(ResourceEntry selected) {
        if (selected != editorRepository.rootDirectory) {
            selected.parent.children.remove(selected);
            if (selected.type != ResourceEntryType.DIRECTORY) {
                new File(engine.getResourceTargetDirectory() + selected.streamableResource.pathToFile).delete();
            }
        }
    }
}
