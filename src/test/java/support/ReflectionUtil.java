package support;

import java.lang.reflect.Field;

public class ReflectionUtil {

    public static <T> T getField(Object obj, String field, Class<T> type) {
        try {
            Field field1 = obj.getClass().getDeclaredField(field);
            field1.setAccessible(true);
            return (T) field1.get(obj);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }
}
