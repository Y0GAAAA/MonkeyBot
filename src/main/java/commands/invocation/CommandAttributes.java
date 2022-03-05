package commands.invocation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class CommandAttributes {

    public enum SingleInstanceMode {
        Global,
        Guild,
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Command {
        String helpText();
    }

    /**
     * Annotation specifying that a method is single instanced.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface SingleInstance {
        SingleInstanceMode mode();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface AdministratorCommand {}
    
}
