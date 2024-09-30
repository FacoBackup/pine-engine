package com.pine.repository;

import com.pine.view.RepeatingViewItem;

import java.io.Serializable;

public record FileInfoDTO(
        String fileName,
        String fileSize,
        String fileType,
        String absolutePath,
        String hash,
        boolean isDirectory) implements RepeatingViewItem, Serializable {
    @Override
    public String getKey() {
        return hash;
    }
}
