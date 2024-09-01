package com.pine.common.fs;

import jakarta.annotation.Nullable;
import org.springframework.lang.NonNull;

import java.util.Objects;

public enum FileType {
    MESH("Mesh", ".pmesh"),
    TEXTURE("Texture", ".ptex"),
    MATERIAL("Material", ".pmat"),
    AUDIO("Audio", ".paud"),
    OTHER("Other", null);

    private final String name;
    private final String extension;

    FileType(String name, String extension) {

        this.name = name;
        this.extension = extension;
    }

    @NonNull
    public static FileType valueOfEnum(String extension) {
        for (FileType ft : FileType.values()) {
            if (Objects.equals(ft.extension, extension)) {
                return ft;
            }
        }
        return OTHER;
    }

    @Nullable
    public String getExtension() {
        return extension;
    }

    public String getName() {
        return name;
    }
}
