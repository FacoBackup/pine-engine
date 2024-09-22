package com.pine.repository;

import com.pine.PBean;
import com.pine.PInject;
import com.pine.service.resource.ResourceService;
import com.pine.service.resource.compute.Compute;
import com.pine.service.resource.compute.ComputeCreationData;

import static com.pine.service.resource.shader.ShaderCreationData.LOCAL_SHADER;

@PBean
public class CoreComputeRepository implements CoreRepository {
    @PInject
    public ResourceService resources;

    public Compute transformationCompute;

    @Override
    public void initialize() {
        transformationCompute = (Compute) resources.addResource(new ComputeCreationData(LOCAL_SHADER + "compute/TRANSFORMATION_COMPUTE.glsl").staticResource());
    }
}
