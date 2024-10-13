package com.pine.service.resource;

import com.pine.FSUtil;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.repository.core.CoreUBORepository;
import com.pine.service.resource.shader.GLSLType;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.ShaderCreationData;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.type.UBODeclaration;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL46;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@PBean
public class ShaderService extends AbstractResourceService<Shader, ShaderCreationData> {
    private int currentSamplerIndex = 0;
    private Shader currentShader;

    @PInject
    public UBOService uboService;

    @PInject
    public CoreUBORepository uboRepository;

    @Override
    protected void bindInternal(Shader instance) {
        if (currentShader == instance) {
            return;
        }
        currentShader = instance;
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
            if (code.contains(UBODeclaration.CAMERA_VIEW.getBlockName()))
                uboService.bindWithShader(uboRepository.cameraViewUBO, instance.getProgram());
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
    public LocalResourceType getResourceType() {
        return LocalResourceType.SHADER;
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
