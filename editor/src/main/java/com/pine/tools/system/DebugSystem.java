package com.pine.tools.system;

import com.pine.injection.PInject;
import com.pine.repository.SettingsRepository;
import com.pine.repository.rendering.RenderingMode;
import com.pine.repository.rendering.RenderingRequest;
import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.resource.shader.GLSLType;
import com.pine.service.resource.shader.UniformDTO;
import com.pine.service.system.AbstractSystem;
import com.pine.tools.repository.ToolsResourceRepository;
import com.pine.tools.types.DebugShadingModel;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class DebugSystem extends AbstractSystem {
}
