package com.pine.service.resource;

import com.pine.FSUtil;
import com.pine.PBean;
import com.pine.PInject;
import com.pine.repository.CoreResourceRepository;
import com.pine.service.resource.primitives.GLSLType;
import com.pine.service.resource.resource.AbstractResourceService;
import com.pine.service.resource.resource.IResource;
import com.pine.service.resource.resource.ResourceType;
import com.pine.service.resource.shader.*;
import com.pine.type.BlockPoint;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL46;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@PBean
public class ShaderService extends AbstractResourceService<Shader, ShaderRuntimeData, ShaderCreationData> {
    private int currentSamplerIndex = 0;

    @PInject
    public UBOService uboService;

    @PInject
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
        GL46.glUseProgram(GL46.GL_NONE);
    }

    @Override
    protected IResource addInternal(ShaderCreationData data) {
        if (data.isLocalResource()) {
            String vertex = processShader(data.vertex());
            String frag = processShader(data.fragment());
            return create(getId(), new ShaderCreationData(vertex, frag));
        }
        return create(getId(), data);
    }

    private Shader create(String id, ShaderCreationData data) {
        var instance = new Shader(id, data);
        return (Shader) bindWithUBO(data.vertex() + "\n" + data.fragment(), instance);
    }

    @Nullable
    public IShader bindWithUBO(String code, IShader instance) {
        if (instance.isValid()) {
            if (code.contains(BlockPoint.CAMERA_VIEW.getBlockName()))
                uboService.bindWithShader(coreResources.cameraViewUBO, instance.getProgram());
            if (code.contains(BlockPoint.CAMERA_PROJECTION.getBlockName()))
                uboService.bindWithShader(coreResources.cameraProjectionUBO, instance.getProgram());
            if (code.contains(BlockPoint.FRAME_COMPOSITION.getBlockName()))
                uboService.bindWithShader(coreResources.frameCompositionUBO, instance.getProgram());
            if (code.contains(BlockPoint.LENS_PP.getBlockName()))
                uboService.bindWithShader(coreResources.lensPostProcessingUBO, instance.getProgram());
            if (code.contains(BlockPoint.SSAO.getBlockName()))
                uboService.bindWithShader(coreResources.ssaoUBO, instance.getProgram());
            if (code.contains(BlockPoint.UBER.getBlockName()))
                uboService.bindWithShader(coreResources.uberUBO, instance.getProgram());
            if (code.contains(BlockPoint.LIGHTS.getBlockName()))
                uboService.bindWithShader(coreResources.lightsUBO, instance.getProgram());
            return instance;
        }
        return null;
    }

    public String processShader(String file) {
        String input = new String(FSUtil.loadResource(file));
        return processIncludes(input);
    }

    public String processIncludes(String input) {
        final String pattern = "#include \".+\"";
        final Pattern regex = Pattern.compile(pattern);
        final Matcher matcher = regex.matcher(input);

        final var resultBuilder = new StringBuilder();
        while (matcher.find()) {
            String fileName = ShaderCreationData.LOCAL_SHADER + matcher.group(0).replaceAll("#include \"(./|../)", "").replaceAll("\"", "");
            String replacement = new String(FSUtil.loadResource(fileName));
            matcher.appendReplacement(resultBuilder, replacement);
        }
        matcher.appendTail(resultBuilder);

        String finalResult = resultBuilder.toString();
        if (finalResult.contains("#include ")) {
            finalResult = processIncludes(finalResult);
        }
        return finalResult;
    }

    @Override
    protected void removeInternal(Shader shader) {
        GL46.glDeleteProgram(shader.getProgram());
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.SHADER;
    }

    public void bindProgram(IShader shader) {
        this.currentSamplerIndex = 0;
        GL46.glUseProgram(shader.getProgram());
    }

    public void bindUniform(UniformDTO uniformDTO, Object data) {
        if (data == null) return;
        Integer uLocation = uniformDTO.getLocation();
        switch (uniformDTO.getType()) {
            case GLSLType.FLOAT:
                GL46.glUniform1fv(uLocation, (FloatBuffer) data);
                break;
            case GLSLType.VEC_2:
                GL46.glUniform2fv(uLocation, (FloatBuffer) data);
                break;

            case GLSLType.VEC_3:
                GL46.glUniform3fv(uLocation, (FloatBuffer) data);
                break;
            case GLSLType.VEC_4:
                GL46.glUniform4fv(uLocation, (FloatBuffer) data);
                break;
            case GLSLType.IVEC_2:
                GL46.glUniform2iv(uLocation, (IntBuffer) data);
                break;
            case GLSLType.IVEC_3:
                GL46.glUniform3iv(uLocation, (IntBuffer) data);
                break;
            case GLSLType.INT:
            case GLSLType.BOOL:
                GL46.glUniform1iv(uLocation, (IntBuffer) data);
                break;
            case GLSLType.MAT_3:
                GL46.glUniformMatrix3fv(uLocation, false, (FloatBuffer) data);
                break;
            case GLSLType.MAT_4:
                GL46.glUniformMatrix4fv(uLocation, false, (FloatBuffer) data);
                break;
            case GLSLType.SAMPLER_CUBE:
                GL46.glActiveTexture(GL46.GL_TEXTURE0 + currentSamplerIndex);
                GL46.glBindTexture(GL46.GL_TEXTURE_CUBE_MAP, (Integer) data);
                GL46.glUniform1i(uLocation, currentSamplerIndex);
                currentSamplerIndex++;
                break;
            case GLSLType.SAMPLER_2_D:
                GL46.glActiveTexture(GL46.GL_TEXTURE0 + currentSamplerIndex);
                GL46.glBindTexture(GL46.GL_TEXTURE_2D, (Integer) data);
                GL46.glUniform1i(uLocation, currentSamplerIndex);
                currentSamplerIndex++;
                break;
        }
    }
}
