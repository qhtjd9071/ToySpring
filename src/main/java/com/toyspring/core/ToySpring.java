package com.toyspring.core;

import com.toyspring.core.annotation.Autowired;
import com.toyspring.core.annotation.CommandMapping;
import com.toyspring.core.annotation.Controller;
import com.toyspring.core.annotation.Service;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class ToySpring {

    private static final Map<String, Object> controllerContainer = new ConcurrentHashMap<>();

    public static Map<String, Object> getControllerContainer() {
        return controllerContainer;
    }

    private static final Map<String, Object> serviceContainer = new ConcurrentHashMap<>();

    private static final Map<String, Object[]> methodContainer = new ConcurrentHashMap<>();

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

        initialSetting();

        System.out.println("=========== aookucatuib started ============");
        doRun();
    }

    private static void doRun() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {

        while ( true ) {
            Scanner sc= new Scanner(System.in);
            String query = sc.nextLine();
            String[] str = query.split("\\?");
            String cmd = str[0];
            String params = null;
            if (str.length != 1) {
                params = str[1];
            }

            if ( methodContainer.get(cmd) != null ) {
                Method method = (Method)methodContainer.get(cmd)[0];
                Object controller = methodContainer.get(cmd)[1];

                Parameter[] parameters = method.getParameters();
                Object[] objects = new Object[parameters.length];
                for (int i = 0 ; i < parameters.length ; i++) {
                    objects[i] = parameters[i].getType().getDeclaredConstructor().newInstance();

                    // mapping parameter
                    if (params != null) {
                        mappingParams(params, parameters, objects, i);
                    }
                }
                if (parameters.length == 0) {
                    method.invoke(controller);
                } else {
                    method.invoke(controller, objects);
                }
            } else {
                System.out.println("명령어가 없습니다.");
            };
        }

    }

    private static void mappingParams(String params, Parameter[] parameters, Object[] objects, int i) {
        String[] paramArr = params.split("&");
        for (String param : paramArr) {
            String[] keyValue = param.split("=");
            String key = keyValue[0];
            String value = keyValue[1];
            System.out.println("key : " + key + ", value : " + value);
            System.out.println(parameters[i].getName());
            if (key.equals(parameters[i].getName())) {
                objects[i] = value;
            }
        }
    }

    public static void initialSetting() throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
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

            Method[] methods = clz.getDeclaredMethods();
            for (Method method : methods) {
                CommandMapping mapping = method.getAnnotation(CommandMapping.class);
                if (mapping != null) {
                    methodContainer.put(mapping.value(), new Object[] {method, object});
                    System.out.println("command : " + mapping.value());
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
