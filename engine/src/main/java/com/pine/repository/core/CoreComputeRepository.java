package com.pine.repository.core;

import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.service.resource.ResourceService;
import com.pine.service.resource.compute.Compute;
import com.pine.service.resource.compute.ComputeCreationData;

import static com.pine.service.resource.shader.ShaderCreationData.LOCAL_SHADER;

@PBean
public class CoreComputeRepository implements CoreRepository {
    @PInject
    public ResourceService resources;
    public Compute voxelizationCompute;


    @Override
    public void initialize() {
        voxelizationCompute = (Compute) resources.addResource(new ComputeCreationData(LOCAL_SHADER + "compute/VOXELIZATION_COMPUTE.glsl").staticResource());
    }
}
