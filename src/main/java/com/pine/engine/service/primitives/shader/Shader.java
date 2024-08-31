package com.pine.engine.service.primitives.shader;

import com.pine.common.resource.ResourceType;
import com.pine.common.resource.AbstractResource;
import com.pine.engine.service.primitives.GLSLType;
import org.lwjgl.opengl.GL46;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Shader extends AbstractResource<ShaderCreationDTO> {
    private final int program;
    private final Map<String, UniformDTO> uniforms = new HashMap<>();
    private boolean valid = true;

    public Shader(String id, ShaderCreationDTO dto) {
        super(id);
        program = GL46.glCreateProgram();

        try {
            prepareShaders(dto.vertex(), dto.fragment());
        } catch (RuntimeException ex) {
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

        extractUniforms(vertex);
        extractUniforms(fragment);

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

    private void extractUniforms(String code) {
        Pattern uniformPattern = Pattern.compile("uniform(\\s+)(highp|mediump|lowp)?(\\s*)((\\w|_)+)((\\s|\\w|_)*);", Pattern.MULTILINE);
        Matcher matcher = uniformPattern.matcher(code);

        while (matcher.find()) {
            String type = matcher.group(4);
            String name = matcher.group(6).replace(" ", "").trim();

            if (GLSLType.valueOfEnum(type) != null) {
                uniforms.put(name, new UniformDTO(GLSLType.valueOfEnum(type), name, GL46.glGetUniformLocation(program, name)));
                continue;
            }

            Pattern structPattern = Pattern.compile("struct\\s+" + type + "\\s*\\{.*?\\}", Pattern.DOTALL);
            Matcher structMatcher = structPattern.matcher(code);
            if (structMatcher.find()) {
                String structCode = structMatcher.group();
                String[] structLines = structCode.split("\n");
                for (String line : structLines) {
                    for (GLSLType glslType : GLSLType.values()) {
                        if (line.contains(glslType.name())) {
                            String[] parts = line.trim().split("\\s+");
                            String fieldName = parts[parts.length - 1].replace(";", "").trim();
                            uniforms.put(name, new UniformDTO(glslType, fieldName, name, GL46.glGetUniformLocation(program, name + "." + fieldName)));
                        }
                    }
                }
            }
        }
    }

    public Map<String, UniformDTO> getUniforms() {
        return uniforms;
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.SHADER;
    }
}

