package com.pine.repository;

import com.pine.PBean;
import com.pine.PInject;
import com.pine.service.resource.ResourceService;
import com.pine.service.resource.primitives.GLSLType;
import com.pine.service.resource.ssbo.SSBOCreationData;
import com.pine.service.resource.ssbo.ShaderStorageBufferObject;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static com.pine.Engine.MAX_ENTITIES;
import static com.pine.Engine.MAX_LIGHTS;

@PBean
public class CoreSSBORepository implements CoreRepository {
    private static final int TRANSFORMATION_PER_ENTITY = 9;
    private static final int INFO_PER_LIGHT = 21;
    private static final int ENTITY_BUFFER_SIZE = TRANSFORMATION_PER_ENTITY * MAX_ENTITIES;
    private static final int LIGHT_BUFFER_SIZE = MAX_LIGHTS * INFO_PER_LIGHT;

    @PInject
    public ResourceService resources;

    public final FloatBuffer lightSSBOState = MemoryUtil.memAllocFloat(LIGHT_BUFFER_SIZE);
    public final FloatBuffer transformationSSBOState = MemoryUtil.memAllocFloat(ENTITY_BUFFER_SIZE);

    public ShaderStorageBufferObject lightMetadataSSBO;
    public ShaderStorageBufferObject lightDescriptionSSBO;
    public ShaderStorageBufferObject transformationSSBO;
    public ShaderStorageBufferObject modelSSBO;

    @Override
    public void initialize() {
        transformationSSBO = (ShaderStorageBufferObject) resources.addResource(new SSBOCreationData(
                10,
                (long) ENTITY_BUFFER_SIZE * GLSLType.FLOAT.getSize()
        ));
        modelSSBO = (ShaderStorageBufferObject) resources.addResource(new SSBOCreationData(
                11,
                (long) MAX_ENTITIES * GLSLType.MAT_4.getSize()
        ));
        lightMetadataSSBO = (ShaderStorageBufferObject) resources.addResource(new SSBOCreationData(
                12,
                (long) MAX_LIGHTS * GLSLType.MAT_4.getSize()
        ));
        lightDescriptionSSBO = (ShaderStorageBufferObject) resources.addResource(new SSBOCreationData(
                13,
                (long) LIGHT_BUFFER_SIZE * GLSLType.FLOAT.getSize()
        ));
    }
}
