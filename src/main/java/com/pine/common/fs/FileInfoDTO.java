package com.pine.common.fs;

public record FileInfoDTO(
        String fileName,
        long fileSize,
        FileType fileType,
        String absolutePath
) {
}
