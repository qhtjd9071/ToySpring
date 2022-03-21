package com.toyspring.core;

import com.toyspring.application.MainApplication;
import com.toyspring.core.annotation.Autowired;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;

public class AutowiredTest {

    @Test
    @DisplayName("Autowired 어노테이션이 붙은 경우 자동으로 주입되는지 테스트")
    public void testAutowired() throws Exception {
        ToySpring.run(MainApplication.class);
        Map<String, Object> map = ToySpring.getControllerContainer();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object object = entry.getValue();
            Field[] fields = object.getClass().getDeclaredFields();
            for (Field field : fields) {
                Autowired autowired = field.getAnnotation(Autowired.class);
                if (autowired != null) {
                    System.out.println(field != null);
                }
            }
        }
    }
}
