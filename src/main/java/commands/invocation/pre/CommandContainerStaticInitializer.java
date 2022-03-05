package commands.invocation.pre;

import java.lang.reflect.Field;

public class CommandContainerStaticInitializer {

    public static void InitializeStatic(Class<?> c) {
        for (Field field : c.getDeclaredFields()) {
            try {
                // assign to avoid optimizing out the instruction (?)
                Object x = field.get(null);
            } catch (IllegalAccessException accessException) {
                System.err.println("Non-public field found :");
                accessException.printStackTrace();
            }
        }
    }

}
