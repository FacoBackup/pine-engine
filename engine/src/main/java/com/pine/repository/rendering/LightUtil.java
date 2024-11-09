package com.pine.repository.rendering;

import com.pine.repository.CameraRepository;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class LightUtil {

    private static final Vector3f RAYLEIGH_COEFFICIENTS = new Vector3f(5.5e-6f, 13.0e-6f, 22.4e-6f);
    private static final float MIE_COEFFICIENT = 0.002f;
    private static final float MIE_PHASE_G = 0.76f;
    private static final float PI = 3.14159265358979323846f;

    public static Vector3f computeSunlightColor(Vector3f sunDirection, Vector3f cameraPosition) {
        Vector3f normalizedSunDirection = new Vector3f(sunDirection).normalize();
        Vector3f viewDirection = new Vector3f(sunDirection).sub(cameraPosition).normalize();

        float cosTheta = viewDirection.dot(normalizedSunDirection);

        float rayleighPhase = 3.0f / (16.0f * PI) * (1.0f + cosTheta * cosTheta);
        Vector3f rayleighScattering = new Vector3f(
                RAYLEIGH_COEFFICIENTS.x * rayleighPhase,
                RAYLEIGH_COEFFICIENTS.y * rayleighPhase,
                RAYLEIGH_COEFFICIENTS.z * rayleighPhase
        );

        float miePhase = (1.0f - MIE_PHASE_G * MIE_PHASE_G) /
                (4.0f * PI * (float) Math.pow(1.0f + MIE_PHASE_G * MIE_PHASE_G - 2.0f * MIE_PHASE_G * cosTheta, 1.5f));
        Vector3f mieScattering = new Vector3f(MIE_COEFFICIENT * miePhase);

        float sunHeight = Math.max(0.0f, normalizedSunDirection.y);
        Vector3f sunlightColor = new Vector3f(
                (float) Math.exp(-rayleighScattering.x / sunHeight - mieScattering.x / sunHeight),
                (float) Math.exp(-rayleighScattering.y / sunHeight - mieScattering.y / sunHeight),
                (float) Math.exp(-rayleighScattering.z / sunHeight - mieScattering.z / sunHeight)
        );

        sunlightColor.set(
                (float) Math.pow(sunlightColor.x, 1.0 / 2.2),
                (float) Math.pow(sunlightColor.y, 1.0 / 2.2),
                (float) Math.pow(sunlightColor.z, 1.0 / 2.2)
        );

        return sunlightColor;
    }
}
