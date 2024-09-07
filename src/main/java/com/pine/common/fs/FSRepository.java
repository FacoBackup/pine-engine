package com.pine.common.fs;

import com.pine.app.core.ui.view.tree.Tree;
import com.pine.common.Loggable;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.*;

@Repository
public class FSRepository implements Loggable {
    private final Map<String, List<FileInfoDTO>> filesByDirectory = new HashMap<>();

    public List<FileInfoDTO> readFiles(final String directory) {
        return filesByDirectory.computeIfAbsent(DigestUtils.sha1Hex(directory), (_) -> readFilesInternal(directory));
    }

    public void readFilesForcefully(final String directory) {
        final String key = DigestUtils.sha1Hex(directory);
        filesByDirectory.put(key, readFilesInternal(directory));
    }

    public Map<String, List<FileInfoDTO>> getFilesByDirectory() {
        return filesByDirectory;
    }

    private List<FileInfoDTO> readFilesInternal(String path) {
        var data = new ArrayList<FileInfoDTO>();
        File directory = new File(path);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    String key = DigestUtils.sha1Hex(file.getAbsolutePath());
                    if (file.isDirectory()) {
                        data.add(new FileInfoDTO(
                                file.getName(),
                                "",
                                "",
                                file.getAbsolutePath(),
                                key,
                                true
                        ));
                    } else {
                        long fileSizeInBytes = file.length();
                        String sizeUnit = "mb";
                        double fileSize = (double) fileSizeInBytes / (1024 * 1024);
                        if (fileSize > 1000) {
                            fileSize = fileSize / 1024;
                            sizeUnit = "gb";
                        }
                        if (fileSize < 1) {
                            fileSize = (double) fileSizeInBytes / 1024;
                            sizeUnit = "kb";
                        }
                        String[] split = file.getName().split("\\.");
                        data.add(new FileInfoDTO(
                                split[0],
                                String.format("%.2f", fileSize) + sizeUnit,
                                split[1],
                                file.getAbsolutePath(),
                                key,
                                false
                        ));
                    }
                }
            }
        } else {
            getLogger().error("The specified path is not a directory or does not exist: {}", path);
        }
        return data;
    }
}
