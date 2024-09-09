package com.pine.common;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;

@Service
public class ContextService {
    private static ApplicationContext context;

    @Autowired
    private ApplicationContext applicationContext;


    @PostConstruct
    private void init() {
        context = applicationContext;
    }

    public static void injectDependencies(Object instance) {
        if (context == null) {
            return;
        }

        Field[] fields = instance.getClass().getFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(InjectBean.class)) {
                Object dependency = context.getBean(field.getType());
                field.setAccessible(true);
                try {
                    field.set(instance, dependency);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to inject dependency", e);
                }
            }
        }
    }
}
