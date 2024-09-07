package com.pine.common.fs;

import com.pine.app.core.ui.view.RepeatingViewItem;

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
