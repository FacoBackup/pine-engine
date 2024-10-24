package com.pine.service.resource.shader;

public class UniformDTO  {
    private final GLSLType type;
    private final String name;
    public final int location;

    public UniformDTO(GLSLType type, String name, int location) {
        this.type = type;
        this.name = name;
        this.location = location;
    }

    public GLSLType getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}