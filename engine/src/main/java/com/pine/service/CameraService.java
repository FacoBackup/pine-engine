package com.pine.service;

import com.pine.service.camera.Camera;

public interface CameraService {
    void handleKeyboard(Camera camera);

    void createViewMatrix(Camera camera);
}
