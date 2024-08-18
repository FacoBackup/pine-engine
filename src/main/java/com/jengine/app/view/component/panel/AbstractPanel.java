package com.jengine.app.view.component.panel;

import com.jengine.app.view.component.View;
import com.jengine.app.view.component.ViewTag;
import com.jengine.app.view.component.view.AbstractView;
import com.jengine.app.view.core.window.WindowRuntimeException;
import javassist.tools.web.Viewer;
import org.springframework.lang.NonNull;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractPanel implements View {
    private String id;
    private final Map<String, View> views = new HashMap<>();
    protected boolean isVisible = true;
    private final AbstractView root = new AbstractView(null, null, this) {
        @Override
        public void render(long index) {
            for (var child : children) {
                child.render(index);
            }
        }

        @Override
        public boolean isVisible() {
            return true;
        }
    };

    @Override
    public void render(long index) {
        if (isVisible) {
            return;
        }
        root.render(index);
    }

    public AbstractPanel() throws WindowRuntimeException {
        String xmlString = load();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(xmlString.getBytes()));
            Node root = document.getDocumentElement();
            processTag(root, this.root);
        } catch (Exception e) {
            throw new WindowRuntimeException("Error during panel creation", e);
        }
        onInitialize();
    }

    private void processTag(Node node, AbstractView parent) throws WindowRuntimeException {
        AbstractView instance = instantiateView(node, parent);
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            processChild(nodeList.item(i), instance);
        }
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public View getElementById(String id) {
        return views.get(id);
    }

    private void processChild(Node currentNode, AbstractView instance) throws WindowRuntimeException {
        if (currentNode.getNodeType() == Node.ELEMENT_NODE && ViewTag.valueOfTag(instance).isChildrenSupported()) {
            processTag(currentNode, instance);
        } else if (currentNode.getNodeType() != Node.ELEMENT_NODE) {
            instance.setInnerText(currentNode.getTextContent());
        }
    }

    private AbstractView instantiateView(Node node, AbstractView parent) throws WindowRuntimeException {
        final String id = node.getAttributes().getNamedItem("id").getNodeValue();
        final String tag = node.getNodeName();
        final ViewTag viewTag = ViewTag.valueOfTag(tag);
        if (viewTag == null) {
            throw new WindowRuntimeException("Could not find tag with name " + tag);
        }
        AbstractView instance;
        try {
            instance = viewTag.getClazz().getConstructor(AbstractView.class, String.class, AbstractPanel.class).newInstance(parent, id, this);
        } catch (Exception e) {
            throw new WindowRuntimeException("Could not instantiate view " + viewTag.getClazz(), e);
        }

        if (id != null) {
            views.put(id, instance);
        }
        return instance;
    }

    private String load() {
        String fileName = this.getClass().getSimpleName() + ".xml";
        try {
            Path path = getPathFromClasspath(fileName);
            return new String(Files.readAllBytes(path));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    private Path getPathFromClasspath(String fileName) throws URISyntaxException {
        URL resource = getClass().getResource(fileName);
        if (resource != null) {
            return Paths.get(resource.toURI());
        }
        throw new URISyntaxException(fileName, "Could not find resource " + fileName);
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
    }
}
