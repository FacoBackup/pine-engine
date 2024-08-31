package com.pine.common.fs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FSService {
    @Autowired
    private FSRepository repository;

    public List<FileInfoDTO> readFiles(final String path) {
        return repository.readFiles(path);
    }

    public String getUserRootPath() {
        return System.getProperty("user.home");
    }
}
