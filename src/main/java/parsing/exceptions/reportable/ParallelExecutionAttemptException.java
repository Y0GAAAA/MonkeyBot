package parsing.exceptions.reportable;

import parsing.exceptions.ReportableException;

import java.lang.reflect.Method;

public class ParallelExecutionAttemptException extends ReportableException {

    public ParallelExecutionAttemptException(Method method) {
        super(method.getName() + " is marked as single instanced and is already being ran somewhere else");
    }

}