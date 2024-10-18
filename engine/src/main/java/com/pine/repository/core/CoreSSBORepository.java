package com.pine.repository.core;

import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.service.resource.ResourceService;
import com.pine.service.resource.shader.GLSLType;
import com.pine.service.resource.ssbo.SSBOCreationData;
import com.pine.service.resource.ssbo.ShaderStorageBufferObject;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static com.pine.Engine.MAX_ENTITIES;
import static com.pine.Engine.MAX_LIGHTS;

@PBean
public class CoreSSBORepository implements CoreRepository {
    public static final int MAX_INFO_PER_LIGHT = 32;
    private static final int ENTITY_BUFFER_SIZE = 16 * MAX_ENTITIES;
    private static final int LIGHT_BUFFER_SIZE = MAX_LIGHTS * MAX_INFO_PER_LIGHT;
    private static final long MAX_VOXEL_QUANTITY = 4_000_000;

    @PInject
    public ResourceService resources;

    public final FloatBuffer lightSSBOState = MemoryUtil.memAllocFloat(LIGHT_BUFFER_SIZE);
    public final FloatBuffer transformationSSBOState = MemoryUtil.memAllocFloat(ENTITY_BUFFER_SIZE);

    public ShaderStorageBufferObject octreeSSBO;
    public ShaderStorageBufferObject lightMetadataSSBO;
    public ShaderStorageBufferObject transformationSSBO;

    @Override
    public void initialize() {
        transformationSSBO = (ShaderStorageBufferObject) resources.addResource(new SSBOCreationData(
                10,
                (long) ENTITY_BUFFER_SIZE * GLSLType.FLOAT.getSize()
        ));
        lightMetadataSSBO = (ShaderStorageBufferObject) resources.addResource(new SSBOCreationData(
                11,
                (long) LIGHT_BUFFER_SIZE * GLSLType.FLOAT.getSize()
        ));
        octreeSSBO = (ShaderStorageBufferObject) resources.addResource(new SSBOCreationData(
                12,
                MAX_VOXEL_QUANTITY * GLSLType.INT.getSize()
        ));
    }
}
