package com.pine.service;

import com.pine.component.MeshComponent;
import com.pine.component.TransformationComponent;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.messaging.Loggable;
import com.pine.repository.RuntimeRepository;
import com.pine.repository.core.CoreBufferRepository;
import com.pine.service.grid.HashGridService;
import imgui.ImGui;
import imgui.flag.ImGuiKey;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL46;

import java.nio.FloatBuffer;
import java.util.Collection;

@PBean
public class ViewportPickingService implements Loggable {
    @PInject
    public CoreBufferRepository coreBufferRepository;

    @PInject
    public RuntimeRepository runtimeRepository;

    @PInject
    public SelectionService selectionService;

    @PInject
    public HashGridService hashGridService;

    public void pick() {
        int x = (int) ((runtimeRepository.mouseX - runtimeRepository.viewportX) * (runtimeRepository.getDisplayW() / runtimeRepository.viewportW));
        int y = (int) ((runtimeRepository.getDisplayH() - runtimeRepository.mouseY - runtimeRepository.viewportY));

        getLogger().warn("X: {} Y: {} OX: {} OY: {}", x, y, runtimeRepository.mouseX, runtimeRepository.mouseY);
        FloatBuffer pixelBuffer = BufferUtils.createFloatBuffer(2);

        GL46.glBindFramebuffer(GL46.GL_READ_FRAMEBUFFER, coreBufferRepository.gBuffer.getFBO());
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
                selectionService.addSelected(transform.getEntityId());
                selectionService.stateRepository.primitiveSelected = transform;
            }
        } else if (!ImGui.isKeyDown(ImGuiKey.LeftCtrl)) {
            selectionService.clearSelection();
        }
    }

    private TransformationComponent findEntity(int actualIndex) {
        for (var tile : hashGridService.getLoadedTiles()) {
            if (tile != null) {
                Collection<MeshComponent> meshes = tile.getWorld().bagMeshComponent.values();
                for (var mesh : meshes) {
                    if (mesh.renderRequest != null && !mesh.renderRequest.isCulled && mesh.renderRequest.renderIndex == actualIndex) {
                        return tile.getWorld().bagTransformationComponent.get(mesh.getEntityId());
                    }
                }
            }
        }
        return null;
    }
}
