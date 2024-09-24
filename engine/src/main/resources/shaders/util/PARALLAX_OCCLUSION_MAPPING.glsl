
vec2 parallaxOcclusionMapping(sampler2D heightMap, float heightScale, int layers) {
    if (distanceFromCamera > PARALLAX_THRESHOLD) return texCoords;
    computeTBN();
    mat3 transposed = transpose(TBN);
    if (!hasViewDirectionComputed) {
        viewDirection = normalize(transposed * (cameraPosition - worldSpacePosition.xyz));
        hasViewDirectionComputed = true;
    }
    float fLayers = float(max(layers, 1));
    float layerDepth = 1.0 / fLayers;
    float currentLayerDepth = 0.0;
    vec2 P = viewDirection.xy / viewDirection.z * max(heightScale, .00000001);
    vec2 deltaTexCoords = P / fLayers;

    vec2 currentUVs = texCoords;
    float currentDepthMapValue = texture(heightMap, currentUVs).r;
    while (currentLayerDepth < currentDepthMapValue) {
        currentUVs -= deltaTexCoords;
        currentDepthMapValue = texture(heightMap, currentUVs).r;
        currentLayerDepth += layerDepth;
    }

    vec2 prevTexCoords = currentUVs + deltaTexCoords;
    float afterDepth = currentDepthMapValue - currentLayerDepth;
    float beforeDepth = texture(heightMap, prevTexCoords).r - currentLayerDepth + layerDepth;


    float weight = afterDepth / (afterDepth - beforeDepth);
    vec2 finalTexCoords = prevTexCoords * weight + currentUVs * (1.0 - weight);

    return finalTexCoords;
}
