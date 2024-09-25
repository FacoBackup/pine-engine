package com.pine.component.light;

import com.pine.PBean;
import com.pine.component.AbstractComponent;
import com.pine.component.EntityComponent;
import com.pine.component.TransformationComponent;
import com.pine.inspection.Color;
import com.pine.inspection.MutableField;
import com.pine.type.LightType;
import org.joml.Matrix4d;
import org.joml.Vector2f;

import java.nio.FloatBuffer;
import java.util.Set;

public abstract class AbstractLightComponent<T extends EntityComponent> extends AbstractComponent<T> {
    @MutableField(label="Screen Space Shadows")
    public boolean screenSpaceShadows = false;
    @MutableField(label = "SSS samples")
    public int shadowSamples = 3;
    @MutableField(label = "Fallout smoothing")
    public float smoothing = 0.5f;
    @MutableField(label = "Intensity")
    public int intensity = 1;
    @MutableField(label = "Color")
    public final Color color = new Color();

    public final void fillData(FloatBuffer dataBuffer, int offset, TransformationComponent transform){
       dataBuffer.put(offset, screenSpaceShadows ? 1 : 0);
       dataBuffer.put(offset + 1, shadowMap ? 1 : 0);
       dataBuffer.put(offset + 2, shadowBias);
       dataBuffer.put(offset + 3, shadowSamples);
       dataBuffer.put(offset + 4, zNear);
       dataBuffer.put(offset + 5, zFar);
       dataBuffer.put(offset + 6, cutoff);
       dataBuffer.put(offset + 7, shadowAttenuationMinDistance);
       dataBuffer.put(offset + 8, smoothing);
       dataBuffer.put(offset + 9, radius);
       dataBuffer.put(offset + 10, size);
       dataBuffer.put(offset + 11, areaRadius);
       dataBuffer.put(offset + 12, planeAreaWidth);
       dataBuffer.put(offset + 13, planeAreaHeight);
       dataBuffer.put(offset + 14, intensity);
       dataBuffer.put(offset + 15, type.getTypeId());
       dataBuffer.put(offset + 16, color.x);
       dataBuffer.put(offset + 17, color.y);
       dataBuffer.put(offset + 18, color.z);
       dataBuffer.put(offset + 19, transform.translation.x);
       dataBuffer.put(offset + 20, transform.translation.y);
       dataBuffer.put(offset + 21, transform.translation.z);
       dataBuffer.put(offset + 22, transform.rotation.x);
       dataBuffer.put(offset + 23, transform.rotation.y);
       dataBuffer.put(offset + 24, transform.rotation.z);
       dataBuffer.put(offset + 27, attenuation.x);
       dataBuffer.put(offset + 28, attenuation.y);
    }
    
    abstract void fillDataInternal(FloatBuffer dataBuffer, int offset);
}

