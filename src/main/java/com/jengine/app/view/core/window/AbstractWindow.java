package com.jengine.app.view.core.window;

import com.jengine.app.IResource;
import com.jengine.app.ResourceRuntimeException;
import com.jengine.app.view.component.View;
import com.jengine.app.view.component.view.AbstractView;
import com.jengine.app.view.core.window.gl3.ImGuiImplGl3;
import com.jengine.app.view.core.window.glfw.ImGuiImplGlfw;
import imgui.ImGui;
import imgui.flag.ImGuiConfigFlags;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;
import java.util.Objects;

public abstract class AbstractWindow implements IResource, View {
    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
    private String glslVersion = null;
    protected long handle;
    protected final Color colorBg = new Color(.5f, .5f, .5f, 1);
    private WindowConfiguration windowConfig;

    public abstract void preStart();

    public abstract void postStart();

    @Override
    public void onInitialize() {
        createGLFWContext(windowConfig);
        ImGui.createContext();

        try {
            initFonts();
        } catch (Exception e) {
            getLogger().warn(e.getMessage(), e);
        }
        imGuiGlfw.init(handle, true);
        imGuiGl3.init(glslVersion);
    }


    protected void createGLFWContext(final WindowConfiguration config) {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glslVersion = "#version 130";
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 0);

        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        handle = GLFW.glfwCreateWindow(config.getWidth(), config.getHeight(), config.getTitle(), MemoryUtil.NULL, MemoryUtil.NULL);

        if (handle == MemoryUtil.NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            final IntBuffer pWidth = stack.mallocInt(1); // int*
            final IntBuffer pHeight = stack.mallocInt(1); // int*

            GLFW.glfwGetWindowSize(handle, pWidth, pHeight);
            final GLFWVidMode vidmode = Objects.requireNonNull(GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor()));
            GLFW.glfwSetWindowPos(handle, (vidmode.width() - pWidth.get(0)) / 2, (vidmode.height() - pHeight.get(0)) / 2);
        }

        GLFW.glfwMakeContextCurrent(handle);

        GL.createCapabilities();

        GLFW.glfwSwapInterval(GLFW.GLFW_TRUE);

        if (config.isFullScreen()) {
            GLFW.glfwMaximizeWindow(handle);
        } else {
            GLFW.glfwShowWindow(handle);
        }

        clearBuffer();
        renderBuffer();

        GLFW.glfwSetWindowSizeCallback(handle, new GLFWWindowSizeCallback() {
            @Override
            public void invoke(final long window, final int width, final int height) {
                runFrame();
            }
        });
    }

    protected abstract void initFonts() throws WindowRuntimeException, ResourceRuntimeException;

    public void dispose() {
        imGuiGl3.dispose();
        imGuiGlfw.dispose();
        ImGui.destroyContext();
        Callbacks.glfwFreeCallbacks(handle);
        GLFW.glfwDestroyWindow(handle);
        GLFW.glfwTerminate();
        Objects.requireNonNull(GLFW.glfwSetErrorCallback(null)).free();
    }

    public void runFrame() {
        startFrame();
        render(0);
        endFrame();
    }

    private void clearBuffer() {
        GL46.glClearColor(colorBg.getRed(), colorBg.getGreen(), colorBg.getBlue(), colorBg.getAlpha());
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
    }

    protected void startFrame() {
        clearBuffer();
        imGuiGlfw.newFrame();
        ImGui.newFrame();
    }

    protected void endFrame() {
        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());

        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long backupWindowPtr = GLFW.glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            GLFW.glfwMakeContextCurrent(backupWindowPtr);
        }

        renderBuffer();
    }

    private void renderBuffer() {
        GLFW.glfwSwapBuffers(handle);
        GLFW.glfwPollEvents();
    }


    public final long getHandle() {
        return handle;
    }

    public final Color getColorBg() {
        return colorBg;
    }

    public abstract int getWindowWidth();

    public abstract String getWindowName();

    public abstract int getWindowHeight();

    public abstract boolean isFullScreen();

    public void setConfig(WindowConfiguration windowConfiguration) {
        this.windowConfig = windowConfiguration;
    }
}
