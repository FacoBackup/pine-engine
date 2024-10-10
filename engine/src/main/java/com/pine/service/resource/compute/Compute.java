package com.pine.service.resource.compute;

import com.pine.GLSLVersion;
import com.pine.service.resource.AbstractResource;
import com.pine.service.resource.IShader;
import com.pine.service.resource.LocalResourceType;
import com.pine.service.resource.shader.UniformDTO;
import org.lwjgl.opengl.GL46;

import java.util.HashMap;
import java.util.Map;


public class Compute extends AbstractResource implements IShader {
    private int program;
    private final Map<String, UniformDTO> uniforms = new HashMap<>();
    private boolean valid = true;

    public Compute(String id, ComputeCreationData dto) {
        super(id);
        try {
            program = GL46.glCreateProgram();
            prepareShaders(GLSLVersion.getVersion() + "\n" + dto.code());
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
    }

    @Override
    public Map<String, UniformDTO> getUniforms() {
        return uniforms;
    }

    @Override
    public LocalResourceType getResourceType() {
        return LocalResourceType.COMPUTE;
    }

    @Override
    public void dispose() {
        GL46.glDeleteProgram(program);
    }
}

