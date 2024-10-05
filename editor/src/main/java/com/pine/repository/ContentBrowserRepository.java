package com.pine.repository;

import com.pine.Engine;
import com.pine.SerializableRepository;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.injection.PostCreation;
import com.pine.repository.fs.ResourceEntry;
import com.pine.repository.fs.ResourceEntryType;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.repository.streaming.StreamingRepository;
import com.pine.service.ProjectService;

import java.io.File;

@PBean
public class ContentBrowserRepository implements SerializableRepository {
    public ResourceEntry root;

    @PInject
    public transient ProjectService projectService;

    @PInject
    public transient Engine engine;

    @PInject
    public transient StreamingRepository streamingRepository;

    @PostCreation(order = Integer.MAX_VALUE)
    public void initialize() {
        if (root == null) {
            root = new ResourceEntry("Content Browser", ResourceEntryType.DIRECTORY, 0, "", null, null);
            streamingRepository.streamableResources.forEach(s -> {
                var file = new File(engine.getResourceTargetDirectory() + s.pathToFile);
                root.children.add(new ResourceEntry(s.name, getType(s.getResourceType()), file.length(), file.getAbsolutePath().replace(projectService.getProjectDirectory(), ""), root, s));
            });
        }
    }

    public ResourceEntryType getType(StreamableResourceType type) {
        return switch (type) {
            case MESH -> ResourceEntryType.MESH;
            case TEXTURE -> ResourceEntryType.TEXTURE;
            case AUDIO -> ResourceEntryType.AUDIO;
        };
    }

    public void delete(ResourceEntry selected) {
        if (selected != root) {
            selected.parent.children.remove(selected);
            if (selected.type != ResourceEntryType.DIRECTORY) {
                new File(engine.getResourceTargetDirectory() + selected.streamableResource.pathToFile).delete();
            }
        }
    }
}
