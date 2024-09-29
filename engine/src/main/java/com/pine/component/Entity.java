package com.pine.component;

import com.pine.inspection.Inspectable;
import com.pine.inspection.MutableField;
import com.pine.theme.Icons;

import java.io.Serializable;
import java.util.*;

public class Entity extends Inspectable implements Serializable {
    @MutableField(label = "Name")
    public String name = "New Entity";

    @MutableField(label = "Is collection")
    public boolean collection = false;

    /**
     * Key: Class.simpleName
     * Value: Component instance
     */
    public final Map<String, EntityComponent> components = new HashMap<>();
    public final List<Entity> children = new LinkedList<>();
    public final String id;
    public Entity parent;
    public final long creationDate = System.currentTimeMillis();
    public boolean visible = true;

    public transient boolean isSearchMatch;
    public String searchMatchedWith;
    public boolean pinned;
    public boolean selected;

    public Entity(String id, String name) {
        this.id = id;
        parent = null;
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
