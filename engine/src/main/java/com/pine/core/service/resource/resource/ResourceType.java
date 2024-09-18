package com.pine.core.service.resource.resource;

import java.util.List;

public enum ResourceType {
    MESH(List.of("gltf", "glb")),
    TEXTURE(List.of("png", "jpeg", "jpg")),
    AUDIO(List.of("wav")),
    SHADER(List.of("vert", "frag", "glsl")),
    UBO(List.of()),
    FBO(List.of());

    private final List<String> fileExtensions;

    ResourceType(List<String> fileExtensions) {
        this.fileExtensions = fileExtensions;
    }

    public List<String> getFileExtensions() {
        return fileExtensions;
    }
}
