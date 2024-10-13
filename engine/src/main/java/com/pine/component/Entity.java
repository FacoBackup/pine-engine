package com.pine.component;

import com.pine.inspection.Inspectable;
import com.pine.inspection.MutableField;
import com.pine.theme.Icons;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Entity extends Inspectable implements Serializable {
    @MutableField(label = "Name")
    public String name = "New Entity";

    @MutableField(label = "Transformation")
    public final Transformation transformation = new Transformation(this, false);

    /**
     * Key: Class.simpleName
     * Value: Component instance
     */
    public final Map<String, AbstractComponent> components = new HashMap<>();
    public final String id;
    public final long creationDate = System.currentTimeMillis();
    public boolean visible = true;
    public boolean selected;

    public Entity(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Entity() {
        this.id = UUID.randomUUID().toString().replaceAll("-", "");
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public String getIcon() {
        return Icons.inventory_2;
    }
}
