package com.pine.engine.util;

import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load;
import static org.lwjgl.stb.STBImageWrite.stbi_write_png;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memFree;

public class ImageUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageUtil.class);

    public static void generateTexture(int width, int height, String filename) {
        int channels = 4;
        try {
            ByteBuffer imageBuffer = BufferUtils.createByteBuffer(width * height * channels);
            for (int i = 0; i < width * height * channels; i++) {
                imageBuffer.put((byte) 0x00);
            }
            imageBuffer.flip();
            if (!stbi_write_png(filename, width, height, channels, imageBuffer, width * channels)) {
                LOGGER.error("Failed to write PNG file: {}", filename);
                return;
            }
            LOGGER.warn("Image generated at: {}", filename);
        } catch (Exception e) {
            LOGGER.error("Failed to write PNG file: {}", filename, e);
        }
    }

    public static void copyInto(String sourceImagePath, String outputImagePath, int channels) {
        try (MemoryStack stack = stackPush()) {
            int[] width1 = new int[1];
            int[] height1 = new int[1];
            int[] channels1 = new int[1];
            ByteBuffer image1 = stbi_load(sourceImagePath, width1, height1, channels1, channels);
            if (image1 == null) {
                LOGGER.error("Error while processing image");
                return;
            }

            int[] width2 = new int[1];
            int[] height2 = new int[1];
            int[] channels2 = new int[1];
            ByteBuffer image2 = stbi_load(outputImagePath, width2, height2, channels2, channels);
            if (image2 == null) {
                stbi_image_free(image1);
                LOGGER.error("Error while processing image");
                return;
            }

            // Determine which image is smaller and which is larger
            ByteBuffer smallerImage, largerImage;
            int smallerWidth, smallerHeight;
            int largerWidth, largerHeight;

            if (width1[0] * height1[0] < width2[0] * height2[0]) {
                smallerImage = image1;
                smallerWidth = width1[0];
                smallerHeight = height1[0];

                largerImage = image2;
                largerWidth = width2[0];
                largerHeight = height2[0];
            } else {
                smallerImage = image2;
                smallerWidth = width2[0];
                smallerHeight = height2[0];

                largerImage = image1;
                largerWidth = width1[0];
                largerHeight = height1[0];
            }

            ByteBuffer outputImage = memAlloc(largerWidth * largerHeight * channels);
            for (int i = 0; i < largerWidth * largerHeight * channels; i++) {
                outputImage.put(i, largerImage.get(i));
            }

            int offsetX = (largerWidth - smallerWidth) / 2;
            int offsetY = (largerHeight - smallerHeight) / 2;
            for (int y = 0; y < smallerHeight; y++) {
                for (int x = 0; x < smallerWidth; x++) {
                    int smallIndex = (y * smallerWidth + x) * channels;
                    int largeIndex = ((y + offsetY) * largerWidth + (x + offsetX)) * channels;

                    for (int z = 0; z < channels; z++) {
                        if(outputImage.get(largeIndex + z) == 0) {
                            outputImage.put(largeIndex + z, smallerImage.get(smallIndex + z));
                        }
                    }
                }
            }

            outputImage.flip();
            stbi_write_png(outputImagePath, largerWidth, largerHeight, channels, outputImage, largerWidth * channels);

            stbi_image_free(image1);
            stbi_image_free(image2);
            memFree(outputImage);
        } catch (Exception e) {
            LOGGER.error("Error while processing image", e);
        }
    }
}
