package com.jengine.jengine.app.engine.resource;

import org.lwjgl.opengl.GL46;

import java.nio.Buffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UBO {
    static class UBOItem {
        int offset;
        int dataSize;
        int chunkSize;

        UBOItem(int offset, int dataSize, int chunkSize) {
            this.offset = offset;
            this.dataSize = dataSize;
            this.chunkSize = chunkSize;
        }
    }

    static class UBOData {
        String name;
        String type;
        Integer offset;
        Integer dataSize;
        Integer chunkSize;
        Integer dataLength;

        UBOData(String name, String type, Integer offset, Integer dataSize, Integer chunkSize, Integer dataLength) {
            this.name = name;
            this.type = type;
            this.offset = offset;
            this.dataSize = dataSize;
            this.chunkSize = chunkSize;
            this.dataLength = dataLength;
        }
    }

    private List<UBOItem> items = new ArrayList<>();
    private List<String> keys = new ArrayList<>();
    private Buffer buffer;
    private String blockName;
    private int blockPoint;

    private static int blockPointIncrement = 0;

    private static int[] getGlslSizes(String type) {
        switch (type) {
            case "float":
            case "int":
            case "bool":
                return new int[]{4, 4};
            case "mat4":
                return new int[]{64, 64};
            case "mat3":
                return new int[]{48, 48};
            case "vec2":
                return new int[]{8, 8};
            case "vec3":
                return new int[]{16, 12};
            case "vec4":
                return new int[]{16, 16};
            default:
                return new int[]{0, 0};
        }
    }

    public UBO(String blockName, List<UBOData> dataArray) {
        int bufferSize = calculate(dataArray);
        for (int i = 0; i < dataArray.size(); i++) {
            UBOData data = dataArray.get(i);
            items.add(new UBOItem(data.offset, data.dataSize, data.chunkSize));
            keys.add(data.name);
        }

        this.blockName = blockName;
        this.blockPoint = blockPointIncrement++;
        buffer = GL46.glCreateBuffers();
        GL46.glBindBuffer(GL46.GL_UNIFORM_BUFFER, buffer);
        GL46.glBufferData(GL46.GL_UNIFORM_BUFFER, bufferSize, (Buffer) null, GL46.GL_DYNAMIC_DRAW);
        GL46.glBindBuffer(GL46.GL_UNIFORM_BUFFER, 0);
        GL46.glBindBufferBase(GL46.GL_UNIFORM_BUFFER, this.blockPoint, buffer);
    }

    public void bindWithShader(int shaderProgram) {
        GL46.glUseProgram(shaderProgram);
        int index = GL46.glGetUniformBlockIndex(shaderProgram, blockName);
        GL46.glUniformBlockBinding(shaderProgram, index, this.blockPoint);
        GL46.glBindBuffer(GL46.GL_UNIFORM_BUFFER, 0);
    }

    public void bind() {
        GL46.glBindBuffer(GL46.GL_UNIFORM_BUFFER, buffer);
    }

    public void unbind() {
        GL46.glBindBuffer(GL46.GL_UNIFORM_BUFFER, 0);
    }


    public void updateData(String name, Buffer data) {
        UBOItem item = items.get(keys.indexOf(name));
        GL46.glBindBuffer(GL46.GL_UNIFORM_BUFFER, buffer);
        GL46.glBufferSubData(GL46.GL_UNIFORM_BUFFER, item.offset, data);
        GL46.glBindBuffer(GL46.GL_UNIFORM_BUFFER, 0);
    }

    public void updateBuffer(Buffer data) {
        GL46.glBindBuffer(GL46.GL_UNIFORM_BUFFER, buffer);
        GL46.glBufferSubData(GL46.GL_UNIFORM_BUFFER, 0, data);
        GL46.glBindBuffer(GL46.GL_UNIFORM_BUFFER, 0);
    }

    private static int calculate(List<UBOData> dataArray) {
        int chunk = 16;
        int offset = 0;
        int[] size;

        for (int i = 0; i < dataArray.size(); i++) {
            UBOData data = dataArray.get(i);

            if (data.dataLength == null || data.dataLength == 0) {
                size = getGlslSizes(data.type);
            } else {
                size = new int[]{data.dataLength * 16 * 4, data.dataLength * 16 * 4};
            }

            int tsize = chunk - size[0];

            if (tsize < 0 && chunk < 16) {
                offset += chunk;
                if (i > 0) dataArray.get(i - 1).chunkSize += chunk;
                chunk = 16;
            } else if (tsize == 0) {
                if ("vec3".equals(data.type) && chunk == 16) {
                    chunk -= size[1];
                } else {
                    chunk = 16;
                }
            } else if (tsize >= 0 || chunk != 16) {
                chunk -= size[1];
            }

            data.offset = offset;
            data.chunkSize = size[1];
            data.dataSize = size[1];

            offset += size[1];
        }

        return offset;
    }
}
