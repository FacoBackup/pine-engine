package com.pine.engine.service.resource.fbo;

import java.util.Objects;

public final class FBOTextureData {
    private final String name;
    private final int attachment;
    private final int precision;
    private final int format;
    private final int type;
    private final boolean linear;
    private final boolean repeat;
    private final boolean isDepth;
    private int id;

    public FBOTextureData(String name, int attachment, int precision,
                          int format, int type,
                          boolean linear, boolean repeat) {
        this.name = name;
        this.attachment = attachment;
        this.precision = precision;
        this.format = format;
        this.type = type;
        this.linear = linear;
        this.repeat = repeat;
        isDepth = false;
    }

    public FBOTextureData(String name) {
        this.name = name;
        this.attachment = 0;
        this.precision = 0;
        this.format = 0;
        this.type = 0;
        this.linear = false;
        this.repeat = false;
        isDepth = true;
    }

    public int id() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String name() {
        return name;
    }

    public int attachment() {
        return attachment;
    }

    public int precision() {
        return precision;
    }

    public int format() {
        return format;
    }

    public int type() {
        return type;
    }

    public boolean linear() {
        return linear;
    }

    public boolean repeat() {
        return repeat;
    }

    public boolean isDepth() {
        return isDepth;
    }
}
