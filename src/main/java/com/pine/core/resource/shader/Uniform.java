package com.pine.core.resource.shader;

import com.pine.core.resource.GLSLType;

import java.util.List;

public class Uniform {
    private final GLSLType type;
    private final String name;
    private String parent;
    private int arraySize;
    private List<Integer> uLocations;
    private final Integer uLocation;

    public Uniform(GLSLType type, String name, Integer uLocation) {
        this.type = type;
        this.name = name;
        this.uLocation = uLocation;
    }

    public Uniform(GLSLType type, String name, String parent, Integer uLocation) {
        this(type, name, uLocation);
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

    public List<Integer> getuLocations() {
        return uLocations;
    }

    public Integer getuLocation() {
        return uLocation;
    }
}