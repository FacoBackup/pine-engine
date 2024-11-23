package com.pine.repository.terrain;

import com.pine.injection.Disposable;
import com.pine.inspection.Color;
import com.pine.inspection.Inspectable;
import com.pine.inspection.InspectableField;

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
