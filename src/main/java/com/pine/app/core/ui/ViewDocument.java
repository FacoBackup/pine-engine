package com.pine.app.core.ui;

import com.pine.app.core.ui.view.AbstractView;
import com.pine.app.core.window.AbstractWindow;
import com.pine.app.core.window.WindowRuntimeException;
import com.pine.common.Loggable;
import com.pine.common.fs.FSUtil;
import imgui.*;
import jakarta.annotation.Nullable;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;

public class ViewDocument implements Loggable {
    private final Map<String, View> views = new HashMap<>();
    private final AbstractWindow window;

    public ViewDocument(AbstractWindow window) {
        this.window = window;
    }

    public void initialize() {
        final var io = ImGui.getIO();
        ImFontAtlas atlas = io.getFonts();

        atlas.setFreeTypeRenderer(true);
        atlas.addFontDefault();

        final var rangesBuilder = new ImFontGlyphRangesBuilder();
        rangesBuilder.addRanges(atlas.getGlyphRangesDefault());
        rangesBuilder.addRanges(new short[]{(short) 0xe005, (short) 0xf8ff, 0});

        final var fontConfig = new ImFontConfig();
        fontConfig.setMergeMode(true);

        final short[] glyphRanges = rangesBuilder.buildRanges();
        atlas.addFontFromMemoryTTF(FSUtil.loadResource("roboto/Roboto-Medium.ttf"), 14, fontConfig, glyphRanges);
        atlas.addFontFromMemoryTTF(FSUtil.loadResource("icons/fa-regular-400.ttf"), 14, fontConfig, glyphRanges);
        atlas.build();
        fontConfig.destroy();
    }

    public AbstractWindow getWindow() {
        return window;
    }

    public ImVec2 getViewportDimensions() {
        return ImGui.getMainViewport().getSize();
    }

    public View getElementById(String id) {
        return views.get(id);
    }

    final public void appendChild(View child, View parent) {
        parent.getChildren().add(child);
        if (child.getId() != null) {
            views.put(child.getId(), child);
        }
        if (child.getContext() == null || parent.getContext() != null) {
            child.setContext(parent.getContext());
        }
        child.setParent(parent);
        child.setDocument(this);
        child.onInitialize();
    }

    public AbstractView createView(ViewTag viewTag, Node node, AbstractView parent) throws WindowRuntimeException {
        final String id = getId(node);
        if (viewTag == null) {
            throw new WindowRuntimeException("Could not find tag with name " + node.getNodeName());
        }
        AbstractView instance;
        try {
            instance = viewTag.getClazz().getConstructor(View.class, String.class).newInstance(parent, id);
        } catch (Exception ex) {
            throw new WindowRuntimeException("Could not instantiate view " + viewTag.getClazz());
        }

        if (id != null) {
            if (views.containsKey(id)) {
                getLogger().error("Duplicated view ID {}", id);
            }
            views.put(id, instance);
        }
        parent.getChildren().add(instance);
        if (instance.getContext() == null || parent.getContext() != null) {
            instance.setContext(parent.getContext());
        }
        instance.setParent(parent);
        instance.setDocument(this);
        instance.onInitialize();
        return instance;
    }

    @Nullable
    private static String getId(Node node) {
        try {
            return node.getAttributes().getNamedItem("id").getNodeValue();
        } catch (Exception _) {
            return null;
        }
    }
}
