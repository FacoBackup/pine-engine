package com.pine.common.fs;

import com.pine.app.core.ui.view.RepeatingViewItem;

public record FileInfoDTO(
        String fileName,
        long fileSize,
        FileType fileType,
        String absolutePath,
        String hash
) implements RepeatingViewItem {
    @Override
    public String getKey() {
        return hash;
    }
}
