package com.pine.component;

import com.pine.inspection.Inspectable;
import com.pine.inspection.InspectableField;
import com.pine.theme.Icons;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Entity extends Inspectable implements Serializable {
    @InspectableField(label = "Name")
    public String name = "New Entity";

    @InspectableField(label = "Transformation")
    public Transformation transformation = new Transformation(this, false);

    /**
     * Key: Class.simpleName
     * Value: Component instance
     */
    public final Map<ComponentType, AbstractComponent> components = new HashMap<>();
    private final String id;
    public final long creationDate = System.currentTimeMillis();
    public boolean visible = true;

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

    public Entity clone() {
        var clone = new Entity(UUID.randomUUID().toString(), name + " (clone)");
        clone.transformation = transformation.clone(clone, false);
        transformation.parent.children.add(clone.transformation);
        for(var component : components.values()) {
            clone.components.put(component.getType(), component.clone(clone));
        }
        return clone;
    }
}
