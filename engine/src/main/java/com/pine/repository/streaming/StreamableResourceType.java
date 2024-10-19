package com.pine.repository.streaming;

import com.pine.theme.Icons;

import java.util.Collections;
import java.util.List;

public enum StreamableResourceType {
    SCENE(Collections.emptyList(), false, true, Icons.inventory_2, "Scene"),
    MESH(List.of("gltf", "glb", "fbx", "obj", "blend"), false, true, Icons.category, "Mesh"),
    TEXTURE(List.of("png", "jpeg", "jpg"), false, false, Icons.texture, "Audio"),
    AUDIO(List.of("wav"), false, false, Icons.audio_file, "Texture"),
    MATERIAL(Collections.emptyList(), true, true, Icons.format_paint, "Material");

    private final List<String> fileExtensions;
    private final boolean mutable;
    private final String icon;
    private final String title;
    private final boolean readable;

    StreamableResourceType(List<String> fileExtensions, boolean mutable, boolean readable, String icon, String title) {
        this.fileExtensions = fileExtensions;
        this.readable = readable;
        this.mutable = mutable;
        this.icon = icon;
        this.title = title;
    }

    public List<String> getFileExtensions() {
        return fileExtensions;
    }

    public boolean isMutable() {
        return mutable;
    }

    public boolean isReadable() {
        return readable;
    }

    public String getTitle() {
        return title;
    }

    public String getIcon() {
        return icon;
    }
}
