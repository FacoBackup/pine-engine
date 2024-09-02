package com.pine.app.core.ui;

import com.pine.app.core.ui.view.AbstractView;
import com.pine.app.core.window.AbstractWindow;
import com.pine.app.core.window.WindowRuntimeException;
import com.pine.common.Loggable;
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

    public AbstractWindow getWindow() {
        return window;
    }

    public int[] getWindowDimensions() {
        return window.getWindowDimensions();
    }

    public View getElementById(String id) {
        return views.get(id);
    }

    final public void appendChild(View child, View parent) {
        parent.getChildren().add(child);
        if (child.getId() != null) {
            views.put(child.getId(), child);
        }
        if(child.getContext() == null || parent.getContext() != null) {
            child.setInternalContext(parent.getContext());
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
            if(views.containsKey(id)) {
                getLogger().error("Duplicated view ID {}", id);
            }
            views.put(id, instance);
        }
        parent.getChildren().add(instance);
        if(instance.getContext() == null || parent.getContext() != null) {
            instance.setInternalContext(parent.getContext());
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
