package com.toyspring.core;

import com.toyspring.core.annotation.Autowired;
import com.toyspring.core.annotation.Controller;
import com.toyspring.core.annotation.Service;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ToySpring {

    private static final Map<String, Object> controllerContainer = new ConcurrentHashMap<>();

    public static Map<String, Object> getControllerContainer() {
        return controllerContainer;
    }

    private static final Map<String, Object> serviceContainer = new ConcurrentHashMap<>();

    public static void run(Class<?> clazz) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException {
        Object object = clazz.getDeclaredConstructor().newInstance();
        String packagePath = object.getClass().getPackageName().replace(".", "/");

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        List<String> paths = new ArrayList<>();

        URL resource = classLoader.getResource(packagePath);
        assert resource != null;
        String decodedPath = URLDecoder.decode(resource.getPath(), StandardCharsets.UTF_8);
        File parentDir = new File(decodedPath);

        if (parentDir.isDirectory()) {
            findClasses(paths, parentDir);
        }

        for (int i = 0 ; i < paths.size() ; i++) {
            String path = paths.get(i);
            int prefixIdx = path.indexOf("classes" + File.separator) + "classes".length();
            path = path.substring(prefixIdx + 1).replace(File.separator, ".").replace(".class", "");
            paths.set(i, path);
        }

        System.out.println("paths : " + paths);
        scanBeans(paths);

        process();
    }

    public static void process() throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        for (Map.Entry<String, Object> entry : controllerContainer.entrySet()) {
            Class<?> clz = Class.forName(entry.getKey());
            Object object = clz.getDeclaredConstructor().newInstance();

            Field[] fields = clz.getDeclaredFields();
            for (Field field : fields) {
                Autowired autowired = field.getAnnotation(Autowired.class);
                if ( autowired != null)  {
                    field.setAccessible(true);
                    field.set(object, serviceContainer.get(field.getType().getName())  );//
                }
            }
        }

    }

    public static void scanBeans(List<String> paths) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        for (String path : paths) {
            Class<?> clz = Class.forName(path);
            Service service = clz.getAnnotation(Service.class);
            if ( service != null ) {
                serviceContainer.put(clz.getName(), clz.getDeclaredConstructor().newInstance());
            }

            Controller controller = clz.getAnnotation(Controller.class);
            if ( controller != null ) {
                controllerContainer.put(clz.getName(), clz.getDeclaredConstructor().newInstance());
            }
        }
    }

    private static void findClasses(List<String> paths, File directory) {
        if (!directory.exists()) {
            return;
        }
        File[] files = directory.listFiles();
        assert files != null;
        for (File file : files) {
            if (file.isDirectory()) {
                findClasses(paths, file);
            } else if (file.getName().endsWith(".class")) {
                paths.add(file.getAbsolutePath());
            }
        }
    }

}
