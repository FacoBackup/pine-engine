package com.pine.component;

import com.pine.inspection.Inspectable;
import com.pine.inspection.InspectableField;
import com.pine.theme.Icons;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Entity extends Inspectable implements Serializable {
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    @InspectableField(label = "Name")
    public String name = "New Entity";

    @InspectableField(label = "Identifier", disabled = true)
    public final String id;

    @InspectableField(label = "Creation date", disabled = true)
    public final String creationDate = FORMATTER.format(new Date());

    /**
     * Key: Class.simpleName
     * Value: Component instance
     */
    public final Map<ComponentType, AbstractComponent> components = new HashMap<>();

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
        var clone = new Entity(UUID.randomUUID().toString(), name + " (clone)");

        for(var component : components.values()) {
            clone.components.put(component.getType(), component.cloneComponent(clone));
        }
        return clone;
    }
}
