package com.pine.service;

import com.pine.injection.PBean;
import com.pine.messaging.Loggable;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@PBean
public class FSService implements Loggable {
    public List<File> readFilesInDirectory(String path) {
        File directory = new File(path);
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                return Arrays.asList(files);
            }
        }
        return Collections.emptyList();
    }

    public void createDirectory(String dirPath) {
        try {
            Path path = Paths.get(dirPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (Exception e) {
            getLogger().error("Error creating directory: {}", e.getMessage(), e);
        }
    }

    public boolean exists(String path) {
        return new File(path).exists();
    }
}
