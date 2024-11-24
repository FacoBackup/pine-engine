package com.pine.editor.panels.viewport.header;

import com.pine.editor.core.AbstractView;
import com.pine.common.injection.PInject;
import com.pine.editor.repository.EditorRepository;
import com.pine.engine.service.streaming.StreamingService;

public class AbstractViewportSettingsPanel extends AbstractView {

    @PInject
    public EditorRepository editorRepository;

    @PInject
    public StreamingService streamingService;

    public void renderOutside() {

    }
}
