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
    public Compute voxelRaymarchingCompute;


    @Override
    public void initialize() {
        voxelRaymarchingCompute = (Compute) resources.addResource(new ComputeCreationData(LOCAL_SHADER + "compute/VOXEL_RAY_MARCHING_COMPUTE.glsl").staticResource());
    }
}
