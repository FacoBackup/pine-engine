package com.pine.panels.painting;

import com.pine.core.view.AbstractView;
import com.pine.injection.PInject;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.streaming.StreamingService;
import com.pine.service.streaming.ref.TextureResourceRef;
import imgui.ImGui;
import imgui.ImVec2;

import static com.pine.panels.viewport.ViewportPanel.INV_X;
import static com.pine.panels.viewport.ViewportPanel.INV_Y;

public abstract class AbstractMaskPanel extends AbstractView {

    @PInject
    public StreamingService streamingService;

    private boolean showMask = false;
    private final ImVec2 maskRes = new ImVec2();

    protected abstract String getTextureId();

    @Override
    final public void render() {
        ImGui.dummy(0, 8);
        if (getTextureId() != null) {
            if (ImGui.checkbox("Show mask" + imguiId, showMask)) {
                showMask = !showMask;
            }

            if (showMask) {
                var targetTexture = (TextureResourceRef) streamingService.streamIn(getTextureId(), StreamableResourceType.TEXTURE);
                if (targetTexture != null) {
                    ImGui.setNextWindowSize(150, 150);
                    if (ImGui.beginChild(imguiId)) {
                        maskRes.x = ImGui.getWindowSizeX();
                        maskRes.y = ImGui.getWindowSizeX();
                        ImGui.image(targetTexture.texture, maskRes, INV_Y, INV_X);
                    }
                    ImGui.endChild();
                }
            }
        }

        renderInternal();
    }

    protected void renderInternal() {
    }
}
