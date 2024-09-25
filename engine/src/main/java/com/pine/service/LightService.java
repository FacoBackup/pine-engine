package com.pine.service;

import com.pine.EngineUtils;
import com.pine.PBean;
import com.pine.PInject;
import com.pine.component.light.*;
import com.pine.repository.CameraRepository;
import com.pine.repository.CoreSSBORepository;
import com.pine.repository.RenderingRepository;
import com.pine.service.world.WorldService;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.nio.FloatBuffer;

import static com.pine.repository.CoreSSBORepository.MAX_INFO_PER_LIGHT;

@PBean
public class LightService {
    @PInject
    public WorldService worldService;
    @PInject
    public CameraRepository camera;
    @PInject
    public RenderingRepository renderingRepository;
    @PInject
    public CoreSSBORepository ssboRepository;
    @PInject
    public DirectionalLightComponent implDirectionalLightComponent;
    @PInject
    public PointLightComponent implPointLightComponent;
    @PInject
    public SphereLightComponent implSphereLightComponent;
    @PInject
    public SpotLightComponent implSpotLightComponent;

    public void packageLights() {
        int offset = 0;
        final Matrix4f cacheMat4 = new Matrix4f();
        final Matrix4f cacheMat42 = new Matrix4f();
        FloatBuffer b = ssboRepository.lightSSBOState;
        for (int i = 0; i < implDirectionalLightComponent.getBag().size(); i++) {
            var light = implDirectionalLightComponent.getBag().get(i);
            if (!light.isFrozen()) {
                var transform = worldService.getTransformationComponentUnchecked(light.getEntityId());
                int internalOffset = fillCommon(b, offset, light);

                b.put(internalOffset, light.atlasFace.x);
                b.put(internalOffset + 1, light.atlasFace.y);
                b.put(internalOffset + 2, light.shadowMap ? 0 : 1);
                b.put(internalOffset + 3, light.shadowBias);
                b.put(internalOffset + 4, light.shadowAttenuationMinDistance);

                if (light.shadowMap) {
                    var view = cacheMat4.lookAt(transform.translation, new Vector3f(0, 0, 0), new Vector3f(0, 1, 0));
                    var proj = cacheMat42.ortho(-light.size, light.size, -light.size, light.size, light.zNear, light.zFar);
                    proj.mul(view);
                    EngineUtils.copyWithOffset(b, proj, 16); // SECOND MATRIX
                }
            }
            light.freezeVersion();
            offset += light.type.getDataDisplacement();
        }

        for (int i = 0; i < implPointLightComponent.getBag().size(); i++) {
            var light = implPointLightComponent.getBag().get(i);
            if (!light.isFrozen()) {
                int internalOffset = fillCommon(b, offset, light);
                b.put(internalOffset, light.zFar);
                b.put(internalOffset + 1, light.shadowMap ? 1 : 0);
                b.put(internalOffset + 2, light.shadowAttenuationMinDistance);
                b.put(internalOffset + 3, light.shadowBias);

            }
            light.freezeVersion();
            offset += light.type.getDataDisplacement();
        }


        for (int i = 0; i < implSphereLightComponent.getBag().size(); i++) {
            var light = implSphereLightComponent.getBag().get(i);
            if (!light.isFrozen()) {
                int internalOffset = fillCommon(b, offset, light);
                b.put(internalOffset, light.areaRadius);
            }
            light.freezeVersion();
            offset += light.type.getDataDisplacement();
        }


        for (int i = 0; i < implSpotLightComponent.getBag().size(); i++) {
            var light = implSpotLightComponent.getBag().get(i);
            if (!light.isFrozen()) {
                var transform = worldService.getTransformationComponentUnchecked(light.getEntityId());
                int internalOffset = fillCommon(b, offset, light);

                cacheMat4.lookAt(transform.translation, transform.translation, new Vector3f(0, 1, 0));
                cacheMat4.identity();
                cacheMat42.rotate(new Quaternionf().rotateX(transform.rotation.x).rotateY(transform.rotation.y).rotateZ(transform.rotation.z));
                cacheMat4.mul(cacheMat42);

                b.put(internalOffset, cacheMat4.m20());
                b.put(internalOffset + 1, cacheMat4.m21());
                b.put(internalOffset + 2, cacheMat4.m22());
                b.put(internalOffset + 3, (float) Math.cos(Math.toRadians(light.radius)));
            }
            light.freezeVersion();
            offset += light.type.getDataDisplacement();
        }

        renderingRepository.lightCount = offset / MAX_INFO_PER_LIGHT;
    }

    private int fillCommon(FloatBuffer lightSSBOState, int offset, AbstractLightComponent<?> light) {
        var transform = worldService.getTransformationComponentUnchecked(light.getEntityId());

        lightSSBOState.put(offset, light.type.getTypeId());
        lightSSBOState.put(offset + 1, light.color.x);
        lightSSBOState.put(offset + 2, light.color.y);
        lightSSBOState.put(offset + 3, light.color.z);
        lightSSBOState.put(offset + 4, transform.translation.x);
        lightSSBOState.put(offset + 5, transform.translation.y);
        lightSSBOState.put(offset + 6, transform.translation.z);
        lightSSBOState.put(offset + 7, light.outerCutoff);
        lightSSBOState.put(offset + 8, light.attenuation.x);
        lightSSBOState.put(offset + 9, light.attenuation.y);
        lightSSBOState.put(offset + 10, light.sss ? 1 : 0);
        lightSSBOState.put(offset + 11, light.innerCutoff);

        return offset + 12;
    }

}
