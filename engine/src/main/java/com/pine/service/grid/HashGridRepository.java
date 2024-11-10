package com.pine.service.grid;

import com.pine.SerializableRepository;
import com.pine.injection.PBean;
import org.joml.Vector3f;

@PBean
public class HashGridRepository implements SerializableRepository {
    public final HashGrid hashGrid = new HashGrid();
    public final Vector3f previousCameraLocation = new Vector3f();
}
