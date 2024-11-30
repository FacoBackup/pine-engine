package com.pine.engine.repository.terrain;

import com.pine.common.Icons;
import com.pine.common.inspection.Inspectable;
import com.pine.common.inspection.InspectableField;
import com.pine.engine.inspection.ResourceTypeField;
import com.pine.engine.repository.streaming.StreamableResourceType;
import org.joml.Vector3f;

public class MaterialLayer extends Inspectable {

    @InspectableField(label = "Channel", disabled = true)
    public final Vector3f channel;

    @InspectableField(label = "Layer name")
    public String name;

    @ResourceTypeField(type = StreamableResourceType.MATERIAL)
    @InspectableField(label = "Material")
    public String material;

    @InspectableField(label = "Weight")
    public float weight;

    public int channelFlag;

    public MaterialLayer(int channel) {
        channelFlag = channel;
        if (channel == 0) {
            this.channel = new Vector3f(1, 0, 0);
        } else if (channel == 1) {
            this.channel = new Vector3f(0, 1, 0);
        } else if (channel == 2) {
            this.channel = new Vector3f(0, 0, 1);
        } else {
            this.channel = new Vector3f(0, 0, 0);
        }
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public String getIcon() {
        return Icons.format_paint;
    }

    public String getFormattedName() {
        return name + "(" + channelFlag + ")";
    }
}
