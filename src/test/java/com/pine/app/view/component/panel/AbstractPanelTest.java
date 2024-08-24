package com.pine.app.view.component.panel;

import com.pine.app.view.component.view.GroupView;
import com.pine.app.view.component.view.InlineView;
import com.pine.app.view.component.view.WindowView;
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
        AbstractPanel spy = createPanel();

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
        AbstractPanel spy = createPanel();

        var b1 = spy.getElementById("b1");
        var b2 = spy.getElementById("b2");
        var b3 = spy.getElementById("b3");

        Assertions.assertInstanceOf(InlineView.class, b1.getParent());
        Assertions.assertInstanceOf(InlineView.class, b2.getParent());
        Assertions.assertInstanceOf(WindowView.class, b3.getParent());

        Assertions.assertInstanceOf(GroupView.class, b1.getParent().getParent());
    }

    private static AbstractPanel createPanel() throws IOException {
        AbstractPanel spy = Mockito.spy(new AbstractPanel(){});
        try (InputStream xml = Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.xml")){
            Assertions.assertNotNull(xml);
            Mockito.doReturn(xml.readAllBytes()).when(spy).loadXML();
        }
        spy.onInitialize();
        return spy;
    }

}