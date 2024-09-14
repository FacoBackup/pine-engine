package com.pine.engine.core.service.resource;

import com.pine.common.fs.FSUtil;
import com.pine.engine.core.EngineDependency;
import com.pine.engine.core.EngineInjectable;
import com.pine.engine.core.repository.CoreResourceRepository;
import com.pine.engine.core.service.resource.primitives.GLSLType;
import com.pine.engine.core.service.resource.resource.AbstractResourceService;
import com.pine.engine.core.service.resource.resource.IResource;
import com.pine.engine.core.service.resource.resource.ResourceType;
import com.pine.engine.core.service.resource.shader.Shader;
import com.pine.engine.core.service.resource.shader.ShaderCreationData;
import com.pine.engine.core.service.resource.shader.ShaderRuntimeData;
import com.pine.engine.core.service.resource.shader.UniformDTO;
import com.pine.engine.core.type.CoreUBOName;
import org.lwjgl.opengl.GL46;

import java.io.File;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.pine.engine.Engine.GLSL_VERSION;

@EngineInjectable
public class ShaderService extends AbstractResourceService<Shader, ShaderRuntimeData, ShaderCreationData> {
    private int currentSamplerIndex = 0;
    private String currentShaderId;

    @EngineDependency
    public UBOService uboService;

    @EngineDependency
    public CoreResourceRepository coreResources;

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
    protected IResource addInternal(ShaderCreationData data) {
        if (data.absoluteId() != null) {
            String vertex = GLSL_VERSION + "\n" + processShader(data.vertex());
            String frag = GLSL_VERSION + "\n" + processShader(data.fragment());
            return create(data.absoluteId(), new ShaderCreationData(vertex, frag, null));
        }
        return create(getId(), data);
    }

    private Shader create(String id, ShaderCreationData data) {
        var instance = new Shader(id, data);
        if (instance.isValid()) {
            if (data.fragment().contains(CoreUBOName.CAMERA_VIEW.getBlockName()) || data.vertex().contains(CoreUBOName.CAMERA_VIEW.getBlockName()))
                uboService.bindWithShader(coreResources.cameraViewUBO, instance.getProgram());
            if (data.fragment().contains(CoreUBOName.CAMERA_PROJECTION.getBlockName()) || data.vertex().contains(CoreUBOName.CAMERA_PROJECTION.getBlockName()))
                uboService.bindWithShader(coreResources.cameraProjectionUBO, instance.getProgram());
            if (data.fragment().contains(CoreUBOName.FRAME_COMPOSITION.getBlockName()) || data.vertex().contains(CoreUBOName.FRAME_COMPOSITION.getBlockName()))
                uboService.bindWithShader(coreResources.frameCompositionUBO, instance.getProgram());
            if (data.fragment().contains(CoreUBOName.LENS_PP.getBlockName()) || data.vertex().contains(CoreUBOName.LENS_PP.getBlockName()))
                uboService.bindWithShader(coreResources.lensPostProcessingUBO, instance.getProgram());
            if (data.fragment().contains(CoreUBOName.SSAO.getBlockName()) || data.vertex().contains(CoreUBOName.SSAO.getBlockName()))
                uboService.bindWithShader(coreResources.ssaoUBO, instance.getProgram());
            if (data.fragment().contains(CoreUBOName.UBER.getBlockName()) || data.vertex().contains(CoreUBOName.UBER.getBlockName()))
                uboService.bindWithShader(coreResources.uberUBO, instance.getProgram());
            if (data.fragment().contains(CoreUBOName.LIGHTS.getBlockName()) || data.vertex().contains(CoreUBOName.LIGHTS.getBlockName()))
                uboService.bindWithShader(coreResources.lightsUBO, instance.getProgram());
            return instance;
        }
        return null;
    }

    protected String processShader(String file) {
        String input = new String(FSUtil.loadResource(file));
        return process(input);
    }

    private String process(String input) {
        final String pattern = "#include \"./(\\w+\\.glsl)\"";
        final Pattern regex = Pattern.compile(pattern);
        final Matcher matcher = regex.matcher(input);

        final var resultBuilder = new StringBuilder();
        while (matcher.find()) {
            String fileName = "shaders" + File.separator + matcher.group(1);
            String replacement = new String(FSUtil.loadResource(fileName));
            matcher.appendReplacement(resultBuilder, replacement);
        }
        matcher.appendTail(resultBuilder);

        String finalResult = resultBuilder.toString();
        if (finalResult.contains("#include ")) {
            finalResult = process(finalResult);
        }
        return finalResult;
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
            case GLSLType.FLOAT:
                if (data instanceof FloatBuffer) {
                    GL46.glUniform1fv(uLocation, (FloatBuffer) data);
                }
                break;
            case GLSLType.VEC_2:
                if (data instanceof FloatBuffer) {
                    GL46.glUniform2fv(uLocation, (FloatBuffer) data);
                }
                break;

            case GLSLType.VEC_3:
                if (data instanceof FloatBuffer) {
                    GL46.glUniform3fv(uLocation, (FloatBuffer) data);
                }
                break;
            case GLSLType.VEC_4:
                if (data instanceof FloatBuffer) {
                    GL46.glUniform4fv(uLocation, (FloatBuffer) data);
                }
                break;
            case GLSLType.IVEC_2:
                if (data instanceof IntBuffer) {
                    GL46.glUniform2iv(uLocation, (IntBuffer) data);
                }
                break;
            case GLSLType.IVEC_3:
                if (data instanceof IntBuffer) {
                    GL46.glUniform3iv(uLocation, (IntBuffer) data);
                }
                break;
            case GLSLType.BOOL:
                if (data instanceof IntBuffer) {
                    GL46.glUniform1iv(uLocation, (IntBuffer) data);
                }
                break;
            case GLSLType.MAT_3:
                if (data instanceof FloatBuffer) {
                    GL46.glUniformMatrix3fv(uLocation, false, (FloatBuffer) data);
                }
                break;

            case GLSLType.MAT_4:
                if (data instanceof FloatBuffer) {
                    GL46.glUniformMatrix4fv(uLocation, false, (FloatBuffer) data);
                }
                break;
            case GLSLType.SAMPLER_CUBE:
                if (data instanceof Integer) {
                    GL46.glActiveTexture(GL46.GL_TEXTURE0 + currentSamplerIndex);
                    GL46.glBindTexture(GL46.GL_TEXTURE_CUBE_MAP, (Integer) data);
                    GL46.glUniform1i(uLocation, currentSamplerIndex);
                    currentSamplerIndex++;
                }
                break;
            case GLSLType.SAMPLER_2_D:
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
