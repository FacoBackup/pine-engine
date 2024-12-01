package com.pine.engine.service.importer.impl;

import com.pine.common.injection.PBean;
import com.pine.engine.repository.streaming.StreamableResourceType;
import com.pine.engine.service.importer.AbstractImporter;
import com.pine.engine.service.importer.data.AbstractImportData;
import com.pine.engine.service.importer.data.TextureImportData;
import com.pine.engine.service.importer.metadata.AbstractResourceMetadata;
import com.pine.engine.service.importer.metadata.TextureResourceMetadata;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@PBean
public class TextureImporter extends AbstractImporter {
    public static final String PREVIEW_EXT = ".preview";

    @Override
    public List<AbstractImportData> importFile(String path) {
        return List.of(new TextureImportData(path));
    }

    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.TEXTURE;
    }

    @Override
    public AbstractResourceMetadata persist(AbstractImportData data) {
        var cast = (TextureImportData) data;

        Path source = Paths.get(cast.path);
        Path target = Paths.get(getPathToFile(data));

        try {
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            File file = new File(cast.path);

            BufferedImage originalImage = ImageIO.read(file);

            // Target width
            int targetWidth = 128;

            // Calculate target height to maintain aspect ratio
            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();
            int targetHeight = (targetWidth * originalHeight) / originalWidth;

            // Create a new BufferedImage with the target size
            BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, originalImage.getType());

            // Draw the original image resized to the new size
            Graphics2D g2d = resizedImage.createGraphics();
            g2d.drawImage(originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH), 0, 0, null);
            g2d.dispose();

            ImageIO.write(resizedImage, "png", new File(getPathToFile(data) + PREVIEW_EXT));

            return new TextureResourceMetadata(data.name, data.id, originalWidth, originalHeight);
        } catch (Exception e) {
            getLogger().error("Error while writing texture {}", data.id, e);
        }
        return null;
    }
}
