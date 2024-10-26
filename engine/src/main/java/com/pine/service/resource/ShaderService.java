package com.pine.service.resource;

import com.pine.FSUtil;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.repository.core.CoreUBORepository;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.ShaderCreationData;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.streaming.ref.EnvironmentMapResourceRef;
import com.pine.service.streaming.ref.TextureResourceRef;
import com.pine.type.UBODeclaration;
import org.jetbrains.annotations.Nullable;
import org.joml.*;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

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
    FloatBuffer bufferFloat = MemoryUtil.memAllocFloat(1);
    IntBuffer bufferIntBool = MemoryUtil.memAllocInt(1);
    FloatBuffer bufferVec2 = MemoryUtil.memAllocFloat(2);
    FloatBuffer bufferVec3 = MemoryUtil.memAllocFloat(3);
    FloatBuffer bufferVec4 = MemoryUtil.memAllocFloat(4);
    IntBuffer bufferVec3i = MemoryUtil.memAllocInt(3);
    FloatBuffer bufferMat3 = MemoryUtil.memAllocFloat(9);
    FloatBuffer bufferMat4 = MemoryUtil.memAllocFloat(16);

    @Override
    protected void bindInternal(Shader instance) {
        if (currentShader == instance) {
            currentSamplerIndex = 0;
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

    public void bindFloat(float value, UniformDTO uniform) {
        bufferFloat.put(0, value);
        GL46.glUniform1fv(uniform.location, bufferFloat);
    }

    public void bindInt(int value, UniformDTO uniform) {
        bufferIntBool.put(0, value);
        GL46.glUniform1iv(uniform.location, bufferIntBool);
    }

    public void bindBoolean(boolean value, UniformDTO uniform) {
        bindInt(value ? 1 : 0, uniform);
    }

    public void bindVec2(Vector2f vec, UniformDTO uniform) {
        bufferVec2.put(0, vec.x);
        bufferVec2.put(1, vec.y);
        GL46.glUniform2fv(uniform.location, bufferVec2);
    }

    public void bindVec3(Vector3f vec, UniformDTO uniform) {
        bufferVec3.put(0, vec.x);
        bufferVec3.put(1, vec.y);
        bufferVec3.put(2, vec.z);
        GL46.glUniform3fv(uniform.location, bufferVec3);
    }

    public void bindVec4(Vector4f vec, UniformDTO uniform) {
       bufferVec4.put(0, vec.x);
       bufferVec4.put(1, vec.y);
       bufferVec4.put(2, vec.z);
       bufferVec4.put(3, vec.w);
        GL46.glUniform4fv(uniform.location, bufferVec4);
    }

    public void bindVec3i(Vector3i vec, UniformDTO uniform) {
        bufferVec3i.put(0, vec.x);
        bufferVec3i.put(1, vec.y);
        bufferVec3i.put(2, vec.z);
        GL46.glUniform3iv(uniform.location, bufferVec3i);
    }

    public void bindMat3(Matrix3f matrix, UniformDTO uniform) {
        bufferMat3.position(0);
        matrix.get(bufferMat3);
        GL46.glUniformMatrix3fv(uniform.location, false, bufferMat3);
    }

    public void bindMat4(Matrix4f matrix, UniformDTO uniform) {
        bufferMat4.position(0);
        matrix.get(bufferMat4);
        GL46.glUniformMatrix4fv(uniform.location, false, bufferMat4);
    }

    public void bindMat4Transposed(Matrix4f matrix, UniformDTO uniform) {
        bufferMat4.position(0);
        matrix.get(bufferMat4);
        GL46.glUniformMatrix4fv(uniform.location, true, bufferMat4);
    }

    public void bindSampler2d(TextureResourceRef sampler, UniformDTO uniform) {
        bindSampler2d(sampler.texture, uniform);
    }

    public void bindSampler2d(int sampler, UniformDTO uniform) {
        GL46.glActiveTexture(GL46.GL_TEXTURE0 + currentSamplerIndex);
        GL46.glBindTexture(GL46.GL_TEXTURE_2D, sampler);
        GL46.glUniform1i(uniform.location, currentSamplerIndex);
        currentSamplerIndex++;
    }

    public void bindSamplerCubeDirect(EnvironmentMapResourceRef sampler, int bindingPoint) {
        GL46.glActiveTexture(GL46.GL_TEXTURE0 + bindingPoint);
        GL46.glBindTexture(GL46.GL_TEXTURE_CUBE_MAP, sampler.texture);
    }

    public void bindSampler2dDirect(TextureResourceRef sampler, int bindingPoint) {
        bindSampler2dDirect(sampler.texture, bindingPoint);
    }

    public void bindSampler2dDirect(int sampler, int bindingPoint) {
        GL46.glActiveTexture(GL46.GL_TEXTURE0 + bindingPoint);
        GL46.glBindTexture(GL46.GL_TEXTURE_2D, sampler);
    }
}
