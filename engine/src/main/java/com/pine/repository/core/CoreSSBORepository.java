package com.pine.repository.core;

import com.pine.injection.PBean;
import com.pine.service.resource.shader.GLSLType;
import com.pine.service.resource.ssbo.SSBOCreationData;
import com.pine.service.resource.ssbo.ShaderStorageBufferObject;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static com.pine.Engine.MAX_LIGHTS;

@PBean
public class CoreSSBORepository implements CoreRepository {
    public static final int MAX_INSTANCING = 500_000;
    public static final int MAX_INFO_PER_LIGHT = 16;
    private static final int LIGHT_BUFFER_SIZE = MAX_LIGHTS * MAX_INFO_PER_LIGHT;

    public final FloatBuffer lightSSBOState = MemoryUtil.memAllocFloat(LIGHT_BUFFER_SIZE);

    public ShaderStorageBufferObject lightMetadataSSBO;
    public ShaderStorageBufferObject foliageTransformationSSBO;

    @Override
    public void initialize() {
        foliageTransformationSSBO = new ShaderStorageBufferObject(new SSBOCreationData(
                13,
                (long) MAX_INSTANCING * GLSLType.FLOAT.getSize() * 16
        ));

        lightMetadataSSBO = new ShaderStorageBufferObject(new SSBOCreationData(
                11,
                (long) LIGHT_BUFFER_SIZE * GLSLType.FLOAT.getSize()
        ));
    }

    @Override
    public void dispose() {
        lightMetadataSSBO.dispose();
        foliageTransformationSSBO.dispose();
    }
}
