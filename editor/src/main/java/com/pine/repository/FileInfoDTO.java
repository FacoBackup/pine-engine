package com.pine.repository;

import com.pine.view.RepeatingViewItem;

public record FileInfoDTO(
        String fileName,
        String fileSize,
        String fileType,
        String absolutePath,
        String hash,
        boolean isDirectory) implements RepeatingViewItem {
    @Override
    public String getKey() {
        return hash;
    }
}
