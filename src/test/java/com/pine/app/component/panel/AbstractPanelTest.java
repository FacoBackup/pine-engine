package com.pine.app.component.panel;

import com.pine.app.core.ui.panel.AbstractPanel;
import com.pine.app.core.ui.view.*;
import com.pine.app.view.core.component.view.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.InputStream;


@ExtendWith(MockitoExtension.class)
class AbstractPanelTest {

    @Test
    public void shouldLoadAllWithId() throws IOException {
        AbstractPanel spy = createPanel("sample.xml");

        Assertions.assertNotNull(spy.getElementById("w"));
        Assertions.assertNull(spy.getElementById("ignored"));
        Assertions.assertNotNull(spy.getElementById("b1"));
        Assertions.assertNotNull(spy.getElementById("b2"));
        Assertions.assertNotNull(spy.getElementById("b3"));

        Assertions.assertEquals("b1", spy.getElementById("b1").getInnerText());
        Assertions.assertEquals("b2", spy.getElementById("b2").getInnerText());
        Assertions.assertEquals("b3", spy.getElementById("b3").getInnerText());
        Assertions.assertEquals("b4 b4 b4", spy.getElementById("b4").getInnerText());
    }

    @Test
    public void shouldCreateHierarchy() throws IOException {
        AbstractPanel spy = createPanel("sample.xml");

        var b1 = spy.getElementById("b1");
        var b2 = spy.getElementById("b2");
        var b3 = spy.getElementById("b3");

        Assertions.assertEquals(spy, b1.getPanel());
        Assertions.assertInstanceOf(InlineView.class, b1.getParent());
        Assertions.assertInstanceOf(InlineView.class, b2.getParent());
        Assertions.assertInstanceOf(WindowView.class, b3.getParent());

        Assertions.assertInstanceOf(GroupView.class, b1.getParent().getParent());
        Assertions.assertInstanceOf(WindowView.class, b1.getParent().getParent().getParent());
    }

    @Test
    public void shouldCreateHierarchy2() throws IOException {
        AbstractPanel spy = createPanel("sample2.xml");

        var newProject = spy.getElementById("newProject");
        var list = spy.getElementById("list");

        Assertions.assertInstanceOf(ButtonView.class, newProject);
        Assertions.assertInstanceOf(RepeatingView.class, list);

        Assertions.assertInstanceOf(WindowView.class, newProject.getParent());
        Assertions.assertInstanceOf(WindowView.class, list.getParent());
        Assertions.assertEquals("Projects", list.getInnerText());
        Assertions.assertEquals("Projects", list.getParent().getInnerText());
    }

    private static AbstractPanel createPanel(String file) throws IOException {
        AbstractPanel spy = Mockito.spy(new AbstractPanel() {
        });
        try (InputStream xml = Thread.currentThread().getContextClassLoader().getResourceAsStream(file)) {
            Assertions.assertNotNull(xml);
            Mockito.doReturn(xml.readAllBytes()).when(spy).loadXML();
        }
        spy.onInitialize();
        return spy;
    }

}