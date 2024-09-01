package com.pine.engine.service;

import com.pine.common.fs.FSUtil;
import com.pine.common.resource.IResource;
import com.pine.common.resource.ResourceService;
import com.pine.engine.service.primitives.GLSLType;
import com.pine.engine.service.primitives.shader.Shader;
import com.pine.engine.service.primitives.shader.ShaderCreationDTO;
import com.pine.engine.service.primitives.shader.ShaderRuntimeData;
import com.pine.engine.service.primitives.shader.UniformDTO;
import org.lwjgl.opengl.GL46;
import org.springframework.stereotype.Repository;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Objects;

@Repository
public class ShaderService implements ResourceService<Shader, ShaderRuntimeData, ShaderCreationDTO> {
    private int currentSamplerIndex = 0;
    private String currentShaderId;

    @Override
    public void bind(Shader instance, ShaderRuntimeData data) {
        bindProgram(instance);
        var uniforms = instance.getUniforms();
        for (var entry : data.getUniformData().entrySet()) {
            bindUniform(uniforms.get(entry.getKey()), entry.getValue());
        }
    }

    @Override
    public void bind(Shader instance) {
        bindProgram(instance);
    }

    @Override
    public void unbind() {
        GL46.glUseProgram(0);
    }

    @Override
    public IResource add(ShaderCreationDTO data) {
        if (data.absoluteId() != null) {
            String vertex = new String(FSUtil.loadResource(data.vertex()));
            String frag = new String(FSUtil.loadResource(data.fragment()));
            return create(data.absoluteId(), new ShaderCreationDTO(vertex, frag, null));
        }
        return create(getId(), data);
    }

    private Shader create(String id, ShaderCreationDTO data) {
        var instance = new Shader(id, data);
        if (instance.isValid()) {
            // TODO - BIND UBOs
            return instance;
        }
        return null;
    }

    @Override
    public void remove(Shader shader) {
        GL46.glDeleteProgram(shader.getProgram());
        if (Objects.equals(currentShaderId, shader.getId())) {
            currentShaderId = null;
        }
    }

    public void bindProgram(Shader shader) {
        if (Objects.equals(currentShaderId, shader.getId())) {
            return;
        }

        this.currentSamplerIndex = 0;
        GL46.glUseProgram(shader.getProgram());
        currentShaderId = shader.getId();
    }

    public void bindUniform(UniformDTO uniformDTO, Object data) {
        if (data == null) return;
        Integer uLocation = uniformDTO.getLocation();
        switch (uniformDTO.getType()) {
            case GLSLType.f:
                if (data instanceof FloatBuffer) {
                    GL46.glUniform1fv(uLocation, (FloatBuffer) data);
                }
                break;
            case GLSLType.vec2:
                if (data instanceof FloatBuffer) {
                    GL46.glUniform2fv(uLocation, (FloatBuffer) data);
                }
                break;

            case GLSLType.vec3:
                if (data instanceof FloatBuffer) {
                    GL46.glUniform3fv(uLocation, (FloatBuffer) data);
                }
                break;
            case GLSLType.vec4:
                if (data instanceof FloatBuffer) {
                    GL46.glUniform4fv(uLocation, (FloatBuffer) data);
                }
                break;
            case GLSLType.ivec2:
                if (data instanceof IntBuffer) {
                    GL46.glUniform2iv(uLocation, (IntBuffer) data);
                }
                break;
            case GLSLType.ivec3:
                if (data instanceof IntBuffer) {
                    GL46.glUniform3iv(uLocation, (IntBuffer) data);
                }
                break;
            case GLSLType.bool:
                if (data instanceof IntBuffer) {
                    GL46.glUniform1iv(uLocation, (IntBuffer) data);
                }
                break;
            case GLSLType.mat3:
                if (data instanceof FloatBuffer) {
                    GL46.glUniformMatrix3fv(uLocation, false, (FloatBuffer) data);
                }
                break;

            case GLSLType.mat4:
                if (data instanceof FloatBuffer) {
                    GL46.glUniformMatrix4fv(uLocation, false, (FloatBuffer) data);
                }
                break;
            case GLSLType.samplerCube:
                if (data instanceof Integer) {
                    GL46.glActiveTexture(GL46.GL_TEXTURE0 + currentSamplerIndex);
                    GL46.glBindTexture(GL46.GL_TEXTURE_CUBE_MAP, (Integer) data);
                    GL46.glUniform1i(uLocation, currentSamplerIndex);
                    currentSamplerIndex++;
                }
                break;
            case GLSLType.sampler2D:
                if (data instanceof Integer) {
                    GL46.glActiveTexture(GL46.GL_TEXTURE0 + currentSamplerIndex);
                    GL46.glBindTexture(GL46.GL_TEXTURE_2D, (Integer) data);
                    GL46.glUniform1i(uLocation, currentSamplerIndex);
                    currentSamplerIndex++;
                }
                break;
        }
    }
}