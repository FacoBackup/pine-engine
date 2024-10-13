package com.pine.injection;

import com.pine.messaging.Loggable;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class PInjector implements Loggable {
    private final List<Object> injectables = new ArrayList<>();
    private final String rootPackageName;

    private record ToInitialize(int order, Method method, Object in) {
    }

    /**
     * Scans classpath for EngineInjectable, injects dependencies and initializes them
     */
    public PInjector(String rootPackageName) {
        this.rootPackageName = rootPackageName;
    }

    private void initializeInjectables() {
        for (var in : injectables) {
            inject(in);
        }
        List<ToInitialize> toInitializeList = new ArrayList<>();
        for (var in : injectables) {
            Arrays.stream(in.getClass().getDeclaredMethods())
                    .filter(m -> m.isAnnotationPresent(PostCreation.class))
                    .findFirst().ifPresent(method -> toInitializeList.add(new ToInitialize(method.getAnnotation(PostCreation.class).order(), method, in)));
        }
        toInitializeList.sort(Comparator.comparingInt(ToInitialize::order));
        for(ToInitialize toInitialize : toInitializeList) {
            try{
                toInitialize.method().invoke(toInitialize.in);
            }catch (Exception ex){
                getLogger().error(ex.getMessage(), ex);
            }
        }
    }

    private void collectInjectables() {
        Reflections reflections = new Reflections(rootPackageName, Scanners.TypesAnnotated);
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(PBean.class);
        for (Class<?> clazz : annotatedClasses) {
            try {
                injectables.add(clazz.getConstructor().newInstance());
            } catch (Exception e) {
                getLogger().error("Failed to instantiate {}", clazz.getSimpleName(), e);
            }
        }
    }

    /**
     * Injects dependency found on list of injectables
     *
     * @param instance: object to be injected with EngineDependency
     */
    public void inject(Object instance) {
        Field[] fields = instance.getClass().getFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(PInject.class)) {
                try {
                    boolean isInjected;
                    Class<?> type = field.getType();
                    boolean isList = List.class.isAssignableFrom(type);
                    if (isList) {
                        isInjected = injectListField(instance, field);
                    } else {
                        isInjected = injectSimpleField(instance, field, type);
                    }

                    if (!isInjected) {
                        getLogger().error("{} dependency {} not injected", instance.getClass().getSimpleName(), type.getSimpleName());
                    }
                } catch (Exception e) {
                    getLogger().error("Could not inject dependency into {}", instance.getClass().getSimpleName(), e);
                }
            }
        }
    }

    private boolean injectListField(Object instance, Field field) throws ClassNotFoundException {
        List<Object> list = new ArrayList<>();
        Type genericType = field.getGenericType();
        Class<?> type;
        if (genericType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericType;
            Type[] typeArguments = parameterizedType.getActualTypeArguments();
            type = Class.forName(typeArguments[0].getTypeName());
        } else {
            return false;
        }

        for (Object i : injectables) {
            if (type.isAssignableFrom(i.getClass())) {
                list.add(i);
            }
        }

        return injectInternal(instance, field, list);
    }

    private boolean injectSimpleField(Object instance, Field field, Class<?> type) {
        boolean isInjected = false;
        for (Object i : injectables) {
            if (i.getClass() == type) {
                isInjected = injectInternal(instance, field, i);
                if (isInjected) {
                    break;
                }
            }
        }
        return isInjected;
    }

    private boolean injectInternal(Object instance, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(instance, value);
            return true;
        } catch (Exception e) {
            getLogger().error("Failed to inject dependency {} onto {}", field.getType().getSimpleName(), instance.getClass().getSimpleName());
            return false;
        }
    }

    public void addInjectables(List<Object> injectables) {
        this.injectables.addAll(injectables);
    }

    public Object getBean(Class<?> clazz) {
        return injectables.stream().filter(a -> a.getClass() == clazz).findFirst().orElse(null);
    }

    public void boot() {
        for (var injectable : injectables) {
            if (Disposable.class.isAssignableFrom(injectable.getClass())) {
                ((Disposable) injectable).dispose();
            }
        }
        injectables.clear();
        injectables.add(this);
        collectInjectables();
        initializeInjectables();
    }
}
