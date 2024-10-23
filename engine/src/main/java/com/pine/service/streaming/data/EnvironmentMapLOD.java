package com.pine.service.streaming.data;

import java.nio.ByteBuffer;

public class EnvironmentMapLOD {
    public final int imageSize;
    public final ByteBuffer[] images;

    public EnvironmentMapLOD(
            ByteBuffer[] images,
            int imageSize
    ) {
        this.images = images;
        this.imageSize = imageSize;
    }

}
