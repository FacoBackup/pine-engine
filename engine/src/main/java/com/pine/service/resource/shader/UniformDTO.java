package com.pine.service.resource.shader;

public class UniformDTO  {
    public static final UniformDTO EMPTY = new UniformDTO("empty", -1);
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