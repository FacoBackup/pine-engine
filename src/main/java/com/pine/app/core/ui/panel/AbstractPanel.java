package com.pine.app.core.ui.panel;

import com.pine.app.core.ui.View;
import com.pine.app.core.ui.ViewTag;
import com.pine.app.core.ui.view.AbstractView;
import com.pine.app.core.window.WindowRuntimeException;
import com.pine.common.ContextService;
import org.intellij.lang.annotations.Language;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

public abstract class AbstractPanel extends AbstractView {

    public AbstractPanel() {
        super(null, null);
        ContextService.injectDependencies(this);
    }

    @Override
    public void onInitialize() {
        try {
            String xmlString = getDefinition();
            byte[] xml;
            if (xmlString == null) {
                xml = loadXML();
            } else {
                xml = xmlString.getBytes();
            }
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

        final NodeList nodeList = node.getChildNodes();
        AbstractView instance = document.createView(viewTag, node, parent);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node currentNode = nodeList.item(i);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE && viewTag.isChildrenSupported()) {
                processTag(currentNode, instance);
            } else if (currentNode.getNodeType() == Node.TEXT_NODE) {
                instance.setInnerText(currentNode.getTextContent().trim());
            }
        }
    }

    @Override
    public void appendChild(View child) {
        getDocument().appendChild(child, this);
    }

    @Language("xml")
    protected String getDefinition() {
        return null;
    }
}
