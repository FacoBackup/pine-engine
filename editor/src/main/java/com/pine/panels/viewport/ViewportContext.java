package com.pine.panels.viewport;

import com.pine.panel.AbstractPanelContext;
import com.pine.service.camera.Camera;

import java.util.HashMap;
import java.util.Map;

public class ViewportContext extends AbstractPanelContext {
    public final Camera camera = new Camera();
}
