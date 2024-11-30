package com.pine.editor.repository;


import com.pine.common.Icons;
import com.pine.common.inspection.Inspectable;
import com.pine.common.inspection.InspectableField;
import com.pine.engine.repository.streaming.StreamableResourceType;
import com.pine.engine.service.importer.ImporterService;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FSEntry extends Inspectable {
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public final String id;

    @InspectableField(label = "Name")
    public String name;

    public final StreamableResourceType type;
    public final String sizeText;
    public String creationDateString;
    public final boolean isDirectory;
    public boolean isHovered = false;

    public FSEntry(File file, StreamableResourceType type, String id, String name) {
        creationDateString = FORMATTER.format(new Date(file.lastModified()));
        this.id = id;
        this.name = name;
        this.type = type;
        sizeText = ImporterService.getSizeWithUnit(file.length());
        isDirectory = false;
    }

    public FSEntry(String name, String id){
        this.id = id;
        this.name = name;
        this.type = null;
        sizeText = null;
        isDirectory = true;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public String getId() {
        return id;
    }

    public StreamableResourceType getType() {
        return type;
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public String getIcon() {
        return isDirectory ? Icons.folder : Icons.file_open;
    }
}
