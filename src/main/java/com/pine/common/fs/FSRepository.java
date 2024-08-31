package com.pine.common.fs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class FSRepository {
    private final Map<String, List<FileInfoDTO>> filesByDirectory = new HashMap<>();

    public Map<String, List<FileInfoDTO>> getFilesByDirectory() {
        return filesByDirectory;
    }

    public List<FileInfoDTO> readFiles(final String directory) {
        return filesByDirectory.computeIfAbsent(directory, this::readFilesInternal);
    }

    private List<FileInfoDTO> readFilesInternal(String path) {
        var data = new ArrayList<FileInfoDTO>();
        File directory = new File(path);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        data.add(new FileInfoDTO(file.getName(), file.getTotalSpace(), FileType.valueOfEnum(file.getName()),file.getAbsolutePath()));
                    }
                }
            }
        } else {
            System.out.println("The specified path is not a directory or does not exist.");
        }
        return data;
    }
}
