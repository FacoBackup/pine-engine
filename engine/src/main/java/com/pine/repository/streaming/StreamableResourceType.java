package com.pine.repository.streaming;

import com.pine.service.importer.data.*;
import com.pine.service.importer.metadata.*;
import com.pine.service.streaming.data.VoxelChunkStreamData;
import com.pine.theme.Icons;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public enum StreamableResourceType implements Serializable {
    ENVIRONMENT_MAP(Collections.emptyList(), false, false, Icons.panorama_photosphere, "Environment Map"),
    VOXEL_CHUNK(Collections.emptyList(), false, false, Icons.grid_view, "Voxel chunk"),
    SCENE(Collections.emptyList(), false, true, Icons.inventory_2, "Scene", SceneResourceMetadata.class, SceneImportData.class),
    MESH(List.of("gltf", "glb", "fbx", "obj", "blend"), false, true, Icons.category, "Mesh", MeshResourceMetadata.class, MeshImportData.class),
    TEXTURE(List.of("png", "jpeg", "jpg"), false, false, Icons.texture, "Texture", TextureResourceMetadata.class, TextureImportData.class),
    AUDIO(List.of("wav"), false, false, Icons.audio_file, "Audio"),
    MATERIAL(Collections.emptyList(), true, true, Icons.format_paint, "Material", MaterialResourceMetadata.class, MaterialImportData.class);

    private final List<String> fileExtensions;
    private final boolean mutable;
    private final String icon;
    private final String title;
    private final boolean readable;
    private final Class<? extends AbstractResourceMetadata> metadataClazz;
    private final Class<? extends AbstractImportData> dataClazz;

    StreamableResourceType(List<String> fileExtensions, boolean mutable, boolean readable, String icon, String title,
                           Class<? extends AbstractResourceMetadata> metadataClazz,
                           Class<? extends AbstractImportData> dataClazz) {
        this.fileExtensions = fileExtensions;
        this.readable = readable;
        this.mutable = mutable;
        this.icon = icon;
        this.title = title;
        this.metadataClazz = metadataClazz;
        this.dataClazz = dataClazz;
    }

    StreamableResourceType(List<String> fileExtensions, boolean mutable, boolean readable, String icon, String title) {
        this(fileExtensions, mutable, readable, icon, title, null, null);
    }

    public static Class<? extends AbstractResourceMetadata> metadataClassOf(String path) {
        for (var value : StreamableResourceType.values()) {
            if (path.endsWith("." + value.name())) {
                return value.metadataClazz;
            }
        }
        return null;
    }

    public static Class<? extends AbstractImportData> dataClassOf(String path) {
        for (var value : StreamableResourceType.values()) {
            if (path.endsWith("." + value.name())) {
                return value.dataClazz;
            }
        }
        return null;
    }

    public Class<? extends AbstractResourceMetadata> getMetadataClazz() {
        return metadataClazz;
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
