package com.pine;

import com.pine.dock.DockPanel;
import com.pine.theme.Icons;
import com.pine.view.AbstractView;
import com.pine.view.View;
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

public abstract class AbstractWindow extends AbstractView implements Initializable {
    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
    private long handle = -1;
    protected int displayW = 1920;
    protected int displayH = 1080;
    protected final DockPanel root = new DockPanel(){
        @Override
        protected ImVec4 getAccentColor() {
            return AbstractWindow.this.getAccentColor();
        }
    };
    private boolean isVisible = true;


    @Override
    final public void onInitialize() {
        appendChild(root);

        initializeWindow();
        onInitializeInternal();
        applySpacing();
        applyFonts();
        root.setHeader(getHeader());
        root.onInitialize();

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

    protected abstract void onInitializeInternal();

    protected abstract View getHeader();

    private void initializeWindow() {
        createGlfwContext();
        ImGui.createContext();

        imGuiGlfw.init(handle, true);
        imGuiGl3.init(getGlslVersion());

        final ImGuiIO io = ImGui.getIO();
        io.setIniFilename(null);
        io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard | ImGuiConfigFlags.DockingEnable);
        io.setConfigViewportsNoTaskBarIcon(true);
        io.setConfigDockingAlwaysTabBar(true);
        io.setConfigWindowsResizeFromEdges(true);
    }

    protected abstract String getGlslVersion();

    protected void createGlfwContext() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 6);

        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        getDisplayResolution();
        handle = GLFW.glfwCreateWindow((int) (displayW * .75), (int) (displayH * .75), getWindowName(), MemoryUtil.NULL, MemoryUtil.NULL);
        if (handle == MemoryUtil.NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            final IntBuffer pWidth = stack.mallocInt(1);
            final IntBuffer pHeight = stack.mallocInt(1);

            GLFW.glfwGetWindowSize(handle, pWidth, pHeight);
            GLFW.glfwSetWindowPos(handle, (displayW - pWidth.get(0)) / 2, (displayH - pHeight.get(0)) / 2);
        }

        GLFW.glfwMakeContextCurrent(handle);
        GL.createCapabilities();
        GLFW.glfwSwapInterval(GLFW.GLFW_TRUE);
        if (isFullScreen()) {
            GLFW.glfwMaximizeWindow(handle);
            GLFW.glfwShowWindow(handle);
        } else {
            GLFW.glfwShowWindow(handle);
        }
        clearBuffer();
        renderBuffer();
        initGlfwEvents();
    }

    private void getDisplayResolution() {
        final GLFWVidMode vidmode = Objects.requireNonNull(GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor()));
        displayW = vidmode.width();
        displayH = vidmode.height();
    }

    private void initGlfwEvents() {
        GLFW.glfwSetWindowCloseCallback(handle, new GLFWWindowCloseCallback() {
            @Override
            public void invoke(long l) {
                GLFW.glfwSetWindowShouldClose(handle, true);
            }
        });
        GLFW.glfwSetWindowIconifyCallback(handle, new GLFWWindowIconifyCallback() {
            @Override
            public void invoke(long window, boolean iconified) {
                isVisible = !iconified;
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
    final public void render() {
        startFrame();
        if (isVisible) {
            ImGui.pushStyleColor(ImGuiCol.Button, getNeutralPalette());
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, getAccentColor());
            ImGui.pushStyleColor(ImGuiCol.ButtonActive, getAccentColor());

            renderInternal();

            ImGui.popStyleColor(3);
        }
        endFrame();
    }

    protected abstract ImVec4 getNeutralPalette();

    protected abstract ImVec4 getAccentColor();

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

    protected abstract float[] getBackgroundColor();

    private void clearBuffer() {
        float[] theme = getBackgroundColor();
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

    public abstract String getWindowName();

    protected boolean isFullScreen() {
        return true;
    }
}
