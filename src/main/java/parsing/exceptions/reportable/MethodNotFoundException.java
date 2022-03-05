package parsing.exceptions.reportable;

import parsing.exceptions.ReportableException;

public class MethodNotFoundException extends ReportableException {

    public MethodNotFoundException(String methodName) {
        super("no method found with name : " + methodName);
    }

}
