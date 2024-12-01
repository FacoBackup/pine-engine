package com.pine.engine.repository.terrain;

import com.pine.common.injection.Disposable;
import com.pine.common.inspection.Color;
import com.pine.common.inspection.Inspectable;
import com.pine.common.inspection.InspectableField;

import java.util.UUID;

public abstract class AbstractDataInstance extends Inspectable implements Disposable {

    @InspectableField(label = "id", disabled = true)
    public final String id = UUID.randomUUID().toString();

    @InspectableField(label = "Name")
    public String name;
    @InspectableField(label = "Color ID")
    public final Color color = new Color();

    public AbstractDataInstance(int i) {
        color.x = ((i >> 16) & 0xFF )/255f;
        color.y = ((i >> 8) & 0xFF)/255f;
        color.z = (i & 0xFF)/255f;
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public void dispose() {
    }
}
