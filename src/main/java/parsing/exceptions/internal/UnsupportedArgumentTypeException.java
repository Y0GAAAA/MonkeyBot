package parsing.exceptions.internal;

public class UnsupportedArgumentTypeException extends Exception {

    public UnsupportedArgumentTypeException(Class<?> type) {
        super(type.getTypeName() + " is not supported as a callback function argument type");
    }

}