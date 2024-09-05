package com.pine.common.fs;

import com.pine.app.core.ui.view.tree.Tree;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.*;

@Repository
public class FSRepository {
    private final Map<String, List<FileInfoDTO>> filesByDirectory = new HashMap<>();
    private Tree directoryTree;

    public List<FileInfoDTO> readFiles(final String directory) {
        return filesByDirectory.computeIfAbsent(DigestUtils.sha1Hex(directory), this::readFilesInternal);
    }

    public void readFilesForcefully(final String directory) {
        final String key = DigestUtils.sha1Hex(directory);
        filesByDirectory.put(key, readFilesInternal(key));
    }

    public Map<String, List<FileInfoDTO>> getFilesByDirectory() {
        return filesByDirectory;
    }

    public Tree getDirectoryTree() {
        return directoryTree;
    }

    private List<FileInfoDTO> readFilesInternal(String path) {
        var data = new ArrayList<FileInfoDTO>();
        File directory = new File(path);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    data.add(new FileInfoDTO(
                            file.getName(),
                            file.getTotalSpace(),
                            FileType.valueOfEnum(file.getName()),
                            file.getAbsolutePath(),
                            DigestUtils.sha1Hex(file.getAbsolutePath()),
                            !file.isFile()
                    ));
                }
            }
        } else {
            System.out.println("The specified path is not a directory or does not exist.");
        }
        return data;
    }

    public Tree createDirectoryTree() {
        return this.directoryTree = new Tree("Directories", UUID.randomUUID().toString());
    }
}
