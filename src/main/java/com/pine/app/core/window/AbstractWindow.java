package com.pine.app.core.window;

import com.pine.app.core.service.WindowService;
import com.pine.app.core.ui.Renderable;
import com.pine.app.core.ui.View;
import com.pine.app.core.ui.ViewDocument;
import com.pine.app.core.ui.panel.AbstractPanel;
import com.pine.app.core.window.gl3.ImGuiImplGl3;
import com.pine.app.core.window.glfw.ImGuiImplGlfw;
import com.pine.common.ContextService;
import com.pine.common.Inject;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;
import java.util.Objects;

public abstract class AbstractWindow implements Renderable {
    private static final String GLSL_VERSION = "#version 130";
    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
    private long handle;
    protected final Color colorBg = new Color(.5f, .5f, .5f, 1);
    private final int[] dimensions = new int[2];
    private final ViewDocument document = new ViewDocument(this);
    private final AbstractPanel root = new AbstractPanel() {
        @Override
        public void onInitialize() {
        }

        @Override
        public ViewDocument getDocument() {
            return document;
        }
    };

    @Inject
    public WindowService windowService;

    public AbstractWindow() {
        ContextService.injectDependencies(this);
    }

    @Override
    public void onInitialize() {
        createGLFWContext();
        ImGui.createContext();
        try {
            initFonts();
        } catch (Exception e) {
            getLogger().warn(e.getMessage(), e);
        }
        imGuiGlfw.init(handle, true);
        imGuiGl3.init(GLSL_VERSION);

        final ImGuiIO io = ImGui.getIO();
        io.setIniFilename(null);
        io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard);
        io.setConfigViewportsNoTaskBarIcon(true);
    }

    protected void createGLFWContext() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 0);

        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        handle = GLFW.glfwCreateWindow(getWindowWidth(), getWindowHeight(), getWindowName(), MemoryUtil.NULL, MemoryUtil.NULL);
        dimensions[0] = getWindowWidth();
        dimensions[1] = getWindowHeight();

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

        GLFW.glfwSetWindowSizeCallback(handle, new GLFWWindowSizeCallback() {
            @Override
            public void invoke(final long window, final int width, final int height) {
                render();
                dimensions[0] = width;
                dimensions[1] = height;
            }
        });

        GLFW.glfwSetWindowCloseCallback(handle, new GLFWWindowCloseCallback() {
            @Override
            public void invoke(long l) {
                windowService.closeWindow(AbstractWindow.this);
            }
        });
    }

    private void initFonts() throws RuntimeException {
//        final ImGuiIO io = ImGui.getIO();
//        io.getFonts().addFontDefault();
//        final ImFontGlyphRangesBuilder rangesBuilder = new ImFontGlyphRangesBuilder();
//        rangesBuilder.addRanges(io.getFonts().getGlyphRangesDefault());
//        rangesBuilder.addRanges(new short[]{(short) 0xe005, (short) 0xf8ff, 0});
//
//        final ImFontConfig fontConfig = new ImFontConfig();
//        fontConfig.setMergeMode(true);
//
//        final short[] glyphRanges = rangesBuilder.buildRanges();
//        material = io.getFonts().addFontFromMemoryTTF(FSUtil.loadResource("icons/MaterialIcons-Regular.ttf"), 14, fontConfig, glyphRanges);
//        roboto = io.getFonts().addFontFromMemoryTTF(FSUtil.loadResource("roboto/Roboto-Regular.ttf"), 14, fontConfig, glyphRanges);
//        io.getFonts().build();
    }

    public void dispose() {
        imGuiGl3.dispose();
        imGuiGlfw.dispose();
        ImGui.destroyContext();
        Callbacks.glfwFreeCallbacks(handle);
        GLFW.glfwDestroyWindow(handle);
        GLFW.glfwTerminate();
        Objects.requireNonNull(GLFW.glfwSetErrorCallback(null)).free();
    }

    @Override
    public void render() {
        clearBuffer();
        imGuiGlfw.newFrame();
        ImGui.newFrame();

        root.render();

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

    private void clearBuffer() {
        GL46.glClearColor(colorBg.getRed(), colorBg.getGreen(), colorBg.getBlue(), colorBg.getAlpha());
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
    }


    private void renderBuffer() {
        GLFW.glfwSwapBuffers(handle);
        GLFW.glfwPollEvents();
    }

    protected void appendChild(View view) {
        root.appendChild(view);
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

    public int[] getWindowDimensions() {
        return dimensions;
    }
}
