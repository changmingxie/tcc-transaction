package org.mengyun.tcctransaction.utils;

import org.mengyun.tcctransaction.support.FactoryBuilder;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by changmingxie on 11/22/15.
 */
public class ReflectionUtils {

    private static ConcurrentHashMap<Class, Class> cachedDeclaredClasses = new ConcurrentHashMap<>();

    private ReflectionUtils() {
    }

    public static void makeAccessible(Method method) {
        if ((!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers())) && !method.isAccessible()) {
            method.setAccessible(true);
        }
    }

    public static Object changeAnnotationValue(Annotation annotation, String key, Object newValue) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Object handler = Proxy.getInvocationHandler(annotation);
        Field f;
        f = handler.getClass().getDeclaredField("memberValues");
        f.setAccessible(true);
        Map<String, Object> memberValues;
        memberValues = (Map<String, Object>) f.get(handler);
        Object oldValue = memberValues.get(key);
        if (oldValue == null || oldValue.getClass() != newValue.getClass()) {
            throw new IllegalArgumentException();
        }
        memberValues.put(key, newValue);
        return oldValue;
    }

    public static Class getDeclaringType(Class targetClass, String methodName, Class<?>[] parameterTypes) {
        if (cachedDeclaredClasses.get(targetClass) == null) {
            Class foundClass = tryFindDeclaredClass(targetClass, methodName, parameterTypes);
            cachedDeclaredClasses.putIfAbsent(targetClass, foundClass);
        }
        return cachedDeclaredClasses.get(targetClass);
    }

    public static Object getNullValue(Class type) {
        if (boolean.class.equals(type)) {
            return false;
        } else if (byte.class.equals(type)) {
            return 0;
        } else if (short.class.equals(type)) {
            return 0;
        } else if (int.class.equals(type)) {
            return 0;
        } else if (long.class.equals(type)) {
            return 0;
        } else if (float.class.equals(type)) {
            return 0;
        } else if (double.class.equals(type)) {
            return 0;
        } else if (char.class.equals(type)) {
            return ' ';
        }
        return null;
    }

    private static Class tryFindDeclaredClass(Class aClass, String methodName, Class<?>[] parameterTypes) {
        Method method = null;
        Class findClass = aClass;
        do {
            Class[] clazzes = findClass.getInterfaces();
            for (Class clazz : clazzes) {
                try {
                    method = clazz.getDeclaredMethod(methodName, parameterTypes);
                } catch (NoSuchMethodException e) {
                    method = null;
                }
                if (method != null) {
                    //found the class
                    //try to get the target by class
                    Object target = null;
                    try {
                        target = FactoryBuilder.factoryOf(clazz).getInstance();
                    } catch (Exception e) {
                        target = null;
                    }
                    if (target == null) {
                        //if not get then use orignal class.
                        return aClass;
                    } else {
                        return clazz;
                    }
                }
            }
            findClass = findClass.getSuperclass();
        } while (!findClass.equals(Object.class));
        return aClass;
    }
}
