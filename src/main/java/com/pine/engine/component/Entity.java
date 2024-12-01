package com.pine.engine.component;

import com.pine.common.Icons;
import com.pine.common.inspection.Color;
import com.pine.common.inspection.Inspectable;
import com.pine.common.inspection.InspectableField;
import com.pine.editor.core.UIUtil;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Entity extends Inspectable implements Serializable {
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    @InspectableField(label = "Name")
    public String name;

    @InspectableField(label = "Color (container only)")
    public final Color color = new Color(UIUtil.DIRECTORY_COLOR.x, UIUtil.DIRECTORY_COLOR.y, UIUtil.DIRECTORY_COLOR.z);

    @InspectableField(label = "Identifier", disabled = true)
    public final String id;

    @InspectableField(label = "Creation date", disabled = true)
    public final String creationDate = FORMATTER.format(new Date());

    @InspectableField(label = "Render index", disabled = true)
    public int renderIndex;

    public boolean isContainer = false;

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
        return "Entity metadata";
    }

    @Override
    public String getIcon() {
        return Icons.inventory_2;
    }

    public Entity cloneEntity() {
        return new Entity(UUID.randomUUID().toString(), name + " (clone)");
    }

    public boolean isContainer() {
        return isContainer;
    }

    public void setContainer(boolean container) {
        isContainer = container;
    }
}
