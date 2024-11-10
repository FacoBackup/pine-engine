package com.pine.service.terrain;

import com.pine.repository.streaming.StreamableResourceType;
import org.apache.logging.log4j.util.TriConsumer;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static com.pine.service.grid.HashGrid.TILE_SIZE;

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
            if (!STBImageWrite.stbi_write_png(filename, width, height, channels, imageBuffer, width * channels)) {
                LOGGER.error("Failed to write PNG file: {}", filename);
                return;
            }
            LOGGER.warn("Image generated at: {}", filename);
        } catch (Exception e) {
            LOGGER.error("Failed to write PNG file: {}", filename, e);
        }
    }

    public static void splitImage(String imagePath, String basePath, TriConsumer<Integer, Integer, Integer> onImageLoad) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer widthBuffer = stack.mallocInt(1);
            IntBuffer heightBuffer = stack.mallocInt(1);
            IntBuffer channelsBuffer = stack.mallocInt(1);

            ByteBuffer imageBuffer = STBImage.stbi_load(imagePath, widthBuffer, heightBuffer, channelsBuffer, 4);
            if (imageBuffer == null) {
                throw new RuntimeException("Failed to load image: " + STBImage.stbi_failure_reason());
            }

            int imageWidth = widthBuffer.get(0);
            int imageHeight = heightBuffer.get(0);
            int components = 4;

            if (imageWidth <= TILE_SIZE) {
                STBImage.stbi_image_free(imageBuffer);

                try {
                    String chunkName = getTerrainTileName(0, 0);
                    Files.copy(Paths.get(imagePath), Paths.get(basePath + chunkName + "." + StreamableResourceType.TEXTURE.name()), StandardCopyOption.REPLACE_EXISTING);
                    onImageLoad.accept(imageWidth, 0, 0);
                    return;
                } catch (Exception e) {
                    LOGGER.error("Failed to copy image: {}", STBImage.stbi_failure_reason(), e);
                    return;
                }
            }

            int columns = imageWidth / TILE_SIZE;
            int rows = imageHeight / TILE_SIZE;

            for (int y = 0; y < rows; y++) {
                for (int x = 0; x < columns; x++) {
                    ByteBuffer tileBuffer = BufferUtils.createByteBuffer(TILE_SIZE * TILE_SIZE * components);
                    for (int ty = 0; ty < TILE_SIZE; ty++) {
                        for (int tx = 0; tx < TILE_SIZE; tx++) {
                            int srcIndex = ((y * TILE_SIZE + ty) * imageWidth + (x * TILE_SIZE + tx)) * components;
                            int dstIndex = (ty * TILE_SIZE + tx) * components;

                            for (int c = 0; c < components; c++) {
                                tileBuffer.put(dstIndex + c, imageBuffer.get(srcIndex + c));
                            }
                        }
                    }
                    String chunkName = getTerrainTileName(x, y);
                    onImageLoad.accept(imageWidth, x, y);
                    STBImageWrite.stbi_write_png(basePath + chunkName + "." + StreamableResourceType.TEXTURE.name(), TILE_SIZE, TILE_SIZE, components, tileBuffer, TILE_SIZE * components);
                }
            }
            STBImage.stbi_image_free(imageBuffer);
        } catch (Exception e) {
            LOGGER.error("Failed to write image: {}", STBImage.stbi_failure_reason(), e);
        }
    }

    public static String getTerrainTileName(int x, int z) {
        return "terrain_" + x + "_" + z;
    }
}
