package com.pine.repository.streaming;

import java.util.Collections;
import java.util.List;

public enum StreamableResourceType {
    SCENE(Collections.emptyList()),
    MESH(List.of("gltf", "glb", "fbx", "obj", "blend")),
    TEXTURE(List.of("png", "jpeg", "jpg")),
    AUDIO(List.of("wav")),
    MATERIAL(Collections.emptyList());

    private final List<String> fileExtensions;

    StreamableResourceType(List<String> fileExtensions) {
        this.fileExtensions = fileExtensions;
    }

    StreamableResourceType() {
        this.fileExtensions = Collections.emptyList();
    }

    public List<String> getFileExtensions() {
        return fileExtensions;
    }
}
