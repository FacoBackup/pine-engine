package com.pine;

import com.pine.common.injection.PInjector;

public class PineEngine {
    private static final PInjector INJECTOR = new PInjector(PineEngine.class.getPackageName());

    public static void main(String[] args) {
        System.setProperty("org.lwjgl.util.NoChecks", "true");
        INJECTOR.boot();
        System.exit(0);
    }
}
