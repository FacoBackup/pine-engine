package com.pine.repository;

import com.pine.PBean;
import com.pine.SerializableRepository;
import com.pine.repository.fs.ResourceEntry;
import com.pine.repository.fs.ResourceEntryType;

import java.io.File;

@PBean
public class ContentBrowserRepository implements SerializableRepository {
    public ResourceEntry root;

    /**
     * Only executed when creating a new project
     * @param projectPath
     */
    public void initialize(String projectPath) {
        root = new ResourceEntry(ResourceEntryType.DIRECTORY, 0, projectPath + File.separator + "resources");
    }
}
