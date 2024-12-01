package com.pine.engine.service.resource.shader;

public class UniformDTO  {
    private final String name;
    public final int location;

    public UniformDTO(String name, int location) {
        this.name = name;
        this.location = location;
    }

    public String getName() {
        return name;
    }
}