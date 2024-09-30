package com.pine.repository;

import com.pine.PBean;
import com.pine.PInject;
import com.pine.service.resource.ResourceService;

@PBean
public class CoreComputeRepository implements CoreRepository {
    @PInject
    public ResourceService resources;


    @Override
    public void initialize() {
//        transformationCompute = (Compute) resources.addResource(new ComputeCreationData(LOCAL_SHADER + "compute/TRANSFORMATION_COMPUTE.glsl").staticResource());
    }
}
