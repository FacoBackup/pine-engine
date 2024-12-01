package com.pine.engine.repository.streaming;

import com.pine.common.Icons;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public enum StreamableResourceType implements Serializable {
    ENVIRONMENT_MAP(Collections.emptyList(), false, false, Icons.panorama_photosphere, "Environment Map"),
    VOXEL_CHUNK(Collections.emptyList(), false, false, Icons.grid_view, "Voxel chunk"),
    SCENE(Collections.emptyList(), false, true, Icons.inventory_2, "Scene"),
    MESH(List.of("gltf", "glb", "fbx", "obj", "blend"), false, true, Icons.category, "Mesh"),
    TEXTURE(List.of("png", "jpeg", "jpg"), false, false, Icons.texture, "Texture"),
    AUDIO(List.of("wav"), false, false, Icons.audio_file, "Audio"),
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
