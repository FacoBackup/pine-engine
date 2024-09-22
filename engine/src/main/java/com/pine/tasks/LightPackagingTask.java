package com.pine.tasks;

import com.pine.PBean;
import com.pine.PInject;
import com.pine.component.LightComponent;
import com.pine.repository.CoreSSBORepository;
import com.pine.repository.RenderingRepository;

import java.util.Vector;

@PBean
public class LightPackagingTask extends AbstractTask {
    @PInject
    public LightComponent lights;

    @PInject
    public CoreSSBORepository ssboRepository;

    @PInject
    public RenderingRepository renderingRepository;

    @Override
    protected int getTickIntervalMilliseconds() {
        return 1000;
    }

    @Override
    protected void tickInternal() {
        Vector<LightComponent> bag = lights.getBag();
        int offset = 0;
        for (int i = 0; i < bag.size(); i++) {
            var light = bag.get(i);
            ssboRepository.lightSSBOState.put(i + offset, light.screenSpaceShadows ? 1 : 0);
            ssboRepository.lightSSBOState.put(i + offset + 1, light.shadowMap ? 1 : 0);
            ssboRepository.lightSSBOState.put(i + offset + 2, light.shadowBias);
            ssboRepository.lightSSBOState.put(i + offset + 3, light.shadowSamples);
            ssboRepository.lightSSBOState.put(i + offset + 4, light.zNear);
            ssboRepository.lightSSBOState.put(i + offset + 5, light.zFar);
            ssboRepository.lightSSBOState.put(i + offset + 6, light.cutoff);
            ssboRepository.lightSSBOState.put(i + offset + 7, light.shadowAttenuationMinDistance);
            ssboRepository.lightSSBOState.put(i + offset + 8, light.smoothing);
            ssboRepository.lightSSBOState.put(i + offset + 9, light.radius);
            ssboRepository.lightSSBOState.put(i + offset + 10, light.size);
            ssboRepository.lightSSBOState.put(i + offset + 11, light.areaRadius);
            ssboRepository.lightSSBOState.put(i + offset + 12, light.planeAreaWidth);
            ssboRepository.lightSSBOState.put(i + offset + 13, light.planeAreaHeight);
            ssboRepository.lightSSBOState.put(i + offset + 14, light.intensity);
            ssboRepository.lightSSBOState.put(i + offset + 15, light.type.getTypeId());
            ssboRepository.lightSSBOState.put(i + offset + 16, light.color.x);
            ssboRepository.lightSSBOState.put(i + offset + 17, light.color.y);
            ssboRepository.lightSSBOState.put(i + offset + 18, light.color.z);
            offset += 19;
        }
        renderingRepository.needsLightUpdate = true;
    }
}
