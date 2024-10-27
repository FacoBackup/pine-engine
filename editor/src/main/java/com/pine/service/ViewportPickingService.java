package com.pine.service;

import com.pine.component.Entity;
import com.pine.component.TransformationComponent;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.messaging.Loggable;
import com.pine.repository.RuntimeRepository;
import com.pine.repository.core.CoreFBORepository;
import com.pine.repository.rendering.RenderingRepository;
import com.pine.repository.rendering.RenderingRequest;
import imgui.ImGui;
import imgui.flag.ImGuiKey;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL46;

import java.nio.FloatBuffer;
import java.util.List;

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
            var actualIndex = (int) renderIndex - 1;

            if (!ImGui.isKeyDown(ImGuiKey.LeftCtrl)) {
                selectionService.clearSelection();
            }

            var transform = findEntity(actualIndex);
            if (transform != null) {
                selectionService.addSelected(transform.entity);
                selectionService.stateRepository.primitiveSelected = transform;
            }
        } else if (!ImGui.isKeyDown(ImGuiKey.LeftCtrl)) {
            selectionService.clearSelection();
        }
    }

    private TransformationComponent findEntity(int actualIndex) {
        int instancedOffset = 0;
        for (int i = 0; i < renderingRepository.requests.size(); i++) {
            var curr = renderingRepository.requests.get(i);
            if (i + instancedOffset == actualIndex) {
                return curr.transformationComponent;
            }
            List<TransformationComponent> transformationComponents = curr.transformationComponents;
            for (int j = 0, transformationComponentsSize = transformationComponents.size(); j < transformationComponentsSize; j++) {
                if (i + instancedOffset + j == actualIndex) {
                    return curr.transformationComponents.get(j);
                }
            }

            if (!renderingRepository.requests.get(i).transformationComponents.isEmpty()) {
                instancedOffset += curr.transformationComponents.size() - 1;
            }
        }
        return null;
    }
}
