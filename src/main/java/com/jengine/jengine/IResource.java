package com.jengine.jengine;

import com.google.gson.Gson;
import com.jengine.jengine.window.core.AbstractWindow;

import java.io.InputStream;

public interface IResource extends Loggable {
    Gson GSON = new Gson();

    default byte[] loadFromResources(String name) throws ResourceRuntimeException {
        try {
            ClassLoader classLoader = AbstractWindow.class.getClassLoader();
            try (InputStream inputStream = classLoader.getResourceAsStream(name)) {
                if (inputStream != null) {
                    return inputStream.readAllBytes();
                }
            }
        } catch (Exception e) {
            throw ResourceRuntimeException.rethrow(e);
        }
        throw new ResourceRuntimeException("No resource found for " + name);
    }
}
