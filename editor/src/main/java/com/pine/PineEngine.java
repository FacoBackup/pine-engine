package com.pine;

import com.pine.app.EditorWindow;
import com.pine.service.WindowService;


public class PineEngine {
    public static void main(String[] args) {
        PInjector injector = new PInjector(PineEngine.class.getPackageName());
        var windowService = (WindowService) injector.getBean(WindowService.class);
        windowService.openWindow(EditorWindow.class);
    }
}
