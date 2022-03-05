package parsing.exceptions.reportable;

import parsing.exceptions.ReportableException;

import java.lang.reflect.Type;

public class IllegalArgumentStringException extends ReportableException {

    public IllegalArgumentStringException(Type expected, String argumentString) {
        super("could not fit string \"" + argumentString + "\" into type " + expected.getTypeName());
    }

}
