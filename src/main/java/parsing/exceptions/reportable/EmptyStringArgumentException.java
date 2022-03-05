package parsing.exceptions.reportable;

import parsing.exceptions.ReportableException;

public class EmptyStringArgumentException extends ReportableException {

    public EmptyStringArgumentException(int expected) {
        super("expected " + expected + " arguments but found none");
    }

}