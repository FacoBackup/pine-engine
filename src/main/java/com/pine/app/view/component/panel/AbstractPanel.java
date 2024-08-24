package com.pine.app.view.component.panel;

import com.pine.app.view.component.View;
import com.pine.app.view.component.ViewTag;
import com.pine.app.view.component.view.AbstractView;
import com.pine.app.view.core.window.WindowRuntimeException;
import jakarta.annotation.Nullable;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractPanel extends AbstractView {
    private final Map<String, View> views = new HashMap<>();
    protected boolean isVisible = true;

    public AbstractPanel(View parent, String id, AbstractPanel panel) {
        super(parent, id, panel);
    }

    public AbstractPanel() {
        super(null, null, null);
    }

    @Override
    public void render(long index) {
        if (!isVisible) {
            return;
        }
        super.render(index + 1);
    }

    @Override
    public void onInitialize() {
        try {
            final byte[] xml = loadXML();
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document document = docBuilder.parse(new ByteArrayInputStream(xml));
            processTag(document.getDocumentElement(), this);
        } catch (Exception e) {
            getLogger().warn("Unable to parse XML", e);
        }
    }

    protected byte[] loadXML(){
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        try (InputStream s = classloader.getResourceAsStream("ui" + File.separator + getClass().getSimpleName() + ".xml")) {
            if (s != null) {
               return s.readAllBytes();
            }
        } catch (Exception e) {
            getLogger().warn("Unable to load {}", getClass().getSimpleName(), e);
        }
        throw new RuntimeException("Unable to load " + getClass().getSimpleName());
    }

    private void processTag(Node node, AbstractView parent) throws WindowRuntimeException {
        final String tag = node.getNodeName();
        final ViewTag viewTag = ViewTag.valueOfTag(tag);
        if(viewTag == null){
            return;
        }

        final boolean isView = !Objects.equals(ViewTag.FRAGMENT.getTag(), tag);
        final NodeList nodeList = node.getChildNodes();
        AbstractView instance = parent;
        if (isView) {
            instance = instantiateView(viewTag, node, parent);
        }

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node currentNode = nodeList.item(i);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE && viewTag.isChildrenSupported()) {
                processTag(currentNode, instance);
            } else if (isView && currentNode.getNodeType() == Node.TEXT_NODE) {
                instance.setInnerText(currentNode.getTextContent().trim());
            }
        }
    }

    @Override
    public View getElementById(String id) {
        return views.get(id);
    }

    private AbstractView instantiateView(ViewTag viewTag, Node node, AbstractView parent) throws WindowRuntimeException {
        final String id = getId(node);
        if (viewTag == null) {
            throw new WindowRuntimeException("Could not find tag with name " + node.getNodeName());
        }
        final AbstractView instance = AbstractView.instantiate(viewTag.getClazz(), parent, id, this);
        if (instance == null) {
            throw new WindowRuntimeException("Could not instantiate view " + viewTag.getClazz());
        }

        if (id != null) {
            views.put(id, instance);
        }
        children.add(instance);
        return instance;
    }

    @Nullable
    private static String getId(Node node) {
        try {
            return node.getAttributes().getNamedItem("id").getNodeValue();
        } catch (Exception _) {
        }
        return null;
    }

    @Override
    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    @Override
    public boolean isVisible() {
        return isVisible;
    }

    public void appendChild(View child, AbstractView parent) {
        parent.getChildren().add(child);
        if (child.getId() != null) {
            views.put(child.getId(), child);
        }
        child.onInitialize();
    }

    @Override
    public void appendChild(View child) {
        getChildren().add(child);
        if (child.getId() != null) {
            views.put(child.getId(), child);
        }
        child.onInitialize();
    }
}
