package com.pine.repository.rendering;

import com.pine.component.Entity;
import com.pine.component.Transformation;
import com.pine.service.streaming.ref.MaterialResourceRef;
import com.pine.service.streaming.ref.MeshResourceRef;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class RenderingRequest implements Serializable {
    public transient MeshResourceRef mesh;
    public transient MaterialResourceRef material;

    public final Transformation transformation;
    public List<Transformation> transformations;
    public int renderIndex;
    public Entity entity;

    public RenderingRequest(MeshResourceRef mesh, Transformation transformation) {
        this(mesh, transformation, Collections.emptyList());
    }

    public RenderingRequest(MeshResourceRef mesh, Transformation transformation, List<Transformation> transformations) {
        this.mesh = mesh;
        this.transformation = transformation;
        this.transformations = transformations;
    }
}
