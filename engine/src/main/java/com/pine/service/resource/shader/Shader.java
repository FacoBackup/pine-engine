package com.pine.service.resource.shader;

import com.pine.GLSLVersion;
import com.pine.service.resource.IResource;
import org.lwjgl.opengl.GL46;

import java.util.HashMap;
import java.util.Map;

public class Shader implements IResource {
    private int program;
    private final Map<String, UniformDTO> uniforms = new HashMap<>();
    private boolean valid = true;

    public Shader(String vertex, String fragment) {
        try {
            program = GL46.glCreateProgram();
            prepareShadersDefault(GLSLVersion.getVersion() + "\n" + vertex, GLSLVersion.getVersion() + "\n" + fragment);
        } catch (Exception ex) {
            getLogger().error("Error while creating shader", ex);
            valid = false;
        }
    }

    public Shader(String compute) {
        try {
            program = GL46.glCreateProgram();
            prepareShadersCompute(GLSLVersion.getVersion() + "\n" + compute);
        } catch (Exception ex) {
            getLogger().error("Error while creating shader", ex);
            valid = false;
        }
    }

    public boolean isValid() {
        return valid;
    }

    public int getProgram() {
        return program;
    }

    private void prepareShadersDefault(final String vertex, final String fragment) {
        int vertexShader = compileShader(vertex, GL46.GL_VERTEX_SHADER);
        int fragmentShader = compileShader(fragment, GL46.GL_FRAGMENT_SHADER);

        GL46.glAttachShader(program, vertexShader);
        GL46.glAttachShader(program, fragmentShader);

        GL46.glLinkProgram(program);
        GL46.glFlush();
    }

    private void prepareShadersCompute(final String code) {
        int computeShader = compileShader(code, GL46.GL_COMPUTE_SHADER);

        GL46.glAttachShader(program, computeShader);

        GL46.glLinkProgram(program);
        GL46.glFlush();
    }

    public Map<String, UniformDTO> getUniforms() {
        return uniforms;
    }

    @Override
    public void dispose() {
        GL46.glDeleteProgram(program);
    }

    private int compileShader(String shaderCode, int shaderType) {
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

    public UniformDTO addUniformDeclaration(String name) {
        GL46.glUseProgram(getProgram());
        UniformDTO uniformDTO = new UniformDTO(name, GL46.glGetUniformLocation(getProgram(), name));
        getUniforms().put(name, uniformDTO);
        return uniformDTO;
    }
}

