package com.jengine.jengine.app.engine.resource;

import com.jengine.jengine.IResource;
import com.jengine.jengine.ResourceRuntimeException;
import org.lwjgl.opengl.GL46;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public abstract class AbstractShader implements IResource {
    private int program;
    private final List<Uniform> uniforms = new ArrayList<>();
    private final Map<String, Integer> uniformMap = new HashMap<>();
    private int currentSamplerIndex = 0;
    private boolean readyToUse = false;
    private String vertexResourceName;
    private String fragmentResourceName;

    protected void compile(String vertexResourceName, String fragmentResourceName) throws ResourceRuntimeException {
        program = GL46.glCreateProgram();
        this.vertexResourceName = vertexResourceName;
        this.fragmentResourceName = fragmentResourceName;

        try {
            String vertex = new String(loadFromResources(vertexResourceName));
            String fragment = new String(loadFromResources(fragmentResourceName));
            prepareShaders(vertex, fragment);
        } catch (ResourceRuntimeException ex) {
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

        uniforms.removeIf(u -> u == null || u.getuLocation() == null && (u.getuLocations() == null || u.getuLocations().isEmpty()));

        for (Uniform uniform : uniforms) {
            uniformMap.put(uniform.getName(), uniform.getuLocation() != null ? uniform.getuLocation() : uniform.getuLocations().getFirst());
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
                uniforms.add(new Uniform(GLSLType.valueOfEnum(type), name, GL46.glGetUniformLocation(program, name)));
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
                            uniforms.add(new Uniform(glslType, fieldName, name, GL46.glGetUniformLocation(program, name + "." + fieldName)));
                        }
                    }
                }
            }
        }
    }

    public List<Uniform> getUniforms() {
        return uniforms;
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

    public void bind(Uniform uniform, Object data) {
        if (readyToUse) {

            if (data == null) return;
            Integer uLocation = uniform.getuLocation();
            switch (uniform.getType()) {
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
}

