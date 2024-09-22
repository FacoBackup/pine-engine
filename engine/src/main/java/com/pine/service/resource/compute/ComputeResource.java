package com.pine.service.resource.compute;

import com.pine.service.resource.resource.AbstractResource;
import com.pine.service.resource.resource.ResourceType;
import com.pine.service.resource.shader.Shader;
import com.pine.service.resource.shader.UniformDTO;
import org.lwjgl.opengl.GL46;

import java.util.HashMap;
import java.util.Map;

import static com.pine.Engine.GLSL_VERSION;

public class ComputeResource extends AbstractResource implements Shader {
    private int program;
    private final Map<String, UniformDTO> uniforms = new HashMap<>();
    private boolean valid = true;

    public ComputeResource(String id, ComputeCreationData dto) {
        super(id);
        try {
            program = GL46.glCreateProgram();
            prepareShaders(GLSL_VERSION + "\n" + dto.code());
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

    private void prepareShaders(final String code) {
        int computeShader = compileShader(code, GL46.GL_COMPUTE_SHADER);

        GL46.glAttachShader(program, computeShader);

        GL46.glLinkProgram(program);
        GL46.glFlush();

        getLogger().info("Shader status {}", GL46.glGetError());
    }

    @Override
    public Map<String, UniformDTO> getUniforms() {
        return uniforms;
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.COMPUTE;
    }
}

