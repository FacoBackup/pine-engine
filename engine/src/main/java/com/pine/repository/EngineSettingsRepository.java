package com.pine.repository;

import com.pine.PBean;

@PBean
public class EngineSettingsRepository {
   public boolean fxaaEnabled = false;
   public float fxaaSpanMax = 8f;
   public float fxaaReduceMin =  1 / 128f;
   public float fxaaReduceMul =  1 / 8f;
   public boolean ssgiEnabled = false;
   public int ssgiBlurSamples = 5;
   public float ssgiBlurRadius = 5f;
   public float ssgiStepSize = 1f;
   public int ssgiMaxSteps = 4;
   public float ssgiStrength = 1f;
   public float ssrFalloff = 3f;
   public float ssrStepSize = 1f;
   public int ssrMaxSteps = 4;
   public float sssMaxDistance = .05f;
   public float sssDepthThickness = .05f;
   public float sssEdgeFalloff= 12f;
   public float sssDepthDelta = 0f;
   public int sssMaxSteps = 24;
   public boolean ssaoEnabled = false;
   public float ssaoFalloffDistance = 1000f;
   public float ssaoRadius = .25f;
   public float ssaoPower = 1f;
   public float ssaoBias =  .1f;
   public int ssaoBlurSamples = 2;
   public int ssaoMaxSamples = 64;
   public int physicsSubSteps = 10;
   public float physicsSimulationStep = 16.66666f;
   public int shadowAtlasQuantity = 4;
   public int shadowMapResolution = 4096;
   public boolean motionBlurEnabled  = false;
   public float motionBlurVelocityScale = 1f;
   public int motionBlurMaxSamples = 50;

   //CAMERA
   public boolean cameraMotionBlur = false;
   public boolean bloom = false;
   public boolean filmGrain = false;
   public boolean vignetteEnabled = false;
   public boolean chromaticAberration = false;
   public boolean distortion = false;
   public boolean DOF = false;
   public int size = 50;
   public int focusDistanceDOF = 10;
   public float apertureDOF = 1.2f;
   public int focalLengthDOF = 5;
   public int samplesDOF = 100;
   public float filmGrainStrength = 1.f;
   public float vignetteStrength = .25f;
   public float bloomThreshold = .75f;
   public int bloomQuality = 8;
   public int bloomOffset = 0;
   public float gamma = 2.2f;
   public float exposure = 1.f;
   public int chromaticAberrationStrength = 1;
   public int distortionStrength = 1;
}
