package com.pine.service.resource.shader;

import java.util.List;

public class UniformDTO  {
    private final GLSLType type;
    private final String name;
    private String parent;
    private int arraySize;
    private List<Integer> locations;
    private final Integer location;

    public UniformDTO(GLSLType type, String name, Integer location) {
        this.type = type;
        this.name = name;
        this.location = location;
    }

    public UniformDTO(GLSLType type, String name, String parent, Integer location) {
        this(type, name, location);
        this.parent = parent;
    }

    public GLSLType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getParent() {
        return parent;
    }

    public int getArraySize() {
        return arraySize;
    }

    public List<Integer> getLocations() {
        return locations;
    }

    public Integer getLocation() {
        return location;
    }
}