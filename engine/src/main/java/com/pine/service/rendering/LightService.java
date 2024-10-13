package com.pine.service.rendering;

import com.pine.EngineUtils;
import com.pine.component.ComponentType;
import com.pine.component.light.*;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.repository.CameraRepository;
import com.pine.repository.WorldRepository;
import com.pine.repository.core.CoreSSBORepository;
import com.pine.repository.rendering.RenderingRepository;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.nio.FloatBuffer;
import java.util.List;

@PBean
public class LightService {
    @PInject
    public CameraRepository camera;
    @PInject
    public RenderingRepository renderingRepository;
    @PInject
    public CoreSSBORepository ssboRepository;
    @PInject
    public WorldRepository worldRepository;


    public void packageLights() {
        int offset = 0;
        int count = 0;
        final Matrix4f cacheMat4 = new Matrix4f();
        final Matrix4f cacheMat42 = new Matrix4f();
        FloatBuffer b = ssboRepository.lightSSBOState;

        List<DirectionalLightComponent> directionalLights = worldRepository.getComponentBag(ComponentType.DIRECTIONAL_LIGHT);
        for (DirectionalLightComponent light : directionalLights) {
            if (light.isNotFrozen()) {
                var transform = light.entity.transformation;
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
            count++;
        }

        List<PointLightComponent> pointLights = worldRepository.getComponentBag(ComponentType.POINT_LIGHT);
        for (PointLightComponent light : pointLights) {
            if (light.isNotFrozen()) {
                int internalOffset = fillCommon(b, offset, light);
                b.put(internalOffset, light.zFar);
                b.put(internalOffset + 1, light.shadowMap ? 1 : 0);
                b.put(internalOffset + 2, light.shadowAttenuationMinDistance);
                b.put(internalOffset + 3, light.shadowBias);

            }
            light.freezeVersion();
            offset += light.type.getDataDisplacement();
            count++;
        }


        List<SphereLightComponent> sphereLights = worldRepository.getComponentBag(ComponentType.SPHERE_LIGHT);
        for (SphereLightComponent light : sphereLights) {
            if (light.isNotFrozen()) {
                int internalOffset = fillCommon(b, offset, light);
                b.put(internalOffset, light.areaRadius);
            }
            light.freezeVersion();
            offset += light.type.getDataDisplacement();
            count++;
        }

        List<SpotLightComponent> spotLights = worldRepository.getComponentBag(ComponentType.SPOT_LIGHT);
        for (SpotLightComponent light : spotLights) {
            if (light.isNotFrozen()) {
                var transform = light.entity.transformation;
                int internalOffset = fillCommon(b, offset, light);

                cacheMat4.lookAt(transform.translation, transform.translation, new Vector3f(0, 1, 0));
                cacheMat4.identity();
                cacheMat42.rotate(transform.rotation);
                cacheMat4.mul(cacheMat42);

                b.put(internalOffset, cacheMat4.m20());
                b.put(internalOffset + 1, cacheMat4.m21());
                b.put(internalOffset + 2, cacheMat4.m22());
                b.put(internalOffset + 3, (float) Math.cos(Math.toRadians(light.radius)));
            }
            light.freezeVersion();
            offset += light.type.getDataDisplacement();
            count++;
        }

        renderingRepository.lightCount = count;
    }

    private int fillCommon(FloatBuffer lightSSBOState, int offset, AbstractLightComponent light) {
        var transform = light.entity.transformation;

        lightSSBOState.put(offset, light.type.getTypeId());
        lightSSBOState.put(offset + 1, light.color.x * light.intensity);
        lightSSBOState.put(offset + 2, light.color.y * light.intensity);
        lightSSBOState.put(offset + 3, light.color.z * light.intensity);
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
