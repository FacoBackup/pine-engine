package com.pine;

import com.pine.app.EditorWindow;
import com.pine.service.WindowService;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class App implements SmartInitializingSingleton {

    @Autowired
    private WindowService windowService;

    @Override
    public void afterSingletonsInstantiated() {
        windowService.openWindow(EditorWindow.class);
    }
}
