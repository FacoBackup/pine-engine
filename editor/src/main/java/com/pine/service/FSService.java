package com.pine.service;

import com.pine.Loggable;
import com.pine.PBean;
import com.pine.PInject;
import com.pine.repository.FSRepository;
import com.pine.repository.FileInfoDTO;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@PBean
public class FSService implements Loggable {
    @PInject
    public FSRepository repository;

    public List<FileInfoDTO> readFiles(final String path) {
        return repository.readFiles(path);
    }

    public void refreshFiles(String directory, Runnable afterProcessing) {
        if(directory != null) {
            new Thread(() -> {
                repository.getFilesByDirectory().clear();
                repository.readFilesForcefully(directory);
                afterProcessing.run();
            }).start();
        }
    }

    public static String getUserRootPath() {
        return System.getProperty("user.home");
    }

    public void write(String file, String filePath) {
        Path path = Paths.get(filePath);
        try {
            Files.write(path, file.getBytes());
            getLogger().info("Successfully wrote to the file: {}", filePath);
        } catch (IOException e) {
            getLogger().error("Error writing to file: {}", e.getMessage(), e);
        }
    }

    public List<String> readDirectories(String path) {
        List<String> directories = new ArrayList<>();
        File directory = new File(path);
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        directories.add(file.getAbsolutePath());
                    }
                }
            }
        }
        return directories;
    }

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

    public boolean containsFile(String directoryPath, String fileName) {
        File directory = new File(directoryPath);
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().equals(fileName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void deleteDirectory(String path) {
        try {
            new File(path).delete();
        } catch (Exception e) {
            getLogger().error("Error deleting directory: {}", e.getMessage(), e);
        }
    }

    public boolean exists(String path) {
        return new File(path).exists();
    }

    public String getParentDir(String directory) {
        Path path = Paths.get(directory);
        Path parentPath = path.getParent();

        if (parentPath != null) {
            return parentPath.toString();
        }
        return null;
    }
}
