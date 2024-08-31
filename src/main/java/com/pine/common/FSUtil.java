package com.pine.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

public class FSUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(FSUtil.class);

    public static byte[] loadResource(String path) {
        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            try (InputStream inputStream = classloader.getResourceAsStream(path)) {
                if (inputStream != null) {
                    return inputStream.readAllBytes();
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error while reading resource {}", e.getMessage());
        }
        throw new RuntimeException("No resource found for " + path);
    }
}
