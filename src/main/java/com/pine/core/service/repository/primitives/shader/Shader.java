package com.pine.core.service.repository.primitives.shader;

import com.pine.common.FSUtil;
import com.pine.core.service.ResourceType;
import com.pine.core.service.common.AbstractResource;
import com.pine.core.service.repository.primitives.GLSLType;
import org.lwjgl.opengl.GL46;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Shader extends AbstractResource<ShaderCreationDTO> {
    private int program;
    private final List<UniformDTO> uniformDTOS = new ArrayList<>();
    private final Map<String, Integer> uniformMap = new HashMap<>();
    private int currentSamplerIndex = 0;
    private boolean readyToUse = false;
    private String vertexResourceName;
    private String fragmentResourceName;

    public Shader(String id, ShaderCreationDTO dto) {
        super(id);
        // TODO
    }

    public void compile(String vertexResourceName, String fragmentResourceName) throws RuntimeException {
        program = GL46.glCreateProgram();
        this.vertexResourceName = vertexResourceName;
        this.fragmentResourceName = fragmentResourceName;

        try {
            String vertex = new String(FSUtil.loadResource(vertexResourceName));
            String fragment = new String(FSUtil.loadResource(fragmentResourceName));
            prepareShaders(vertex, fragment);
        } catch (RuntimeException ex) {
            getLogger().error("Error loading shader {} {}", vertexResourceName, fragmentResourceName, ex);
            throw ex;
        }
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

        uniformDTOS.removeIf(u -> u == null || u.getuLocation() == null && (u.getuLocations() == null || u.getuLocations().isEmpty()));

        for (UniformDTO uniformDTO : uniformDTOS) {
            uniformMap.put(uniformDTO.getName(), uniformDTO.getuLocation() != null ? uniformDTO.getuLocation() : uniformDTO.getuLocations().getFirst());
        }

        this.readyToUse = true;
        getLogger().info("Shader status {}", GL46.glGetError());

        // TODO - BIND UBOs
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
                uniformDTOS.add(new UniformDTO(GLSLType.valueOfEnum(type), name, GL46.glGetUniformLocation(program, name)));
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
                            uniformDTOS.add(new UniformDTO(glslType, fieldName, name, GL46.glGetUniformLocation(program, name + "." + fieldName)));
                        }
                    }
                }
            }
        }
    }

    public List<UniformDTO> getUniforms() {
        return uniformDTOS;
    }

    public Map<String, Integer> getUniformMap() {
        return uniformMap;
    }

    public void bindProgram() {
        if (readyToUse) {
            this.currentSamplerIndex = 0;
            GL46.glUseProgram(this.program);
        } else {
            warn();
        }
    }

    public void bind(UniformDTO uniformDTO, Object data) {
        if (readyToUse) {

            if (data == null) return;
            Integer uLocation = uniformDTO.getuLocation();
            switch (uniformDTO.getType()) {
                case f:
                    if (data instanceof FloatBuffer) {
                        GL46.glUniform1fv(uLocation, (FloatBuffer) data);
                    }
                    break;
                case vec2:
                    if (data instanceof FloatBuffer) {
                        GL46.glUniform2fv(uLocation, (FloatBuffer) data);
                    }
                    break;

                case vec3:
                    if (data instanceof FloatBuffer) {
                        GL46.glUniform3fv(uLocation, (FloatBuffer) data);
                    }
                    break;
                case vec4:
                    if (data instanceof FloatBuffer) {
                        GL46.glUniform4fv(uLocation, (FloatBuffer) data);
                    }
                    break;
                case ivec2:
                    if (data instanceof IntBuffer) {
                        GL46.glUniform2iv(uLocation, (IntBuffer) data);
                    }
                    break;
                case ivec3:
                    if (data instanceof IntBuffer) {
                        GL46.glUniform3iv(uLocation, (IntBuffer) data);
                    }
                    break;
                case bool:
                    if (data instanceof IntBuffer) {
                        GL46.glUniform1iv(uLocation, (IntBuffer) data);
                    }
                    break;
                case mat3:
                    if (data instanceof FloatBuffer) {
                        GL46.glUniformMatrix3fv(uLocation, false, (FloatBuffer) data);
                    }
                    break;

                case mat4:
                    if (data instanceof FloatBuffer) {
                        GL46.glUniformMatrix4fv(uLocation, false, (FloatBuffer) data);
                    }
                    break;
                case samplerCube:
                    if (data instanceof Integer) {
                        GL46.glActiveTexture(GL46.GL_TEXTURE0 + currentSamplerIndex);
                        GL46.glBindTexture(GL46.GL_TEXTURE_CUBE_MAP, (Integer) data);
                        GL46.glUniform1i(uLocation, currentSamplerIndex);
                        currentSamplerIndex++;
                    }
                    break;

                case sampler2D:
                    // Add binding code for sampler2D here if needed
                    break;
            }
        } else {
            warn();
        }
    }

    private void warn() {
        getLogger().warn("Shader is not ready {} {}", vertexResourceName, fragmentResourceName);
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.SHADER;
    }
}

