package com.pine.service.resource;

import java.util.Collections;
import java.util.List;

public enum LocalResourceType {
    SHADER(),
    UBO(),
    FBO(),
    SSBO(),
    COMPUTE();

    private final List<String> fileExtensions;

    LocalResourceType(List<String> fileExtensions) {
        this.fileExtensions = fileExtensions;
    }

    LocalResourceType() {
        this.fileExtensions = Collections.emptyList();
    }

    public List<String> getFileExtensions() {
        return fileExtensions;
    }
}
