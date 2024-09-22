package com.pine.repository;

import com.pine.PBean;
import com.pine.PInject;
import com.pine.service.resource.ResourceService;
import com.pine.service.resource.primitives.GLSLType;
import com.pine.service.resource.ssbo.SSBOCreationData;
import com.pine.service.resource.ssbo.ShaderStorageBufferObject;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

@PBean
public class CoreSSBORepository implements CoreRepository {
    public static final int TRANSFORMATION_PER_ENTITY = 9;
    private static final int MAX_ENTITIES = 2000;
    private static final int BUFFER_SIZE = TRANSFORMATION_PER_ENTITY * MAX_ENTITIES;

    @PInject
    public ResourceService resources;

    public final FloatBuffer transformationSSBOState = MemoryUtil.memAllocFloat(BUFFER_SIZE);
    public ShaderStorageBufferObject transformationSSBO;
    public ShaderStorageBufferObject modelSSBO;

    @Override
    public void initialize() {
        transformationSSBO = (ShaderStorageBufferObject) resources.addResource(new SSBOCreationData(
                10,
                (long) BUFFER_SIZE * GLSLType.FLOAT.getSize()
        ));
        modelSSBO = (ShaderStorageBufferObject) resources.addResource(new SSBOCreationData(
                11,
                (long) MAX_ENTITIES * GLSLType.MAT_4.getSize()
        ));
    }
}
