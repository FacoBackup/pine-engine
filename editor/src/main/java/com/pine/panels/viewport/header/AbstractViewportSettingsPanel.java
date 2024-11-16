package com.pine.panels.viewport.header;

import com.pine.core.AbstractView;
import com.pine.injection.PInject;
import com.pine.repository.EditorRepository;
import com.pine.service.streaming.StreamingService;

public class AbstractViewportSettingsPanel extends AbstractView {

    @PInject
    public EditorRepository editorRepository;

    @PInject
    public StreamingService streamingService;

    public void renderOutside() {

    }
}
