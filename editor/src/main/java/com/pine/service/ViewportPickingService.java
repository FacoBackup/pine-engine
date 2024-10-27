package com.pine.service;

import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.injection.PInjector;
import com.pine.messaging.Loggable;
import com.pine.repository.EditorRepository;
import com.pine.repository.RuntimeRepository;
import com.pine.repository.core.CoreFBORepository;
import com.pine.repository.rendering.RenderingRepository;
import com.pine.service.serialization.SerializationService;
import com.pine.tools.repository.ToolsResourceRepository;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL46;

import java.nio.FloatBuffer;

import static com.pine.core.dock.DockWrapperPanel.FRAME_SIZE;

@PBean
public class ViewportPickingService implements Loggable {
    @PInject
    public RenderingRepository renderingRepository;

    @PInject
    public CoreFBORepository coreFBORepository;

    @PInject
    public RuntimeRepository runtimeRepository;

    @PInject
    public SelectionService selectionService;

    public void pick() {
        int x = (int) ((runtimeRepository.mouseX - runtimeRepository.viewportX) * (runtimeRepository.getDisplayW() / runtimeRepository.viewportW));
        int y = (int) ((runtimeRepository.getDisplayH() - runtimeRepository.mouseY - runtimeRepository.viewportY));

        getLogger().warn("X: {} Y: {} OX: {} OY: {}", x, y, runtimeRepository.mouseX, runtimeRepository.mouseY);
        FloatBuffer pixelBuffer = BufferUtils.createFloatBuffer(2);

        GL46.glBindFramebuffer(GL46.GL_READ_FRAMEBUFFER, coreFBORepository.gBuffer.getFBO());
        GL46.glReadBuffer(GL46.GL_COLOR_ATTACHMENT4);
        GL46.glReadPixels(x, y, 1, 1, GL46.GL_RG, GL46.GL_FLOAT, pixelBuffer);

        float renderIndex = pixelBuffer.get(1);
        getLogger().warn("Picked {}", renderIndex);

        GL46.glReadBuffer(GL46.GL_NONE);
        GL46.glBindFramebuffer(GL46.GL_READ_FRAMEBUFFER, GL11.GL_NONE);

        if (renderIndex >= 1) {
            var request = renderingRepository.requests.get((int) renderIndex - 1);
            selectionService.clearSelection();
            selectionService.addSelected(request.entity);
        }
    }
}
