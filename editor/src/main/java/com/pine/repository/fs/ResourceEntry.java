package com.pine.repository.fs;


import com.pine.repository.streaming.AbstractStreamableResource;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ResourceEntry implements Serializable {
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public String name;
    public final ResourceEntryType type;
    public final float size;
    public final long creationDate = System.currentTimeMillis();
    public final String path;
    public List<ResourceEntry> children = new ArrayList<>();
    public ResourceEntry parent;
    public final String sizeText;
    public AbstractStreamableResource<?> streamableResource;
    public String creationDateString;

    public ResourceEntry(String name, ResourceEntryType type, float size, String path, ResourceEntry parent, AbstractStreamableResource<?> streamableResource) {
        creationDateString = FORMATTER.format(new Date(creationDate));
        this.name = name;
        this.streamableResource = streamableResource;
        this.type = type;
        this.size = size;
        this.path = path;
        this.parent = parent;
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
}
