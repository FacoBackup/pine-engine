package com.pine.common.fs;

import com.pine.common.Loggable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class FSService implements Loggable {
    @Autowired
    private FSRepository repository;

    public List<FileInfoDTO> readFiles(final String path) {
        return repository.readFiles(path);
    }

    @Async
    public void refreshFiles(String directory, Runnable afterProcessing) {
        repository.getFilesByDirectory().clear();
        repository.readFilesForcefully(directory);
        afterProcessing.run();
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
        } else {
            System.out.println(directory.getAbsolutePath() + " is not a directory.");
        }

        return directories;
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
