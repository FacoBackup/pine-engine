package com.pine.app.core.window;

import com.pine.app.core.service.WindowService;
import com.pine.app.core.ui.Renderable;
import com.pine.app.core.ui.ViewDocument;
import com.pine.app.core.ui.panel.DockDTO;
import com.pine.app.core.ui.panel.DockPanel;
import com.pine.common.ContextService;
import com.pine.common.Inject;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;
import java.util.List;
import java.util.Objects;

public abstract class AbstractWindow implements Renderable {
    private static final String GLSL_VERSION = "#version 130";
    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
    private long handle = -1;
    private final ViewDocument viewDocument = new ViewDocument(this);
    private final DockPanel root = new DockPanel();

    @Inject
    public WindowService windowService;

    public AbstractWindow() {
        ContextService.injectDependencies(this);
    }

    @Override
    public void onInitialize() {
        createGlfwContext();
        ImGui.createContext();

        imGuiGlfw.init(handle, true);
        imGuiGl3.init(GLSL_VERSION);

        final ImGuiIO io = ImGui.getIO();
        io.setIniFilename(null);
        io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard | ImGuiConfigFlags.DockingEnable);
        io.setConfigViewportsNoTaskBarIcon(true);
        io.setConfigDockingAlwaysTabBar(true);
        io.setConfigWindowsResizeFromEdges(true);

        viewDocument.initialize();

        root.setDocument(viewDocument);
        root.initializeDockSpaces(getDockSpaces());
        root.onInitialize();
    }

    protected abstract List<DockDTO> getDockSpaces();

    protected void createGlfwContext() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 0);

        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        handle = GLFW.glfwCreateWindow(getWindowWidth(), getWindowHeight(), getWindowName(), MemoryUtil.NULL, MemoryUtil.NULL);

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
        if (isFullScreen()) {
            GLFW.glfwMaximizeWindow(handle);
        } else {
            GLFW.glfwShowWindow(handle);
        }

        clearBuffer();
        renderBuffer();

        initGlfwEvents();
    }

    private void initGlfwEvents() {
        GLFW.glfwSetWindowCloseCallback(handle, new GLFWWindowCloseCallback() {
            @Override
            public void invoke(long l) {
                windowService.closeWindow(AbstractWindow.this);
            }
        });
    }

    public void dispose() {
        imGuiGl3.shutdown();
        imGuiGlfw.shutdown();
        disposeImGui();
        disposeWindow();

    }

    private void disposeImGui() {
        ImGui.destroyContext();
    }

    private void disposeWindow() {
        Callbacks.glfwFreeCallbacks(handle);
        GLFW.glfwDestroyWindow(handle);
        GLFW.glfwTerminate();
        Objects.requireNonNull(GLFW.glfwSetErrorCallback(null)).free();
    }

    @Override
    public void render() {
        startFrame();

        root.render();

        endFrame();
    }

    private void endFrame() {
        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());
        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long backupCurrentContext = GLFW.glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            GLFW.glfwMakeContextCurrent(backupCurrentContext);
        }
        renderBuffer();
    }

    private void startFrame() {
        clearBuffer();
        imGuiGl3.newFrame();
        imGuiGlfw.newFrame();
        ImGui.newFrame();
    }

    private void clearBuffer() {
        float[] theme = viewDocument.getBackgroundColor();
        GL46.glClearColor(theme[0], theme[1], theme[2], 1);
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
    }


    private void renderBuffer() {
        GLFW.glfwSwapBuffers(handle);
        GLFW.glfwPollEvents();
    }

    public final long getHandle() {
        return handle;
    }

    public abstract int getWindowWidth();

    public abstract String getWindowName();

    public abstract int getWindowHeight();

    public abstract boolean isFullScreen();
}
