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

    private int fillCommon(FloatBuffer lightSSBOState, int offset, AbstractLightComponent light) {
        var transform = worldRepository.bagTransformationComponent.get(light.getEntityId());

        lightSSBOState.put(offset, light.type.getTypeId());
        offset++;
        lightSSBOState.put(offset, light.color.x * light.intensity);
        offset++;
        lightSSBOState.put(offset, light.color.y * light.intensity);
        offset++;
        lightSSBOState.put(offset, light.color.z * light.intensity);
        offset++;
        lightSSBOState.put(offset, transform.translation.x);
        offset++;
        lightSSBOState.put(offset, transform.translation.y);
        offset++;
        lightSSBOState.put(offset, transform.translation.z);
        offset++;
        lightSSBOState.put(offset, light.outerCutoff);
        offset++;
        lightSSBOState.put(offset, light.sss ? 1 : 0);
        offset++;
        lightSSBOState.put(offset, light.innerCutoff);
        offset++;

        return offset;
    }

}
