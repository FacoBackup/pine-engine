package com.jengine.jengine.window;

import com.jengine.jengine.window.core.AbstractWindow;
import com.jengine.jengine.window.core.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WindowService {

    @Autowired
    private WindowRepository windowRepository;

    public void addWindow(AbstractWindow app){
        launch(app);
        windowRepository.addWindow(app);
    }

    public void launch(final AbstractWindow app) {
        initialize(app);
        app.preRun();
        app.run();
        app.postRun();
        app.dispose();
    }

    private void initialize(final AbstractWindow app) {
        final Configuration config = new Configuration();
        app.configure(config);
        app.init(config);
    }
}
