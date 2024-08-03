package com.jengine.jengine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import org.lwjgl.opengl.*;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;


@Service
public class ApplicationUIService {
    @Autowired
    private WindowRepository windowRepository;

    @Autowired
    private IMGUIExample example;

    @EventListener(ApplicationReadyEvent.class)
    public void startLoop() {
        final long window = windowRepository.getWindow();
        loop(window);
        terminate(window);
    }

    private static void terminate(long window) {
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }

    private void loop(final long window) {
        GL.createCapabilities();
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
            example.newFrame();
            glfwPollEvents();

        }
    }

}
