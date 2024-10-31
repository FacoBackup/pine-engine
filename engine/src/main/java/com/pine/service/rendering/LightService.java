package com.pine.service.rendering;

import com.pine.EngineUtils;
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
    private int offset = 0;
    private int count = 0;
    private final Matrix4f auxMat4 = new Matrix4f();
    private final Matrix4f aux2Mat4 = new Matrix4f();

    public void packageLights() {
        offset = 0;
        count = 0;
        FloatBuffer b = ssboRepository.lightSSBOState;
        for (var l : worldRepository.bagDirectionalLightComponent.values()) {
            packageDirectionalLight(l, b);
        }

        for (var l : worldRepository.bagPointLightComponent.values()) {
            packagePointLight(l, b);
        }

        for (var l : worldRepository.bagSphereLightComponent.values()) {
            packageSphereLight(l, b);
        }

        for (var l : worldRepository.bagSpotLightComponent.values()) {
            packageSpotLight(l, b);
        }
        renderingRepository.lightCount = count;
    }

    private void packageSpotLight(SpotLightComponent light, FloatBuffer b) {
        var transform = worldRepository.bagTransformationComponent.get(light.getEntityId());
        int internalOffset = fillCommon(b, offset, light);

        auxMat4.lookAt(transform.translation, transform.translation, new Vector3f(0, 1, 0));

        aux2Mat4.identity();
        aux2Mat4.rotate(transform.rotation);

        auxMat4.mul(aux2Mat4);

        b.put(internalOffset, auxMat4.m20());
        b.put(internalOffset + 1, auxMat4.m21());
        b.put(internalOffset + 2, auxMat4.m22());
        b.put(internalOffset + 3, (float) Math.cos(Math.toRadians(light.radius)));
        light.freezeVersion();
        offset += light.type.getDataDisplacement();
        count++;
    }

    private void packageSphereLight(SphereLightComponent l, FloatBuffer b) {
        int internalOffset = fillCommon(b, offset, l);
        b.put(internalOffset, l.areaRadius);

        offset += l.type.getDataDisplacement();
        count++;
    }

    private void packagePointLight(PointLightComponent l, FloatBuffer b) {
        int internalOffset = fillCommon(b, offset, l);
        b.put(internalOffset, l.zFar);
        b.put(internalOffset + 1, l.shadowMap ? 1 : 0);
        b.put(internalOffset + 2, l.shadowAttenuationMinDistance);
        b.put(internalOffset + 3, l.shadowBias);

        offset += l.type.getDataDisplacement();
        count++;
    }

    private void packageDirectionalLight(DirectionalLightComponent l, FloatBuffer b) {
        var transform = worldRepository.bagTransformationComponent.get(l.getEntityId());
        int internalOffset = fillCommon(b, offset, l);

        b.put(internalOffset, l.atlasFace.x);
        b.put(internalOffset + 1, l.atlasFace.y);
        b.put(internalOffset + 2, l.shadowMap ? 0 : 1);
        b.put(internalOffset + 3, l.shadowBias);
        b.put(internalOffset + 4, l.shadowAttenuationMinDistance);

        if (l.shadowMap) {
            var view = auxMat4.lookAt(transform.translation, new Vector3f(0, 0, 0), new Vector3f(0, 1, 0));
            var proj = aux2Mat4.ortho(-l.size, l.size, -l.size, l.size, l.zNear, l.zFar);
            proj.mul(view);
            EngineUtils.copyWithOffset(b, proj, 16); // SECOND MATRIX
        }

        offset += l.type.getDataDisplacement();
        count++;
    }

    private int fillCommon(FloatBuffer lightSSBOState, int offset, AbstractLightComponent light) {
        var transform = worldRepository.bagTransformationComponent.get(light.getEntityId());

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
