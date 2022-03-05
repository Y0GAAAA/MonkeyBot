package parsing;

import commands.invocation.MessageContext;
import discord4j.common.util.Snowflake;
import parsing.arguments.Sentence;
import parsing.exceptions.reportable.EmptyStringArgumentException;
import parsing.exceptions.reportable.IllegalArgumentStringException;
import parsing.exceptions.reportable.MismatchingArgumentCount;
import parsing.exceptions.internal.UnsupportedArgumentTypeException;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.function.Function;

public class DiscordCommandParser {
    
    public Object[] parseFunctionArgumentsFromCommandString(String argumentString, Parameter[] functionParameters)
    throws
           EmptyStringArgumentException,
           IllegalArgumentStringException,
           MismatchingArgumentCount
    {
        final int expectedArgumentCount = functionParameters.length;

        if (expectedArgumentCount == 0)
            return new Object[0];

        if (argumentString.isEmpty())
            throw new EmptyStringArgumentException(expectedArgumentCount);

        final Object[] objectifiedArgs = new Object[expectedArgumentCount];
        final String[] stringizedArgs = argumentString.split(" ");
    
        final int foundArgumentCount = stringizedArgs.length;
    
        if (foundArgumentCount == 0)
            throw new EmptyStringArgumentException(expectedArgumentCount);

        if (foundArgumentCount < expectedArgumentCount)
            throw new MismatchingArgumentCount(expectedArgumentCount, stringizedArgs.length);
        
        for (int i = 0; i < functionParameters.length; i++) {
            final Type t = functionParameters[i].getType();

            if (t.equals(Sentence.class)) {
                objectifiedArgs[i] = new Sentence(Arrays.copyOfRange(stringizedArgs, i, stringizedArgs.length));
                return objectifiedArgs;
            }
            
            objectifiedArgs[i] = ArgumentParser.parse(t, stringizedArgs[i]);
        }

        return objectifiedArgs;
    }

    private enum ParsableArgument {

        _SNOWFLAKE(Snowflake.class, Snowflake::of),
        _LONG(Long.class, Long::parseLong),
        _URL(URL.class, s -> {
            try {
                return new URL(s);
            } catch (MalformedURLException ignored) {
                return null;
            }
        }),
        _BOOLEAN(Boolean.class, Boolean::parseBoolean),
        _STRING(String.class, s -> s);
        
        private final Function<String, Object> converter;
        private final Class<?> _class;

        ParsableArgument(Class<?> c, Function<String, Object> converter) {
            this.converter = converter;
            this._class = c;
        }

        public Function<String, Object> getConverter() {
            return this.converter;
        }
        public Class<?> getArgumentClass() {
            return this._class;
        }

    }

    public static class ArgumentParser {

        private static final List<Class<?>> MANUALLY_PARSED_CLASSES = Arrays.asList(
                MessageContext.class,
                Sentence.class
        );
        
        private static final HashMap<Class<?>, Function<String, Object>> CONVERSION_MAP;

        static {
            ParsableArgument[] validArgumentTypes = ParsableArgument.values();
            CONVERSION_MAP = new HashMap<>(validArgumentTypes.length);
            
            for (ParsableArgument arg : validArgumentTypes) {
                CONVERSION_MAP.put(
                        arg.getArgumentClass(),
                        arg.getConverter()
                );
            }
        }

        public static Void validateMethod(Method method) throws UnsupportedArgumentTypeException {
            for (Parameter parameter : method.getParameters()) {
                Type type = parameter.getType();
                
                if (MANUALLY_PARSED_CLASSES.contains(type)) { // ignore types that are parsed manually
                    continue;
                }
                
                if (Objects.isNull(CONVERSION_MAP.get(type))) {
                    throw new UnsupportedArgumentTypeException((Class<?>)type);
                }
            }
            
            return null;
        }
        public static Object parse(Type expected, String value) throws IllegalArgumentStringException {
            Function<String, Object> converter = CONVERSION_MAP.get(expected);
            
            if (Objects.isNull(converter)) {
                throw new IllegalStateException("Unsupported type : " + expected.getTypeName());
            }

            try {
                Object conversionResult = converter.apply(value);

                if (Objects.nonNull(conversionResult)) {
                    return conversionResult;
                }

            } catch (Exception ignored) {}
            throw new IllegalArgumentStringException(expected, value);
        }

    }

}