package com.pine.service.resource;

import com.pine.messaging.Loggable;
import com.pine.service.resource.shader.GLSLType;
import com.pine.service.resource.shader.UniformDTO;
import org.lwjgl.opengl.GL46;

import java.util.Map;

public interface IShader extends Loggable {
    int getProgram();

    boolean isValid();

    default int compileShader(String shaderCode, int shaderType) {
        int shader = GL46.glCreateShader(shaderType);
        GL46.glShaderSource(shader, shaderCode);
        GL46.glCompileShader(shader);

        boolean compiled = GL46.glGetShaderi(shader, GL46.GL_COMPILE_STATUS) != 0;

        if (!compiled) {
            String error = GL46.glGetShaderInfoLog(shader);
            getLogger().error("Shader compilation error: {} {}", error, shaderCode);
        }

        return shader;
    }


    default UniformDTO addUniformDeclaration(String name, GLSLType type) {
        GL46.glUseProgram(getProgram());
        UniformDTO uniformDTO = new UniformDTO(type, name, GL46.glGetUniformLocation(getProgram(), name));
        getUniforms().put(name, uniformDTO);
        return uniformDTO;
    }

    Map<String, UniformDTO> getUniforms();
}
