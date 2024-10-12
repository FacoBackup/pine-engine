vec3 packValues(
    float anisotropicRotation,
    float anisotropy,
    float clearCoat,
    float sheen,
    float sheenTint,
    int renderingMode,
    bool ssrEnabled
) {
    // Map the float inputs to the range of 16-bit unsigned integers (0 to 65535)
    uint anisotropicRotationInt = uint(clamp(anisotropicRotation, 0.0, 1.0) * 65535.0);
    uint anisotropyInt = uint(clamp(anisotropy, 0.0, 1.0) * 65535.0);
    uint clearCoatInt = uint(clamp(clearCoat, 0.0, 1.0) * 65535.0);
    uint sheenInt = uint(clamp(sheen, 0.0, 1.0) * 65535.0);
    uint sheenTintInt = uint(clamp(sheenTint, 0.0, 1.0) * 65535.0);

    // Pack the anisotropicRotation and anisotropy into X
    uint xPacked = (anisotropicRotationInt << 16) | anisotropyInt;

    // Pack the clearCoat and sheen into Y
    uint yPacked = (clearCoatInt << 16) | sheenInt;

    // Pack the sheenTint, renderingMode, and ssrEnabled into Z
    uint zPacked = (sheenTintInt << 16) | (uint(renderingMode & 0x7FFF) << 1) | uint(ssrEnabled ? 1 : 0);

    // Convert packed uints to floats for vec3
    return vec3(
        float(xPacked) / 4294967295.0, // Normalize to range [0.0, 1.0]
        float(yPacked) / 4294967295.0, // Normalize to range [0.0, 1.0]
        float(zPacked) / 4294967295.0// Normalize to range [0.0, 1.0]
    );
}


void unpackValues(
    vec3 packedData,
    out float anisotropicRotation,
    out float anisotropy,
    out float clearCoat,
    out float sheen,
    out float sheenTint,
    out int renderingMode,
    out bool ssrEnabled
) {
    uint mask = uint(0xFFFF);
    uint mask1 = uint(0x7FFF);
    // Convert vec3 back to 32-bit uints
    uint xPacked = uint(packedData.x * 4294967295.0);
    uint yPacked = uint(packedData.y * 4294967295.0);
    uint zPacked = uint(packedData.z * 4294967295.0);

    // Unpack X channel (anisotropicRotation and anisotropy)
    uint anisotropicRotationInt = (xPacked >> 16) & mask;
    uint anisotropyInt = xPacked & mask;

    // Unpack Y channel (clearCoat and sheen)
    uint clearCoatInt = (yPacked >> 16) & mask;
    uint sheenInt = yPacked & mask;

    // Unpack Z channel (sheenTint, renderingMode, ssrEnabled)
    uint sheenTintInt = (zPacked >> 16) & mask;
    uint renderingModeInt = (zPacked >> 1) & mask1;
    bool ssrEnabledBool = (zPacked & uint(0x1)) != 0;

    // Convert the 16-bit integers back to float values in the range [0.0, 1.0]
    anisotropicRotation = float(anisotropicRotationInt) / 65535.0;
    anisotropy = float(anisotropyInt) / 65535.0;
    clearCoat = float(clearCoatInt) / 65535.0;
    sheen = float(sheenInt) / 65535.0;
    sheenTint = float(sheenTintInt) / 65535.0;

    // Set the integer and boolean outputs for renderingMode and ssrEnabled
    renderingMode = int(renderingModeInt);
    ssrEnabled = ssrEnabledBool;
}
