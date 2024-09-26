package com.pine.service.resource.resource;

import java.util.Collections;
import java.util.List;

public enum ResourceType {
    PRIMITIVE(List.of("gltf", "glb")),
    TEXTURE(List.of("png", "jpeg", "jpg")),
    AUDIO(List.of("wav")),
    SHADER(List.of("vert", "frag")),
    UBO(),
    FBO(),
    SSBO(),
    COMPUTE();

    private final List<String> fileExtensions;

    ResourceType(List<String> fileExtensions) {
        this.fileExtensions = fileExtensions;
    }

    ResourceType() {
        this.fileExtensions = Collections.emptyList();
    }

    public List<String> getFileExtensions() {
        return fileExtensions;
    }
}
