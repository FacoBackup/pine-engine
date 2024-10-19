package com.pine.repository.fs;


import com.pine.service.importer.metadata.AbstractResourceMetadata;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileEntry implements IEntry {
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public AbstractResourceMetadata metadata;
    public final float size;
    public final String path;
    public final String sizeText;
    public String creationDateString;

    public FileEntry(AbstractResourceMetadata metadata, File file) {
        creationDateString = FORMATTER.format(new Date(file.lastModified()));
        this.metadata = metadata;
        this.size = file.length();
        this.path = file.getAbsolutePath();
        String sizeUnit = "mb";
        double fileSize = (double) size / (1024 * 1024);
        if (fileSize > 1000) {
            fileSize = fileSize / 1024;
            sizeUnit = "gb";
        }
        if (fileSize < 1) {
            fileSize = (double) size / 1024;
            sizeUnit = "kb";
        }
        sizeText = String.format("%.2f", fileSize) + sizeUnit;
    }

    @Override
    public String getId() {
        return metadata.id;
    }
}
