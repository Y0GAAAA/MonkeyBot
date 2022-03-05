package reflection;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Stream;

public class Util {

    public static Stream<Method> getMethodsFromClasses(Class<?>[] classes) {
        return Arrays.stream(classes)
                .map(Class::getDeclaredMethods)
                .flatMap(Stream::of);
    }

}