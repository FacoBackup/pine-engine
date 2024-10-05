
void computeTBN() {
    if (!hasTBNComputed) {
        hasTBNComputed = true;
        if (isDecalPass) {
            vec3 N = abs(normalVec);
            if (N.z > N.x && N.z > N.y)
            T = vec3(1., 0., 0.);
            else
            T = vec3(0., 0., 1.);
            T = normalize(T - N * dot(T, N));
            B = cross(T, N);
            TBN = mat3(T, B, N);
            return;
        }
        vec3 dp1 = dFdx(worldPosition);
        vec3 dp2 = dFdy(worldPosition);
        vec2 duv1 = dFdx(naturalTextureUV);
        vec2 duv2 = dFdy(naturalTextureUV);

        vec3 dp2perp = cross(dp2, naturalNormal);
        vec3 dp1perp = cross(naturalNormal, dp1);
        vec3 T = dp2perp * duv1.x + dp1perp * duv2.x;
        vec3 B = dp2perp * duv1.y + dp1perp * duv2.y;

        float invmax = inversesqrt(max(dot(T, T), dot(B, B)));
        TBN = mat3(T * invmax, B * invmax, naturalNormal);
    }
}
