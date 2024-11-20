package com.pine.repository;

import com.pine.inspection.Color;
import com.pine.inspection.Inspectable;
import com.pine.inspection.InspectableField;
import com.pine.inspection.ResourceTypeField;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.theme.Icons;
import org.joml.Vector2f;

import java.util.UUID;

public class FoliageInstance extends Inspectable {
    public int count = 0;
    public int offset = 0;

    @InspectableField(label = "id", disabled = true)
    public final String id = UUID.randomUUID().toString();

    @InspectableField(label = "Name")
    public String name;
    @ResourceTypeField(type = StreamableResourceType.MATERIAL)
    @InspectableField(label = "Material")
    public String material;
    @ResourceTypeField(type = StreamableResourceType.MESH)
    @InspectableField(label = "Mesh")
    public String mesh;

    @InspectableField(label = "Color ID", disabled = true)
    public final Color color = new Color();
    @InspectableField(label = "Max distance from camera", min = 1)
    public int maxDistanceFromCamera = 100;
    @InspectableField(label = "Max instances per cell (squared) ", min = 1, max = 15)
    public int maxIterations = 5;
    @InspectableField(label = "Instance offset scale")
    public Vector2f instanceOffset = new Vector2f(5);

    public FoliageInstance(int i) {
        name = "Foliage instance " + i;
        color.x = (i >> 16) & 0xFF;
        color.y = (i >> 8) & 0xFF;
        color.z = i & 0xFF;
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public String getIcon() {
        return Icons.forest;
    }
}
