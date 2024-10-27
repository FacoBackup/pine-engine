package com.pine.repository.rendering;

import com.pine.component.Entity;
import com.pine.component.TransformationComponent;
import com.pine.service.streaming.ref.MaterialResourceRef;
import com.pine.service.streaming.ref.MeshResourceRef;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class RenderingRequest implements Serializable {
    public transient MeshResourceRef mesh;
    public transient MaterialResourceRef material;

    public final TransformationComponent transformationComponent;
    public List<TransformationComponent> transformationComponents;
    public int renderIndex;
    public Entity entity;

    public RenderingRequest(MeshResourceRef mesh, TransformationComponent transformationComponent) {
        this(mesh, transformationComponent, Collections.emptyList());
    }

    public RenderingRequest(MeshResourceRef mesh, TransformationComponent transformationComponent, List<TransformationComponent> transformationComponents) {
        this.mesh = mesh;
        this.transformationComponent = transformationComponent;
        this.transformationComponents = transformationComponents;
    }
}
