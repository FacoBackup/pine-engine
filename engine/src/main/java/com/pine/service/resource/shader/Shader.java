package com.pine.service.resource.shader;

import com.pine.service.resource.resource.AbstractResource;
import com.pine.service.resource.resource.ResourceType;
import org.lwjgl.opengl.GL46;

import java.util.HashMap;
import java.util.Map;

import static com.pine.Engine.GLSL_VERSION;

public class Shader extends AbstractResource implements IShader {
    private int program;
    private final Map<String, UniformDTO> uniforms = new HashMap<>();
    private boolean valid = true;

    public Shader(String id, ShaderCreationData dto) {
        super(id);
        try {
            program = GL46.glCreateProgram();
            prepareShaders(GLSL_VERSION + "\n" + dto.vertex(), GLSL_VERSION + "\n" + dto.fragment());
        } catch (Exception ex) {
            getLogger().error("Error while creating shader", ex);
            valid = false;
        }
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
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
        int error = GL46.glGetError();
        if(error != 0) {
            getLogger().error("Shader status {} {}", error, GL46.glGetString(error));
        }
    }

    @Override
    public Map<String, UniformDTO> getUniforms() {
        return uniforms;
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.SHADER;
    }
}

