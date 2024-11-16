package com.pine.component;

import com.pine.inspection.Inspectable;
import com.pine.inspection.InspectableField;
import com.pine.theme.Icons;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Entity extends Inspectable implements Serializable {
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    @InspectableField(label = "Name")
    public String name;

    @InspectableField(label = "Identifier", disabled = true)
    public final String id;

    @InspectableField(label = "Creation date", disabled = true)
    public final String creationDate = FORMATTER.format(new Date());
    public int renderIndex;

    public Entity(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String id(){
        return id;
    }

    public Entity() {
        id = UUID.randomUUID().toString();
        name = "New Entity (" + id.substring(0, 4) + ")";
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public String getIcon() {
        return Icons.inventory_2;
    }

    public Entity cloneEntity() {
        return new Entity(UUID.randomUUID().toString(), name + " (clone)");
    }
}
