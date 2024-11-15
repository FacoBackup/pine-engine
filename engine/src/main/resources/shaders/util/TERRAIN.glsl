vec3 computePosition(vec4 tilesScaleTranslation){
    const float VERTICES_PER_TILE = 6.0;
    const float TILES_PER_RUN = tilesScaleTranslation.x;
    const float VERTICES_PER_CHUNK = TILES_PER_RUN * TILES_PER_RUN * VERTICES_PER_TILE;

    float index = mod(float(gl_VertexID), VERTICES_PER_CHUNK);

    float zPos = mod(floor(index / VERTICES_PER_TILE), TILES_PER_RUN);
    float xPos = floor(index / (TILES_PER_RUN * VERTICES_PER_TILE));

    // Create a triangle
    int triangleID = int(mod(index, VERTICES_PER_TILE));
    if (triangleID == 1 || triangleID == 3 || triangleID == 4){
        zPos++;
    }
    if (triangleID == 2 || triangleID == 4 || triangleID == 5){
        xPos++;
    }

    vec3 position = vec3(xPos, 0.0, zPos) * tilesScaleTranslation.y;
    position.x += tilesScaleTranslation.z;
    position.z += tilesScaleTranslation.w;
    return position;
}