package com.pine.service.resource.ssbo;

import com.pine.service.resource.primitives.GLSLType;
import com.pine.service.resource.ubo.UBOData;

public class SSBOData extends UBOData {

    public SSBOData(String name, GLSLType type) {
        super(name, type);
    }

    public SSBOData(String name, GLSLType type, Integer dataLength) {
        super(name, type, dataLength);
    }
}
