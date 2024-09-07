package com.pine.engine.service;

import com.pine.common.fs.FSUtil;
import com.pine.engine.resource.AbstractResourceService;
import com.pine.engine.resource.IResource;
import com.pine.engine.resource.ResourceType;
import com.pine.engine.service.primitives.GLSLType;
import com.pine.engine.service.primitives.shader.Shader;
import com.pine.engine.service.primitives.shader.ShaderCreationDTO;
import com.pine.engine.service.primitives.shader.ShaderRuntimeData;
import com.pine.engine.service.primitives.shader.UniformDTO;
import org.lwjgl.opengl.GL46;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Objects;

public class ShaderService extends AbstractResourceService<Shader, ShaderRuntimeData, ShaderCreationDTO> {
    private int currentSamplerIndex = 0;
    private String currentShaderId;

    @Override
    protected void bindInternal(Shader instance, ShaderRuntimeData data) {
        bindProgram(instance);
        var uniforms = instance.getUniforms();
        for (var entry : data.getUniformData().entrySet()) {
            bindUniform(uniforms.get(entry.getKey()), entry.getValue());
        }
    }

    @Override
    protected void bindInternal(Shader instance) {
        bindProgram(instance);
    }

    @Override
    public void unbind() {
        GL46.glUseProgram(0);
    }

    @Override
    protected IResource addInternal(ShaderCreationDTO data) {
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
    protected void removeInternal(Shader shader) {
        GL46.glDeleteProgram(shader.getProgram());
        if (Objects.equals(currentShaderId, shader.getId())) {
            currentShaderId = null;
        }
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.SHADER;
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

    @Override
    public void onInitialize() {
        //        addResource(new ShaderCreationDTO("shaders/SPRITE.vert", "shaders/SPRITE.frag", "sprite"));
        //        addResource(new ShaderCreationDTO("shaders/V_BUFFER.vert", "shaders/V_BUFFER.frag", "visibility"));
        //        addResource(new ShaderCreationDTO("shaders/QUAD.vert", "shaders/TO_SCREEN.frag", "toScreen"));
        //        addResource(new ShaderCreationDTO("shaders/QUAD.vert", "shaders/BILINEAR_DOWNSCALE.glsl", "downscale"));
        //        addResource(new ShaderCreationDTO("shaders/QUAD.vert", "shaders/BILATERAL_BLUR.glsl", "bilateralBlur"));
        //        addResource(new ShaderCreationDTO("shaders/QUAD.vert", "shaders/BOKEH.frag", "bokeh"));
        //        addResource(new ShaderCreationDTO("shaders/CUBEMAP.vert", "shaders/IRRADIANCE_MAP.frag", "irradiance"));
        //        addResource(new ShaderCreationDTO("shaders/CUBEMAP.vert", "shaders/PREFILTERED_MAP.frag", "prefiltered"));
        //        addResource(new ShaderCreationDTO("shaders/QUAD.vert", "shaders/SSGI.frag", "ssgi"));
        //        addResource(new ShaderCreationDTO("shaders/QUAD.vert", "shaders/MOTION_BLUR.frag", "mb"));
        //        addResource(new ShaderCreationDTO("shaders/QUAD.vert", "shaders/SSAO.frag", "ssao"));
        //        addResource(new ShaderCreationDTO("shaders/QUAD.vert", "shaders/BOX-BLUR.frag", "boxBlur"));
        //        addResource(new ShaderCreationDTO("shaders/SHADOWS.vert", "shaders/DIRECTIONAL_SHADOWS.frag", "directShadows"));
        //        addResource(new ShaderCreationDTO("shaders/SHADOWS.vert", "shaders/OMNIDIRECTIONAL_SHADOWS.frag", "omniDirectShadows"));
        //        addResource(new ShaderCreationDTO("shaders/QUAD.vert", "shaders/FRAME_COMPOSITION.frag", "composition"));
        //        addResource(new ShaderCreationDTO("shaders/QUAD.vert", "shaders/BRIGHTNESS_FILTER.frag", "bloom"));
        //        addResource(new ShaderCreationDTO("shaders/QUAD.vert", "shaders/LENS_POST_PROCESSING.frag", "lens"));
        //        addResource(new ShaderCreationDTO("shaders/QUAD.vert", "shaders/GAUSSIAN.frag", "gaussian"));
        //        addResource(new ShaderCreationDTO("shaders/QUAD.vert", "shaders/UPSAMPLE_TENT.glsl", "upSampling"));
        //        addResource(new ShaderCreationDTO("shaders/QUAD.vert", "shaders/ATMOSPHERE.frag", "atmosphere"));
    }
}
