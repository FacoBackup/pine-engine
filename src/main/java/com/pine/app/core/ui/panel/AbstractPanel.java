package com.pine.app.core.ui.panel;

import com.pine.app.core.repository.WindowRepository;
import com.pine.app.core.service.WindowService;
import com.pine.app.core.ui.View;
import com.pine.app.core.ui.ViewTag;
import com.pine.app.core.ui.view.AbstractView;
import com.pine.app.core.window.WindowRuntimeException;
import com.pine.common.ContextService;
import com.pine.common.Inject;
import jakarta.annotation.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractPanel extends AbstractView {
    @Inject
    public WindowRepository windowRepository;

    private final Map<String, View> views = new HashMap<>();
    private IPanelContext internalContext;

    public AbstractPanel() {
        super(null, null, null);
        ContextService.injectDependencies(this);
    }

    final public IPanelContext getContext() {
        return internalContext;
    }

    final public void setInternalContext(IPanelContext internalContext) {
        this.internalContext = internalContext;
    }

    @Override
    public void render() {
        if (!visible) {
            return;
        }
        super.render();
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

    public final byte[] loadXML() {
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
        if (viewTag == null) {
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
    final public View getElementById(String id) {
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
        parent.getChildren().add(instance);
        instance.onInitialize();
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

    final public void appendChild(View child, AbstractView parent) {
        parent.getChildren().add(child);
        if (child.getId() != null) {
            views.put(child.getId(), child);
        }
        if (child.getClass().isAssignableFrom(AbstractPanel.class)) {
            ((AbstractPanel) child).setParent(parent);
            ((AbstractPanel) child).setInternalContext(getContext());
        }
        child.onInitialize();
    }

    @Override
    public void appendChild(View child) {
        appendChild(child, this);
    }

    final protected void setParent(View parent) {
        this.parent = parent;
    }

    @Override
    public int[] getWindowDimensions() {
        return windowRepository.getCurrentWindow().getWindowDimensions();
    }
}
