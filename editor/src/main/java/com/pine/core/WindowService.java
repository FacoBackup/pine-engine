package com.pine.core;

import com.pine.FSUtil;
import com.pine.GLSLVersion;
import com.pine.injection.*;
import com.pine.messaging.Loggable;
import com.pine.theme.Icons;
import com.pine.window.EditorWindow;
import imgui.*;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;
import java.util.Objects;

@PBean
public class WindowService implements Disposable, Loggable {
    private static boolean shouldStop = false;
    private static final CharSequence WINDOW_NAME = "Pine Engine";
    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
    private long handle = -1;
    private int displayW;
    private int displayH;
    public AbstractWindow window = new EditorWindow();

    @PInject
    public PInjector injector;

    @PostCreation(order = Integer.MAX_VALUE)
    public void onInitialize() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> shouldStop = true));

        createGlfwContext();
        ImGui.createContext();

        imGuiGlfw.init(handle, true);
        imGuiGl3.init(GLSLVersion.getVersion());

        final ImGuiIO io = ImGui.getIO();
        io.setIniFilename(null);
        io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard | ImGuiConfigFlags.DockingEnable);
        io.setConfigViewportsNoTaskBarIcon(true);
        io.setConfigDockingAlwaysTabBar(true);
        io.setConfigWindowsResizeFromEdges(true);

        applySpacing();
        applyFonts();

        start();
    }

    public void start() {
        try {
            injector.inject(window);
            window.onInitialize();

            setupWindow(window.getWindowScaleX(), window.getWindowScaleY());
            GLFW.glfwShowWindow(handle);

            while (!GLFW.glfwWindowShouldClose(handle) && !shouldStop) {
                try {
                    startFrame();
                    window.render();
                    endFrame();
                } catch (Exception e) {
                    getLogger().error(e.getMessage(), e);
                }
            }
        } catch (Exception ex) {
            getLogger().error(ex.getMessage(), ex);
        }
    }

    public void stop() {
        shouldStop = true;
    }

    private void createGlfwContext() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 6);

        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        final GLFWVidMode vidmode = Objects.requireNonNull(GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor()));
        displayW = vidmode.width();
        displayH = vidmode.height();
        handle = GLFW.glfwCreateWindow((int) (displayW * .75), (int) (displayH * .75), WINDOW_NAME, MemoryUtil.NULL, MemoryUtil.NULL);
        if (handle == MemoryUtil.NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        setupWindow(1, 1);

        GLFW.glfwMakeContextCurrent(handle);
        GL.createCapabilities();
        GLFW.glfwSwapInterval(GLFW.GLFW_FALSE);

        clearBuffer();
        renderBuffer();
        initGlfwEvents();
    }

    private void setupWindow(float widthScale, float heightScale) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final IntBuffer pWidth = stack.mallocInt(1);
            final IntBuffer pHeight = stack.mallocInt(1);

            GLFW.glfwGetWindowSize(handle, pWidth, pHeight);
            GLFW.glfwSetWindowPos(handle, (int) ((displayW - pWidth.get(0) * widthScale) / 2), (int) ((displayH - pHeight.get(0) * heightScale) / 2));
            GLFW.glfwSetWindowSize(handle, (int) (pWidth.get(0) * widthScale), (int) (pHeight.get(0) * heightScale));
        }
    }

    private void initGlfwEvents() {
        GLFW.glfwSetWindowCloseCallback(handle, new GLFWWindowCloseCallback() {
            @Override
            public void invoke(long l) {
                GLFW.glfwSetWindowShouldClose(handle, true);
            }
        });
    }

    private void applySpacing() {
        ImGuiStyle style = ImGui.getStyle();
        float borderRadius = 3f;
        float borderWidth = 1;

        style.setWindowMinSize(new ImVec2(25f, 25f));
        style.setWindowPadding(new ImVec2(8f, 8f));
        style.setFramePadding(new ImVec2(5f, 5f));
        style.setCellPadding(new ImVec2(6f, 5f));
        style.setItemSpacing(new ImVec2(6f, 5f));
        style.setItemInnerSpacing(new ImVec2(6f, 6f));
        style.setTouchExtraPadding(new ImVec2(0f, 0f));
        style.setIndentSpacing(25f);
        style.setScrollbarSize(13f);
        style.setGrabMinSize(10f);
        style.setWindowBorderSize(borderWidth);
        style.setChildBorderSize(borderWidth);
        style.setPopupBorderSize(borderWidth);
        style.setFrameBorderSize(borderWidth);
        style.setTabBorderSize(borderWidth);
        style.setWindowRounding(0);
        style.setChildRounding(borderRadius);
        style.setFrameRounding(borderRadius);
        style.setPopupRounding(borderRadius);
        style.setScrollbarRounding(9f);
        style.setGrabRounding(borderRadius);
        style.setLogSliderDeadzone(4f);
        style.setTabRounding(borderRadius);
        style.setAlpha(1);
    }

    private void applyFonts() {
        final var io = ImGui.getIO();
        io.getFonts().setFreeTypeRenderer(true);

        final ImFontConfig fontConfig = new ImFontConfig();
        fontConfig.setPixelSnapH(true);

        io.getFonts().addFontFromMemoryTTF(FSUtil.loadResource("fonts/Roboto-Regular.ttf"), 14, fontConfig, io.getFonts().getGlyphRangesDefault());
        fontConfig.setMergeMode(true);
        fontConfig.setGlyphOffset(-2, 4);
        io.getFonts().addFontFromMemoryTTF(FSUtil.loadResource("fonts/MaterialIcons.ttf"), 18, fontConfig, Icons.RANGE);

        io.getFonts().build();
        fontConfig.destroy();
    }

    public void startFrame() {
        clearBuffer();
        imGuiGl3.newFrame();
        imGuiGlfw.newFrame();
        ImGui.newFrame();

        ImGui.pushStyleColor(ImGuiCol.Button, window.getNeutralPalette());
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, window.getAccentColor());
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, window.getAccentColor());
    }

    public void endFrame() {
        ImGui.popStyleColor(3);
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

    private void clearBuffer() {
        GL46.glClearColor(0, 0, 0, 1);
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
    }

    private void renderBuffer() {
        GLFW.glfwSwapBuffers(handle);
        GLFW.glfwPollEvents();
    }

    public int getDisplayW() {
        return displayW;
    }

    public int getDisplayH() {
        return displayH;
    }

    @Override
    public void dispose() {
        GLFW.glfwWindowShouldClose(handle);

        imGuiGl3.shutdown();
        imGuiGlfw.shutdown();
        ImGui.destroyContext();
        Callbacks.glfwFreeCallbacks(handle);
        GLFW.glfwDestroyWindow(handle);
        GLFW.glfwTerminate();
        Objects.requireNonNull(GLFW.glfwSetErrorCallback(null)).free();
    }

    public void maximize() {
        GLFW.glfwMaximizeWindow(handle);
    }
}
