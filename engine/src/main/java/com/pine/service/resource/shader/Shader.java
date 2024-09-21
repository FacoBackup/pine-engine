package com.pine.service.resource.shader;

import com.pine.service.resource.primitives.GLSLType;
import com.pine.service.resource.resource.AbstractResource;
import com.pine.service.resource.resource.ResourceType;
import org.lwjgl.opengl.GL46;

import java.util.HashMap;
import java.util.Map;

public class Shader extends AbstractResource {
    private int program;
    private final Map<String, UniformDTO> uniforms = new HashMap<>();
    private boolean valid = true;

    public Shader(String id, ShaderCreationData dto) {
        super(id);
        try {
            program = GL46.glCreateProgram();
            prepareShaders(dto.vertex(), dto.fragment());
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

    private void prepareShaders(final String vertex, final String fragment) {
        int vertexShader = compileShader(vertex, GL46.GL_VERTEX_SHADER);
        int fragmentShader = compileShader(fragment, GL46.GL_FRAGMENT_SHADER);

        GL46.glAttachShader(program, vertexShader);
        GL46.glAttachShader(program, fragmentShader);

        GL46.glLinkProgram(program);
        GL46.glFlush();

        getLogger().info("Shader status {}", GL46.glGetError());
    }

    private int compileShader(String shaderCode, int shaderType) {
        int shader = GL46.glCreateShader(shaderType);
        GL46.glShaderSource(shader, shaderCode);
        GL46.glCompileShader(shader);

        boolean compiled = GL46.glGetShaderi(shader, GL46.GL_COMPILE_STATUS) != 0;

        if (!compiled) {
            String error = GL46.glGetShaderInfoLog(shader);
            System.err.println("Shader compilation error: " + error);
        }

        return shader;
    }

    public UniformDTO addUniformDeclaration(String name, GLSLType type) {
        GL46.glUseProgram(program);
        UniformDTO uniformDTO = new UniformDTO(type, name, GL46.glGetUniformLocation(program, name));
        uniforms.put(name, uniformDTO);
        return uniformDTO;
    }

    public Map<String, UniformDTO> getUniforms() {
        return uniforms;
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.SHADER;
    }
}

